package controller;

import model.transaction.Transaction;
import model.seat.Seat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Simulates concurrent seat‑booking operations using {@link CountDownLatch}
 * and an {@link ExecutorService}. Each worker thread attempts to book a seat
 * via
 * {@link BookingController#bookSeat(String, String, String, Transaction.PaymentMethod)}.
 * The outcomes are collected and can be rendered by {@link view.SimulatorView}.
 */
public class SimulatorController {

    public enum SyncMechanism {
        FILE_LOCK,
        SYNCHRONIZED,
        OPTIMISTIC
    }

    private final BookingController bookingController;

    public SimulatorController(BookingController bookingController) {
        this.bookingController = bookingController;
    }

    /**
     * Runs a booking simulation using the default synchronized strategy.
     */
    public List<BookingResult> runSimulation(List<String> fanIds,
            String matchId,
            List<String> seatIds,
            int threads) {
        return runSimulation(fanIds, matchId, seatIds, threads, SyncMechanism.SYNCHRONIZED);
    }

    /**
     * Runs a booking simulation with the chosen synchronization mechanism.
     */
    public List<BookingResult> runSimulation(List<String> fanIds,
            String matchId,
            List<String> seatIds,
            int threads,
            SyncMechanism mechanism) {
        if (fanIds == null || seatIds == null || fanIds.size() != seatIds.size()) {
            throw new IllegalArgumentException("fanIds and seatIds must be non‑null and of equal length");
        }
        int tasks = fanIds.size();
        CountDownLatch latch = new CountDownLatch(tasks);
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        List<BookingResult> results = new ArrayList<>();
        Object lock = new Object();
        for (int i = 0; i < tasks; i++) {
            final int idx = i;
            executor.submit(() -> {
                String fanId = fanIds.get(idx);
                String seatId = seatIds.get(idx);
                try {
                    Transaction tx = executeBooking(mechanism, fanId, matchId, seatId);
                    BookingResult res;
                    if (tx != null) {
                        res = new BookingResult(fanId, matchId, seatId, true, tx.getTransactionId());
                    } else {
                        res = new BookingResult(fanId, matchId, seatId, false, null);
                    }
                    synchronized (lock) {
                        results.add(res);
                    }
                } catch (Exception e) {
                    synchronized (lock) {
                        results.add(new BookingResult(fanId, matchId, seatId, false, null, e.getMessage()));
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        executor.shutdown();
        return results;
    }

    private Transaction executeBooking(SyncMechanism mechanism, String fanId, String matchId, String seatId)
            throws Exception {
        switch (mechanism) {
            case FILE_LOCK:
                return bookWithFileLock(fanId, matchId, seatId);
            case OPTIMISTIC:
                return bookWithOptimisticRetry(fanId, matchId, seatId);
            case SYNCHRONIZED:
                return bookWithSynchronizedLock(fanId, matchId, seatId);
            default:
                throw new IllegalArgumentException("Unknown mechanism: " + mechanism);
        }
    }

    private Transaction bookWithSynchronizedLock(String fanId, String matchId, String seatId) {
        try {
            return bookingController.withSynchronizedBooking(
                    () -> bookingController.bookSeat(fanId, matchId, seatId,
                            Transaction.PaymentMethod.CASH));
        } catch (Exception e) {
            throw new IllegalStateException("Synchronized booking failed", e);
        }
    }

    private Transaction bookWithFileLock(String fanId, String matchId, String seatId) throws Exception {
        return bookingController.withFileLockedBooking(
                () -> bookingController.bookSeat(fanId, matchId, seatId,
                        Transaction.PaymentMethod.CASH));
    }

    private Transaction bookWithOptimisticRetry(String fanId, String matchId, String seatId) throws Exception {
        int maxAttempts = 8;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            Seat snapshot = bookingController.getSeatSnapshot(seatId);
            if (snapshot == null || !"AVAILABLE".equalsIgnoreCase(snapshot.getStatus())) {
                return null;
            }

            Transaction tx = bookingController.bookSeatOptimistic(
                    fanId, matchId, seatId, Transaction.PaymentMethod.CASH,
                    snapshot.getVersion());
            if (tx != null) {
                return tx;
            }

            Seat latest = bookingController.getSeatSnapshot(seatId);
            if (latest == null || !"AVAILABLE".equalsIgnoreCase(latest.getStatus())) {
                return null;
            }
            if (attempt < maxAttempts) {
                Thread.sleep(25L * attempt);
            }
        }
        return null;
    }

    /** Simple DTO that captures the outcome of a single booking attempt. */
    public static class BookingResult {
        public final String fanId;
        public final String matchId;
        public final String seatId;
        public final boolean success;
        public final String transactionId;
        public final String errorMessage;

        public BookingResult(String fanId, String matchId, String seatId,
                boolean success, String transactionId) {
            this(fanId, matchId, seatId, success, transactionId, null);
        }

        public BookingResult(String fanId, String matchId, String seatId,
                boolean success, String transactionId, String errorMessage) {
            this.fanId = fanId;
            this.matchId = matchId;
            this.seatId = seatId;
            this.success = success;
            this.transactionId = transactionId;
            this.errorMessage = errorMessage;
        }

        public boolean success() {
            return success;
        }
    }
}
