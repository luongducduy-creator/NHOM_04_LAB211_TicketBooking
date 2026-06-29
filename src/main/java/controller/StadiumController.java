package controller;

import model.Stadium;
import model.match.Match;
import model.seat.Seat;
import model.seat.Section;
import repository.MatchRepository;
import repository.SeatRepository;
import repository.SectionRepository;
import repository.StadiumRepository;

import java.io.IOException;
import java.util.*;

/**
 * T5 – StadiumController
 * Handles: getAllStadiums, getSections, buildSeatMap (ASCII map data)
 */
public class StadiumController {

    private final StadiumRepository stadiumRepo;
    private final SectionRepository sectionRepo;
    private final SeatRepository    seatRepo;
    private final MatchRepository   matchRepo;

    public StadiumController() {
        this.stadiumRepo = new StadiumRepository();
        this.sectionRepo = new SectionRepository();
        this.seatRepo    = new SeatRepository(System.getProperty("user.dir") + "/data/seats.csv");
        this.matchRepo   = new MatchRepository();
        try {
            this.seatRepo.autoRenumberSeats();
        } catch (IOException e) {
            System.out.println("[ERROR] Failed to auto-renumber seats: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────
    //  STADIUMS
    // ─────────────────────────────────────────────
    public List<Stadium> getAllStadiums() {
        return stadiumRepo.findAll();
    }

    public Stadium getStadiumById(String stadiumId) {
        return stadiumRepo.findById(stadiumId);
    }

    // ─────────────────────────────────────────────
    //  MATCHES
    // ─────────────────────────────────────────────
    public List<Match> getAllMatches() {
        return matchRepo.findAll();
    }

    public Match getMatchById(String matchId) {
        return matchRepo.findById(matchId);
    }

    // ─────────────────────────────────────────────
    //  SECTIONS
    // ─────────────────────────────────────────────
    /**
     * Get all sections belonging to a stadium.
     */
    public List<Section> getSections(String stadiumId) {
        return sectionRepo.findByStadium(stadiumId);
    }

    public Section getSectionById(String sectionId) {
        return sectionRepo.findById(sectionId);
    }

    // ─────────────────────────────────────────────
    //  SEAT MAP
    // ─────────────────────────────────────────────
    /**
     * Build a seat map for a section: Map<rowLabel, List<Seat>> sorted by row+number.
     * This is the data model that SeatMapView renders as ASCII.
     */
    public Map<String, List<Seat>> buildSeatMap(String sectionId) {
        List<Seat> allSeats;
        try {
            allSeats = seatRepo.findAll();
        } catch (IOException e) {
            System.out.println("[ERROR] Cannot load seats: " + e.getMessage());
            return Collections.emptyMap();
        }

        // Filter by section and group by row
        Map<String, List<Seat>> seatMap = new TreeMap<>(Comparator.comparingInt(StadiumController::rowOrder));
        for (Seat seat : allSeats) {
            if (seat.getSectionId().equalsIgnoreCase(sectionId)) {
                String row = seat.getRow();
                seatMap.computeIfAbsent(row, k -> new ArrayList<>()).add(seat);
            }
        }

        // Sort seats within each row by seat number
        for (List<Seat> row : seatMap.values()) {
            row.sort(Comparator.comparingInt(s -> {
                try { return Integer.parseInt(s.getNumber()); } catch (Exception e) { return 0; }
            }));
        }

        return seatMap;
    }

    /**
     * Get all available seats in a section (status = AVAILABLE).
     */
    public List<Seat> getAvailableSeats(String sectionId) {
        List<Seat> result = new ArrayList<>();
        try {
            for (Seat s : seatRepo.findAll()) {
                if (s.getSectionId().equalsIgnoreCase(sectionId)
                        && "AVAILABLE".equalsIgnoreCase(s.getStatus())) {
                    result.add(s);
                }
            }
        } catch (IOException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
        return result;
    }

    /**
     * Find a seat by its ID.
     */
    public Seat getSeatById(String seatId) {
        try {
            return seatRepo.findAll().stream()
                    .filter(s -> s.getSeatId().equalsIgnoreCase(seatId))
                    .findFirst().orElse(null);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Mark seat as BOOKED and persist.
     */
    public boolean markSeatBooked(String seatId) {
        try {
            List<Seat> seats = seatRepo.findAll();
            for (Seat s : seats) {
                if (s.getSeatId().equalsIgnoreCase(seatId)) {
                    if (!"AVAILABLE".equalsIgnoreCase(s.getStatus())) {
                        return false; // Already taken
                    }
                    s.setStatus("BOOKED");
                    seatRepo.saveAll(seats);
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("[ERROR] Cannot update seat: " + e.getMessage());
        }
        return false;
    }

    /**
     * Mark seat as AVAILABLE again (for cancellation).
     */
    public boolean releaseSeat(String seatId) {
        try {
            List<Seat> seats = seatRepo.findAll();
            for (Seat s : seats) {
                if (s.getSeatId().equalsIgnoreCase(seatId)) {
                    s.setStatus("AVAILABLE");
                    seatRepo.saveAll(seats);
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
        return false;
    }

    // ─────────────────────────────────────────────
    //  HELPERS
    // ─────────────────────────────────────────────
    private static int rowOrder(String row) {
        try { return Integer.parseInt(row); } catch (Exception e) { return 0; }
    }
}
