package view;

import controller.BookingController;
import controller.FanController;
import controller.StadiumController;
import model.fan.Fan;
import model.match.Match;
import model.seat.Seat;
import model.seat.Section;
import model.ticket.Ticket;
import model.transaction.Transaction;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * T6 – BookingView
 * Handles the complete booking flow:
 *   1. List matches
 *   2. Select match → show stadium + sections
 *   3. Select section → show ASCII seat map
 *   4. Select seat
 *   5. Select payment method
 *   6. Confirm & book
 *
 * Also handles: view my tickets, cancel booking.
 */
public class BookingView {

    private final BookingController bookingCtrl;
    private final StadiumController stadiumCtrl;
    private final FanController     fanCtrl;
    private final SeatMapView       seatMapView;
    private final Scanner           sc;

    public BookingView(BookingController bookingCtrl,
                       StadiumController stadiumCtrl,
                       FanController fanCtrl,
                       Scanner sc) {
        this.bookingCtrl = bookingCtrl;
        this.stadiumCtrl = stadiumCtrl;
        this.fanCtrl     = fanCtrl;
        this.seatMapView = new SeatMapView();
        this.sc          = sc;
    }

    // ─────────────────────────────────────────────
    //  BOOKING MENU (Fan must be logged in)
    // ─────────────────────────────────────────────
    public void showMenu(Fan currentFan) {
        int choice = -1;
        do {
            System.out.println("\n+==========================+");
            System.out.println("|      TICKET BOOKING      |");
            System.out.println("+==========================+");
            System.out.println("|  1. Book a ticket        |");
            System.out.println("|  2. My tickets           |");
            System.out.println("|  3. Cancel a booking     |");
            System.out.println("|  0. Back                 |");
            System.out.println("+==========================+");
            System.out.print("Choose: ");

            choice = readInt();
            switch (choice) {
                case 1 -> startBookingFlow(currentFan);
                case 2 -> showMyTickets(currentFan);
                case 3 -> cancelBookingFlow(currentFan);
                case 0 -> System.out.println("Returning...");
                default -> System.out.println("[!] Invalid option.");
            }
        } while (choice != 0);
    }

    // ─────────────────────────────────────────────
    //  STEP 1: SELECT MATCH
    // ─────────────────────────────────────────────
    private void startBookingFlow(Fan fan) {
        List<Match> matches = stadiumCtrl.getAllMatches();
        if (matches.isEmpty()) {
            System.out.println("[!] No matches available.");
            return;
        }

        System.out.println("\n  ===== AVAILABLE MATCHES =====");
        for (int i = 0; i < matches.size(); i++) {
            Match m = matches.get(i);
            System.out.printf("  %2d. [%s] %s vs %s  |  %s  |  Stadium: %s%n",
                    i + 1, m.getMatchId(), m.getHomeTeam(), m.getAwayTeam(),
                    m.getDate(), m.getStadiumId());
        }
        System.out.print("\n  Enter match number (0 to cancel): ");
        int idx = readInt() - 1;
        if (idx < 0 || idx >= matches.size()) { System.out.println("Cancelled."); return; }

        Match chosen = matches.get(idx);
        selectSection(fan, chosen);
    }

    // ─────────────────────────────────────────────
    //  STEP 2: SELECT SECTION
    // ─────────────────────────────────────────────
    private void selectSection(Fan fan, Match match) {
        List<Section> sections = stadiumCtrl.getSections(match.getStadiumId());
        if (sections.isEmpty()) {
            System.out.println("[!] No sections found for this stadium.");
            return;
        }

        System.out.println("\n  ===== SECTIONS – Stadium " + match.getStadiumId() + " =====");
        for (int i = 0; i < sections.size(); i++) {
            Section s = sections.get(i);
            int avail = stadiumCtrl.getAvailableSeats(s.getSectionId()).size();
            System.out.printf("  %2d. [%s] %s (%s)  –  Available: %d seats%n",
                    i + 1, s.getSectionId(), s.getName(), s.getType(), avail);
        }
        System.out.print("\n  Enter section number (0 to go back): ");
        int idx = readInt() - 1;
        if (idx < 0 || idx >= sections.size()) { System.out.println("Cancelled."); return; }

        Section chosen = sections.get(idx);
        selectSeat(fan, match, chosen);
    }

    // ─────────────────────────────────────────────
    //  STEP 3: SHOW SEAT MAP + SELECT SEAT
    // ─────────────────────────────────────────────
    private void selectSeat(Fan fan, Match match, Section section) {
        // Build and display seat map
        Map<String, List<Seat>> seatMap = stadiumCtrl.buildSeatMap(section.getSectionId());
        seatMapView.display(seatMap, section);

        List<Seat> available = stadiumCtrl.getAvailableSeats(section.getSectionId());
        seatMapView.displayAvailableSeats(available, section.getSectionId());

        if (available.isEmpty()) {
            System.out.println("[!] No seats available in this section.");
            return;
        }

        System.out.print("  Enter Seat ID to book (e.g. SEAT5), or 0 to go back: ");
        String seatId = sc.nextLine().trim();
        if (seatId.equals("0")) { System.out.println("Cancelled."); return; }

        // Verify seat is in available list
        boolean validSeat = available.stream()
                .anyMatch(s -> s.getSeatId().equalsIgnoreCase(seatId));
        if (!validSeat) {
            System.out.println("[ERROR] Invalid or unavailable seat ID.");
            return;
        }

        confirmAndBook(fan, match, section, seatId);
    }

    // ─────────────────────────────────────────────
    //  STEP 4: PAYMENT & CONFIRM
    // ─────────────────────────────────────────────
    private void confirmAndBook(Fan fan, Match match, Section section, String seatId) {
        double price = section.getType().name().equalsIgnoreCase("VIP") ? 800_000.0 : 300_000.0;

        System.out.println("\n  ===== BOOKING CONFIRMATION =====");
        System.out.println("  Match  : " + match.getHomeTeam() + " vs " + match.getAwayTeam());
        System.out.println("  Date   : " + match.getDate());
        System.out.println("  Section: " + section.getName() + " (" + section.getType() + ")");
        System.out.println("  Seat   : " + seatId);
        System.out.printf ("  Price  : %,.0f VND%n", price);

        System.out.println("\n  Select payment method:");
        System.out.println("    1. CASH");
        System.out.println("    2. ONLINE");
        System.out.print("  Choose: ");
        int pmChoice = readInt();
        Transaction.PaymentMethod pm = (pmChoice == 2)
                ? Transaction.PaymentMethod.ONLINE
                : Transaction.PaymentMethod.CASH;

        System.out.print("\n  Confirm booking? (y/n): ");
        String confirm = sc.nextLine().trim().toLowerCase();
        if (!confirm.equals("y") && !confirm.equals("yes")) {
            System.out.println("Booking cancelled.");
            return;
        }

        Transaction tr = bookingCtrl.bookSeat(fan.getId(), match.getMatchId(), seatId, pm);
        if (tr != null) {
            System.out.println("\n  ✔ BOOKING SUCCESS");
            System.out.println("  Transaction ID: " + tr.getTransactionId());
            System.out.printf ("  Amount paid   : %,.0f VND (%s)%n", tr.getAmount(), tr.getPaymentMethod());
        }
    }

    // ─────────────────────────────────────────────
    //  VIEW MY TICKETS
    // ─────────────────────────────────────────────
    public void showMyTickets(Fan fan) {
        List<Ticket> tickets = fanCtrl.getMyTickets(fan.getId());
        System.out.println("\n  ===== MY TICKETS – " + fan.getName() + " =====");
        if (tickets.isEmpty()) {
            System.out.println("  [!] You have no tickets.");
            return;
        }
        System.out.printf("  %-12s %-10s %-10s %-8s %12s %-10s %-10s%n",
                "TicketID", "MatchID", "SeatID", "Type", "Price", "Date", "Status");
        System.out.println("  " + "-".repeat(78));
        for (Ticket t : tickets) {
            System.out.printf("  %-12s %-10s %-10s %-8s %,12.0f %-10s %-10s%n",
                    t.getTicketId(), t.getMatchId(), t.getSeatId(),
                    t.getSeatType(), t.getPrice(), t.getDate(), t.getStatus());
        }
    }

    // ─────────────────────────────────────────────
    //  CANCEL BOOKING FLOW
    // ─────────────────────────────────────────────
    private void cancelBookingFlow(Fan fan) {
        List<Transaction> transactions = bookingCtrl.getMyTransactions(fan.getId());
        if (transactions.isEmpty()) {
            System.out.println("  [!] You have no bookings to cancel.");
            return;
        }

        System.out.println("\n  ===== MY TRANSACTIONS =====");
        System.out.printf("  %-12s %-10s %12s %-10s %-12s%n",
                "TransID", "TicketID", "Amount", "Payment", "Status");
        System.out.println("  " + "-".repeat(60));
        for (Transaction t : transactions) {
            System.out.printf("  %-12s %-10s %,12.0f %-10s %-12s%n",
                    t.getTransactionId(), t.getTicketId(),
                    t.getAmount(), t.getPaymentMethod(), t.getStatus());
        }

        System.out.print("\n  Enter Transaction ID to cancel (0 to go back): ");
        String transId = sc.nextLine().trim();
        if (transId.equals("0")) return;

        bookingCtrl.cancelBooking(transId, fan.getId());
    }

    // ─────────────────────────────────────────────
    //  HELPER
    // ─────────────────────────────────────────────
    private int readInt() {
        try {
            String line = sc.nextLine().trim();
            return Integer.parseInt(line);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
