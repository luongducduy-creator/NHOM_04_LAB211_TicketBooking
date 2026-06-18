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
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
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
}
