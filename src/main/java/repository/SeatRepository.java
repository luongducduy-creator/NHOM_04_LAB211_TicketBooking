package repository;

import model.seat.Seat;
import java.io.*;
import java.util.*;

public class SeatRepository {
    private final String filePath;

    public SeatRepository(String filePath) {
        this.filePath = filePath;
    }

    public void saveAll(List<Seat> seats) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Seat seat : seats) {
                writer.write(seat.toCsvLine());
                writer.newLine();
            }
        }
    }

    public List<Seat> findAll() throws IOException {
        List<Seat> seats = new ArrayList<>();

        File file = new File(filePath);
        if (!file.exists()) {
            return seats;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                seats.add(Seat.fromCsvLine(line));
            }
        }
        return seats;
    }

    public void addSeat(Seat seat) throws IOException {
        List<Seat> seats = findAll();
        seats.add(seat);
        saveAll(seats);
    }

    public void deleteSeat(String seatId) throws IOException {
        List<Seat> seats = findAll();
        seats.removeIf(s -> s.getSeatId().equals(seatId));
        saveAll(seats);
    }

    public void updateSeat(Seat updated) throws IOException {
        List<Seat> seats = findAll();
        for (int i = 0; i < seats.size(); i++) {
            if (seats.get(i).getSeatId().equals(updated.getSeatId())) {
                seats.set(i, updated);
            }
        }
        saveAll(seats);
    }

    public void autoRenumberSeats() throws IOException {
        List<Seat> seats = findAll();
        if (seats.isEmpty())
            return;

        Seat header = null;
        if (seats.get(0).getSeatId().equalsIgnoreCase("seatId")) {
            header = seats.remove(0);
        }

        // Group by (sectionId, row)
        Map<String, Map<String, List<Seat>>> grouped = new LinkedHashMap<>();
        for (Seat seat : seats) {
            grouped.computeIfAbsent(seat.getSectionId(), k -> new LinkedHashMap<>())
                    .computeIfAbsent(seat.getRow(), k -> new ArrayList<>())
                    .add(seat);
        }

        // Renumber seats within each group
        for (Map<String, List<Seat>> rows : grouped.values()) {
            for (List<Seat> rowSeats : rows.values()) {
                rowSeats.sort(Comparator.comparingInt(s -> {
                    try {
                        return Integer.parseInt(s.getNumber());
                    } catch (Exception e) {
                        try {
                            return Integer.parseInt(s.getSeatId().replaceAll("[^0-9]", ""));
                        } catch (Exception ex) {
                            return 0;
                        }
                    }
                }));

                for (int i = 0; i < rowSeats.size(); i++) {
                    rowSeats.get(i).setNumber(String.valueOf(i + 1));
                }
            }
        }

        if (header != null) {
            seats.add(0, header);
        }
        saveAll(seats);
    }
}
