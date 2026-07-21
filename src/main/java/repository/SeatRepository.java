package repository;

import model.seat.Seat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Repository for seats.csv. Synchronization is intentionally implemented here
 * so Controller and View never manipulate CSV files directly.
 */
public class SeatRepository {
    public enum OptimisticUpdateResult {
        SUCCESS,
        VERSION_CONFLICT,
        NOT_AVAILABLE,
        NOT_FOUND
    }

    private static final String HEADER = "seatId,sectionId,row,number,status,version";
    private static final Map<Path, Object> JVM_LOCKS = new ConcurrentHashMap<>();

    private final Path filePath;
    private final Path bookingLockPath;
    private final Object jvmLock;

    public SeatRepository(String filePath) {
        this.filePath = Path.of(filePath).toAbsolutePath().normalize();
        Path parent = this.filePath.getParent();
        this.bookingLockPath = parent.resolve("booking.lock");
        this.jvmLock = JVM_LOCKS.computeIfAbsent(this.filePath, ignored -> new Object());
    }

    /**
     * Writes through a temporary file so readers never observe a half-written CSV.
     */
    public void saveAll(List<Seat> seats) throws IOException {
        synchronized (jvmLock) {
            saveAllUnlocked(seats);
        }
    }

    private void saveAllUnlocked(List<Seat> seats) throws IOException {
        Path parent = filePath.getParent();
        Files.createDirectories(parent);
        Path tempFile = Files.createTempFile(parent, "seats-", ".tmp");
        boolean moved = false;
        try (BufferedWriter writer = Files.newBufferedWriter(tempFile)) {
            writer.write(HEADER);
            writer.newLine();
            for (Seat seat : seats) {
                if (seat == null || "seatId".equalsIgnoreCase(seat.getSeatId())) {
                    continue;
                }
                writer.write(seat.toCsvLine());
                writer.newLine();
            }
        }

        try {
            try {
                Files.move(tempFile, filePath, StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException ignored) {
                Files.move(tempFile, filePath, StandardCopyOption.REPLACE_EXISTING);
            }
            moved = true;
        } finally {
            if (!moved) {
                Files.deleteIfExists(tempFile);
            }
        }
    }

    public List<Seat> findAll() throws IOException {
        synchronized (jvmLock) {
            return findAllUnlocked();
        }
    }

    private List<Seat> findAllUnlocked() throws IOException {
        List<Seat> seats = new ArrayList<>();
        if (!Files.exists(filePath)) {
            return seats;
        }

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
<<<<<<< HEAD
                if (line.isBlank() || line.startsWith("seatId,")) {
                    continue;
                }
                seats.add(Seat.fromCsvLine(line));
=======
                Seat seat = Seat.fromCsvLine(line);
                if (seat != null) {  // skip header, dòng trống, dòng lỗi
                    seats.add(seat);
                }
>>>>>>> 8a2da6f7cccfcdcd82716edfea420f10115f7e5c
            }
        }
        return seats;
    }

    public Seat findById(String seatId) throws IOException {
        for (Seat seat : findAll()) {
            if (seat.getSeatId().equalsIgnoreCase(seatId)) {
                return seat;
            }
        }
        return null;
    }

    /**
     * Baseline read-check-write with no transaction-level lock. It is kept for
     * the NO_LOCK experiment and may exhibit double booking under contention.
     */
    public boolean tryBookNoLock(String seatId) throws IOException {
        List<Seat> seats = findAll();
        Seat target = findSeat(seats, seatId);
        if (target == null || !isAvailable(target)) {
            return false;
        }
        target.setStatus("BOOKED");
        target.setVersion(target.getVersion() + 1);
        saveAll(seats);
        return true;
    }

    /**
     * Optimistic compare-and-set. Only the small version check and CSV replace
     * are synchronized; callers are free to read and do other work concurrently.
     */
    public OptimisticUpdateResult tryBookOptimistic(String seatId, int expectedVersion) throws IOException {
        synchronized (jvmLock) {
            List<Seat> seats = findAllUnlocked();
            Seat target = findSeat(seats, seatId);
            if (target == null) {
                return OptimisticUpdateResult.NOT_FOUND;
            }
            if (target.getVersion() != expectedVersion) {
                return OptimisticUpdateResult.VERSION_CONFLICT;
            }
            if (!isAvailable(target)) {
                return OptimisticUpdateResult.NOT_AVAILABLE;
            }
            target.setStatus("BOOKED");
            target.setVersion(target.getVersion() + 1);
            saveAllUnlocked(seats);
            return OptimisticUpdateResult.SUCCESS;
        }
    }

    /**
     * Executes the complete booking transaction under the repository's JVM lock.
     */
    public <T> T withSynchronizedBooking(Callable<T> bookingAction) throws Exception {
        synchronized (jvmLock) {
            return bookingAction.call();
        }
    }

    /**
     * Executes the complete booking transaction while holding a Java NIO file
     * lock. tryLock + retry avoids OverlappingFileLockException between threads
     * in the same JVM while still providing a real inter-process file lock.
     */
    public <T> T withFileLockedBooking(Callable<T> bookingAction) throws Exception {
        Files.createDirectories(bookingLockPath.getParent());
        long deadline = System.nanoTime() + 5_000_000_000L;
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(bookingLockPath.toFile(), "rw");
                FileChannel channel = randomAccessFile.getChannel()) {
            FileLock fileLock = null;
            while (fileLock == null) {
                try {
                    fileLock = channel.tryLock();
                } catch (OverlappingFileLockException ignored) {
                    // Another thread in this JVM currently owns the file lock.
                }
                if (fileLock == null) {
                    if (System.nanoTime() >= deadline) {
                        throw new IOException("Timed out waiting for booking file lock");
                    }
                    Thread.sleep(5L);
                }
            }
            try (FileLock ignored = fileLock) {
                return bookingAction.call();
            }
        }
    }

    public boolean releaseSeat(String seatId) throws IOException {
        synchronized (jvmLock) {
            List<Seat> seats = findAllUnlocked();
            Seat target = findSeat(seats, seatId);
            if (target == null) {
                return false;
            }
            target.setStatus("AVAILABLE");
            target.setVersion(target.getVersion() + 1);
            saveAllUnlocked(seats);
            return true;
        }
    }

    public void addSeat(Seat seat) throws IOException {
        synchronized (jvmLock) {
            List<Seat> seats = findAllUnlocked();
            seats.add(seat);
            saveAllUnlocked(seats);
        }
    }

    public void deleteSeat(String seatId) throws IOException {
        synchronized (jvmLock) {
            List<Seat> seats = findAllUnlocked();
            seats.removeIf(s -> s.getSeatId().equalsIgnoreCase(seatId));
            saveAllUnlocked(seats);
        }
    }

    public void updateSeat(Seat updated) throws IOException {
        synchronized (jvmLock) {
            List<Seat> seats = findAllUnlocked();
            for (int i = 0; i < seats.size(); i++) {
                if (seats.get(i).getSeatId().equalsIgnoreCase(updated.getSeatId())) {
                    seats.set(i, updated);
                    break;
                }
            }
            saveAllUnlocked(seats);
        }
    }

    public void autoRenumberSeats() throws IOException {
        synchronized (jvmLock) {
            List<Seat> seats = findAllUnlocked();
            if (seats.isEmpty()) {
                return;
            }

            Map<String, Map<String, List<Seat>>> grouped = new LinkedHashMap<>();
            for (Seat seat : seats) {
                grouped.computeIfAbsent(seat.getSectionId(), key -> new LinkedHashMap<>())
                        .computeIfAbsent(seat.getRow(), key -> new ArrayList<>())
                        .add(seat);
            }

            for (Map<String, List<Seat>> rows : grouped.values()) {
                for (List<Seat> rowSeats : rows.values()) {
                    rowSeats.sort(Comparator.comparingInt(SeatRepository::seatNumber));
                    for (int i = 0; i < rowSeats.size(); i++) {
                        rowSeats.get(i).setNumber(String.valueOf(i + 1));
                    }
                }
            }
            saveAllUnlocked(seats);
        }
    }

    private static Seat findSeat(List<Seat> seats, String seatId) {
        for (Seat seat : seats) {
            if (seat.getSeatId().equalsIgnoreCase(seatId)) {
                return seat;
            }
        }
        return null;
    }

    private static boolean isAvailable(Seat seat) {
        return "AVAILABLE".equalsIgnoreCase(seat.getStatus());
    }

    private static int seatNumber(Seat seat) {
        try {
            return Integer.parseInt(seat.getNumber());
        } catch (NumberFormatException ignored) {
            String digits = seat.getSeatId().replaceAll("[^0-9]", "");
            return digits.isEmpty() ? 0 : Integer.parseInt(digits);
        }
    }
}
