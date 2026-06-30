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

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * T6 – BookingView
 * Handles the complete booking flow:
 * 1. List matches
 * 2. Select match → show stadium + sections
 * 3. Select section → show ASCII seat map
 * 4. Select seat
 * 5. Select payment method
 * 6. Confirm & book
 *
 * Also handles: view my tickets, cancel booking.
 */
public class BookingView {

    private final BookingController bookingCtrl;
    private final StadiumController stadiumCtrl;
    private final FanController fanCtrl;
    private final SeatMapView seatMapView;
    private final Scanner sc;

    public BookingView(BookingController bookingCtrl,
            StadiumController stadiumCtrl,
            FanController fanCtrl,
            Scanner sc) {
        this.bookingCtrl = bookingCtrl;
        this.stadiumCtrl = stadiumCtrl;
        this.fanCtrl = fanCtrl;
        this.seatMapView = new SeatMapView();
        this.sc = sc;
    }

    // ─────────────────────────────────────────────
    // BOOKING MENU (Fan must be logged in)
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
            System.out.println("|  4. Transaction History  |");
            System.out.println("|  0. Back                 |");
            System.out.println("+==========================+");
            System.out.print("Choose: ");

            choice = readInt();
            switch (choice) {
                case 1 -> startBookingFlow(currentFan, false);
                case 2 -> showMyTickets(currentFan);
                case 3 -> cancelBookingFlow(currentFan);
                case 4 -> showTransactionHistory(currentFan);
                case 0 -> System.out.println("Returning...");
                default -> System.out.println("[!] Invalid option.");
            }
        } while (choice != 0);
    }

    // ─────────────────────────────────────────────
    // STEP 1: SELECT MATCH
    // ─────────────────────────────────────────────
    public void startBookingFlow(Fan fan, boolean isStaffBooking) {
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
        if (idx < 0 || idx >= matches.size()) {
            System.out.println("Cancelled.");
            return;
        }

        Match chosen = matches.get(idx);
        selectSection(fan, chosen, isStaffBooking);
    }

    // ─────────────────────────────────────────────
    // STEP 2: SELECT SECTION
    // ─────────────────────────────────────────────
    private void selectSection(Fan fan, Match match, boolean isStaffBooking) {
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
        if (idx < 0 || idx >= sections.size()) {
            System.out.println("Cancelled.");
            return;
        }

        Section chosen = sections.get(idx);
        selectSeat(fan, match, chosen, isStaffBooking);
    }

    // ─────────────────────────────────────────────
    // STEP 3: SHOW SEAT MAP + SELECT SEAT
    // ─────────────────────────────────────────────
    private void selectSeat(Fan fan, Match match, Section section, boolean isStaffBooking) {
        // Build and display seat map
        Map<String, List<Seat>> seatMap = stadiumCtrl.buildSeatMap(section.getSectionId());
        
        List<String> bookedSeatIds = bookingCtrl.getTicketRepo().findAll().stream()
                .filter(t -> t.getMatchId().equals(match.getMatchId()) && t.getStatus() == model.ticket.TicketStatus.SOLD)
                .map(Ticket::getSeatId)
                .toList();

        seatMapView.display(seatMap, section, bookedSeatIds);

        List<Seat> available = stadiumCtrl.getAvailableSeats(section.getSectionId());
        available.removeIf(s -> bookedSeatIds.contains(s.getSeatId()));

        if (available.isEmpty()) {
            System.out.println("[!] No seats available in this section.");
            return;
        }

        System.out.print("  Enter Seat ID to book (e.g. SEAT5), or 0 to go back: ");
        String seatId = sc.nextLine().trim();
        if (seatId.equals("0")) {
            System.out.println("Cancelled.");
            return;
        }

        // Verify seat is in available list
        boolean validSeat = available.stream()
                .anyMatch(s -> s.getSeatId().equalsIgnoreCase(seatId));
        if (!validSeat) {
            System.out.println("[ERROR] Invalid or unavailable seat ID.");
            return;
        }

        confirmAndBook(fan, match, section, seatId, isStaffBooking);
    }

    // ─────────────────────────────────────────────
    // STEP 4: PAYMENT & CONFIRM
    // ─────────────────────────────────────────────
    private void confirmAndBook(Fan fan, Match match, Section section, String seatId, boolean isStaffBooking) {
        double price = section.getType().name().equalsIgnoreCase("VIP") ? 800_000.0 : 300_000.0;

        System.out.println("\n  ===== BOOKING CONFIRMATION =====");
        System.out.println("  Match  : " + match.getHomeTeam() + " vs " + match.getAwayTeam());
        System.out.println("  Date   : " + match.getDate());
        System.out.println("  Section: " + section.getName() + " (" + section.getType() + ")");
        System.out.println("  Seat   : " + seatId);
        System.out.printf("  Price  : %,.0f VND%n", price);

        Transaction.PaymentMethod pm = Transaction.PaymentMethod.ONLINE;
        if (isStaffBooking) {
            System.out.println("\n  Select payment method:");
            System.out.println("    1. CASH (Staff physical collection)");
            System.out.println("    2. ONLINE");
            System.out.print("  Choose: ");
            int pmChoice = readInt();
            pm = (pmChoice == 2)
                    ? Transaction.PaymentMethod.ONLINE
                    : Transaction.PaymentMethod.CASH;
        } else {
            System.out.println("  Payment Method: ONLINE (Fans can only pay online)");
        }

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
                System.out.printf("  Amount paid   : %,.0f VND (%s)%n", tr.getAmount(), tr.getPaymentMethod());
                // Show updated transaction history
                showTransactionHistory(fan);
            }
    }

    // ─────────────────────────────────────────────
    // VIEW MY TICKETS & DOWNLOAD TICKET
    // ─────────────────────────────────────────────
    public void showMyTickets(Fan fan) {
        List<Ticket> tickets = fanCtrl.getMyTickets(fan.getId());
        System.out.println("\n  ===== MY TICKETS – " + fan.getName() + " =====");
        if (tickets.isEmpty()) {
            System.out.println("  [!] You have no tickets.");
            return;
        }
        System.out.printf("  %-12s %-10s %-18s %-8s %12s %-10s %-10s%n",
                "TicketID", "MatchID", "SeatID", "Type", "Price", "Date", "Status");
        System.out.println("  " + "-".repeat(86));
        for (Ticket t : tickets) {
            String seatDisplay = t.getSeatId();
            Seat seat = stadiumCtrl.getSeatById(t.getSeatId());
            if (seat != null) {
                seatDisplay += "(R" + seat.getRow() + "N" + seat.getNumber() + ")";
            }
            System.out.printf("  %-12s %-10s %-18s %-8s %,12.0f %-10s %-10s%n",
                    t.getTicketId(), t.getMatchId(), seatDisplay,
                    t.getSeatType(), t.getPrice(), t.getDate(), t.getStatus());
        }

        System.out.print("\n  Enter Ticket ID to download/print (or 0 to go back): ");
        String ticketId = sc.nextLine().trim();
        if (ticketId.equals("0") || ticketId.isEmpty())
            return;

        Ticket target = tickets.stream()
                .filter(t -> t.getTicketId().equalsIgnoreCase(ticketId))
                .findFirst().orElse(null);
        if (target == null) {
            System.out.println("[ERROR] Ticket not found in your list.");
            return;
        }
        downloadTicket(target, fan);
    }

    public void downloadTicket(Ticket ticket, Fan fan) {
        File dir = new File(System.getProperty("user.dir") + "/downloads");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, "ticket_" + ticket.getTicketId() + ".txt");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write("=================================================\n");
            bw.write("            FOOTBALL MATCH TICKET                \n");
            bw.write("=================================================\n");
            bw.write("  Ticket ID    : " + ticket.getTicketId() + "\n");
            bw.write("  Owner Name   : " + fan.getName() + "\n");
            bw.write("  Owner Email  : " + fan.getEmail() + "\n");
            bw.write("  Match ID     : " + ticket.getMatchId() + "\n");
            String seatDisplay = ticket.getSeatId();
            Seat seat = stadiumCtrl.getSeatById(ticket.getSeatId());
            if (seat != null) {
                seatDisplay += "(R" + seat.getRow() + "N" + seat.getNumber() + ")";
            }
            bw.write("  Seat ID      : " + seatDisplay + "\n");
            bw.write("  Seat Type    : " + ticket.getSeatType() + "\n");
            bw.write("  Price        : " + String.format("%,.0f VND", ticket.getPrice()) + "\n");
            bw.write("  Match Date   : " + ticket.getDate() + "\n");
            bw.write("  Status       : " + ticket.getStatus() + "\n");
            bw.write("=================================================\n");
            bw.write("       Thank you for your purchase!              \n");
            System.out.println("[OK] Ticket downloaded successfully to: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("[ERROR] Failed to download ticket: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────
    // VIEW TRANSACTION HISTORY
    // ─────────────────────────────────────────────
    public void showTransactionHistory(Fan fan) {
        List<Transaction> transactions = bookingCtrl.getMyTransactions(fan.getId());
        System.out.println("\n  ===== TRANSACTION HISTORY – " + fan.getName() + " =====");
        if (transactions.isEmpty()) {
            System.out.println("  [!] You have no transactions.");
            return;
        }
        System.out.printf("  %-12s %-10s %15s %-12s %-12s%n",
                "TransID", "TicketID", "Amount (VND)", "Payment", "Status");
        System.out.println("  " + "-".repeat(65));
        for (Transaction t : transactions) {
            System.out.printf("  %-12s %-10s %,15.0f %-12s %-12s%n",
                    t.getTransactionId(), t.getTicketId(),
                    t.getAmount(), t.getPaymentMethod(), t.getStatus());
        }
    }

    // ─────────────────────────────────────────────
    // CANCEL BOOKING FLOW
    // ─────────────────────────────────────────────
    public void cancelBookingFlow(Fan fan) {
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
        if (transId.equals("0"))
            return;

        bookingCtrl.cancelBooking(transId, fan.getId());
    }

    // ─────────────────────────────────────────────
    // HELPER
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
