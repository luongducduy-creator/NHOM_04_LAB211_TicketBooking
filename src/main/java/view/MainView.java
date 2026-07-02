package view;

import controller.BookingController;
import controller.FanController;
import controller.StadiumController;
import model.fan.Fan;
import model.match.Match;
import model.ticket.Ticket;
import model.Stadium;
import model.seat.Seat;
import model.seat.SeatType;
import model.seat.Section;
import model.transaction.Transaction;

import java.io.*;
import java.util.*;

/**
 * T6 – MainView
 * Entry-point View, handles:
 * - Guest menu (Register, Login, Browse matches/stadiums)
 * - Fan menu (Book ticket, My tickets, Reports, Logout)
 *
 * MVC Wiring: MainView → FanController / StadiumController / BookingController
 */
public class MainView {

    private final FanController fanCtrl;
    private final StadiumController stadiumCtrl;
    private final BookingController bookingCtrl;
    private final Scanner sc;

    // Sub-views
    private final BookingView bookingView;
    private final ReportView reportView;

    // Repositories for role CRUD
    private final repository.StadiumRepository stadiumRepo = new repository.StadiumRepository();
    private final repository.SectionRepository sectionRepo = new repository.SectionRepository();
    private final repository.SeatRepository seatRepo = new repository.SeatRepository(
            System.getProperty("user.dir") + "/data/seats.csv");
    private final repository.MatchRepository matchRepo = new repository.MatchRepository();
    private final repository.TicketRepository ticketRepo = new repository.TicketRepository(
            System.getProperty("user.dir") + "/data/tickets.csv");
    private final repository.TransactionRepository transactionRepo = new repository.TransactionRepository();
    private final repository.InvoiceRepository invoiceRepo = new repository.InvoiceRepository();
    private final repository.FeedbackRepository feedbackRepo = new repository.FeedbackRepository();
    private final repository.NotificationRepository notificationRepo = new repository.NotificationRepository();

    public MainView(FanController fanCtrl,
            StadiumController stadiumCtrl,
            BookingController bookingCtrl,
            Scanner sc) {
        this.fanCtrl = fanCtrl;
        this.stadiumCtrl = stadiumCtrl;
        this.bookingCtrl = bookingCtrl;
        this.sc = sc;

        this.bookingView = new BookingView(bookingCtrl, stadiumCtrl, fanCtrl, sc);
        this.reportView = new ReportView(bookingCtrl, stadiumCtrl, sc);
    }

    // ─────────────────────────────────────────────
    // APPLICATION ENTRY POINT
    // ─────────────────────────────────────────────
    public void start() {
        printWelcomeBanner();
        Fan currentFan = null;
        boolean isStaff = false;
        boolean isAdmin = false;
        boolean running = true;

        while (running) {
            if (currentFan != null) {
                // Logged-in Fan menu
                int choice = showFanMenu(currentFan);
                switch (choice) {
                    case 1 -> browseMatchesFlow();
                    case 2 -> viewMatchDetailsFlow();
                    case 3 -> viewStadiumLayoutFlow();
                    case 4 -> bookingView.showMenu(currentFan);
                    case 5 -> bookingView.showMyTickets(currentFan);
                    case 6 -> bookingView.cancelBookingFlow(currentFan);
                    case 7 -> viewBookingHistoryFlow(currentFan);
                    case 8 -> bookingView.showTransactionHistory(currentFan);
                    case 9 -> viewInvoiceFlow(currentFan);
                    case 10 -> sendFeedbackFlow(currentFan);
                    case 11 -> viewNotificationsFlow();
                    case 12 -> updateProfileFlow(currentFan);
                    case 13 -> changePasswordFlow(currentFan);
                    case 0 -> {
                        currentFan = logout(currentFan);
                    }
                    default -> System.out.println("Invalid option.");
                }
            } else if (isStaff) {
                // Logged-in Staff menu
                int choice = showStaffMenu();
                switch (choice) {
                    case 1 -> viewStadiumLayoutFlow();
                    case 2 -> staffSellTicketFlow();
                    case 3 -> staffManageBookingFlow();
                    case 4 -> staffVerifyTicketFlow();
                    case 5 -> staffViewInvoiceFlow();
                    case 6 -> staffRespondFeedbackFlow();
                    case 7 -> staffSendNotificationFlow();
                    case 0 -> {
                        System.out.println("\n[INFO] Staff logged out.");
                        isStaff = false;
                    }
                    default -> System.out.println("Invalid option.");
                }
            } else if (isAdmin) {
                // Logged-in Admin menu
                int choice = showAdminMenu();
                switch (choice) {
                    case 1 -> adminManageStadiums();
                    case 2 -> adminManageSections();
                    case 3 -> adminManageSeats();
                    case 4 -> adminManageMatches();
                    case 5 -> adminManageTicketTypes();
                    case 6 -> adminManageBookings();
                    case 7 -> adminManagePayments();
                    case 8 -> adminManageReports();
                    case 0 -> {
                        System.out.println("\n[INFO] Admin logged out.");
                        isAdmin = false;
                    }
                    default -> System.out.println("Invalid option.");
                }
            } else {
                // Guest menu
                int choice = showGuestMenu();
                switch (choice) {
                    case 1 -> currentFan = registerFlow();
                    case 2 -> currentFan = loginFlow();
                    case 3 -> isStaff = loginStaffFlow();
                    case 4 -> isAdmin = loginAdminFlow();
                    case 5 -> browseMatchesFlow();
                    case 6 -> viewMatchDetailsFlow();
                    case 7 -> viewStadiumLayoutFlow();
                    case 8 -> forgotPasswordFlow();
                    case 0 -> {
                        running = false;
                        printGoodbye();
                    }
                    default -> System.out.println("Invalid option. Please try again.");
                }
            }
        }
    }

    // ─────────────────────────────────────────────
    // MENUS
    // ─────────────────────────────────────────────
    private int showGuestMenu() {
        System.out.println("\n+==============================================+");
        System.out.println("|    FOOTBALL TICKET BOOKING SYSTEM v1.0       |");
        System.out.println("+==============================================+");
        System.out.println("| 1. Register (Guest)                          |");
        System.out.println("| 2. Login as Fan                              |");
        System.out.println("| 3. Login as Staff                            |");
        System.out.println("| 4. Login as Admin                            |");
        System.out.println("| 5. Search/Browse Matches                     |");
        System.out.println("| 6. View Match Details                        |");
        System.out.println("| 7. View Stadium Layout                       |");
        System.out.println("| 8. Forgot Password                           |");
        System.out.println("| 0. Exit                                      |");
        System.out.println("+==============================================+");
        System.out.print("Choose: ");
        return readInt();
    }

    private int showFanMenu(Fan fan) {
        System.out.println("\n+==============================================+");
        System.out.printf("| Logged in as: %-30s |%n", trim(fan.getName(), 30));
        System.out.println("+==============================================+");
        System.out.println("| 1. Search/Browse Matches                     |");
        System.out.println("| 2. View Match Details                        |");
        System.out.println("| 3. View Stadium Layout                       |");
        System.out.println("| 4. Book a ticket                             |");
        System.out.println("| 5. View My Tickets & Download                |");
        System.out.println("| 6. Cancel a booking                          |");
        System.out.println("| 7. View Booking History                      |");
        System.out.println("| 8. View Transaction History                  |");
        System.out.println("| 9. View Invoice                              |");
        System.out.println("| 10. Send Feedback                            |");
        System.out.println("| 11. View Notifications                       |");
        System.out.println("| 12. Update Profile                           |");
        System.out.println("| 13. Change Password                          |");
        System.out.println("| 0. Logout                                    |");
        System.out.println("+==============================================+");
        System.out.print("Choose: ");
        return readInt();
    }

    private int showStaffMenu() {
        System.out.println("\n+==============================================+");
        System.out.println("|                  STAFF MENU                  |");
        System.out.println("+==============================================+");
        System.out.println("| 1. View Seat Map & Stadium Layout            |");
        System.out.println("| 2. Book Ticket for Fan                       |");
        System.out.println("| 3. Manage Booking (View/Confirm/Cancel)      |");
        System.out.println("| 4. Verify Payment / Verify Ticket            |");
        System.out.println("| 5. View Invoice                              |");
        System.out.println("| 6. Respond Feedback                          |");
        System.out.println("| 7. Send Notification                         |");
        System.out.println("| 0. Logout                                    |");
        System.out.println("+==============================================+");
        System.out.print("Choose: ");
        return readInt();
    }

    private int showAdminMenu() {
        System.out.println("\n+==============================================+");
        System.out.println("|                  ADMIN MENU                  |");
        System.out.println("+==============================================+");
        System.out.println("| 1. Stadium Management                        |");
        System.out.println("| 2. Section Management                        |");
        System.out.println("| 3. Seat Management                           |");
        System.out.println("| 4. Match Management                          |");
        System.out.println("| 5. Ticket Type Management                    |");
        System.out.println("| 6. Booking Management                        |");
        System.out.println("| 7. Payment Management                        |");
        System.out.println("| 8. Report Management                         |");
        System.out.println("| 0. Logout                                    |");
        System.out.println("+==============================================+");
        System.out.print("Choose: ");
        return readInt();
    }

    // ─────────────────────────────────────────────
    // GUEST & GENERAL FLOWS
    // ─────────────────────────────────────────────
    private Fan registerFlow() {
        System.out.println("\n  ===== REGISTER =====");
        // Name validation: non-empty, starts with alphanumeric
        String name;
        while (true) {
            System.out.print("  Full name  : ");
            name = sc.nextLine().trim();
            if (name.isEmpty()) {
                System.out.println("[ERROR] Name cannot be empty.");
                continue;
            }
            if (!java.util.regex.Pattern.matches("^[A-Za-z0-9].*", name)) {
                System.out.println("[ERROR] Name must start with a letter or number, no leading special characters.");
                continue;
            }
            break;
        }
        // Email validation
        String email;
        while (true) {
            System.out.print("  Email      : ");
            email = sc.nextLine().trim();
            if (!java.util.regex.Pattern.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", email)) {
                System.out.println("[ERROR] Invalid email format.");
                continue;
            }
            // Check duplicate via controller without lambda
            if (fanCtrl.isAdminOrStaffEmail(email)) {
                System.out.println("[ERROR] Admin and Staff accounts cannot be registered. They are pre-assigned by the system.");
                continue;
            }
            boolean duplicate = false;
            for (Fan f : fanCtrl.getAllFans()) {
                if (f.getEmail().equalsIgnoreCase(email)) {
                    duplicate = true;
                    break;
                }
            }
            if (duplicate) {
                System.out.println("[ERROR] Email already registered.");
                continue;
            }
            break;
        }
        // Phone validation (10 digits)
        String phone;
        while (true) {
            System.out.print("  Phone      : ");
            phone = sc.nextLine().trim();
            if (!java.util.regex.Pattern.matches("\\d{10}", phone)) {
                System.out.println("[ERROR] Phone must be exactly 10 digits.");
                continue;
            }
            break;
        }
        // Birth year validation
        int year;
        while (true) {
            System.out.print("  Birth year : ");
            year = readInt();
            if (year < 1930 || year > 2026) {
                System.out.println("[ERROR] Birth year must be between 1930 and 2026.");
                continue;
            }
            break;
        }
        // Password validation
        String pass;
        while (true) {
            System.out.print("  Password   : ");
            pass = sc.nextLine().trim();
            if (pass.length() < 4) {
                System.out.println("[ERROR] Password must be at least 4 characters.");
                continue;
            }
            break;
        }
        // Attempt registration (should succeed now)
        Fan newFan = fanCtrl.register(name, email, phone, year, pass);
        if (newFan == null) {
            System.out.println("[ERROR] Registration failed due to unexpected validation. Please try again.");
        }
        return newFan;
    }

    private Fan loginFlow() {
        System.out.println("\n  ===== LOGIN =====");
        // Email validation
        String email;
        while (true) {
            System.out.print("  Email    : ");
            email = sc.nextLine().trim();
            if (!java.util.regex.Pattern.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", email)) {
                System.out.println("[ERROR] Invalid email format.");
                continue;
            }
            break;
        }
        // Password entry and authentication
        String pass;
        Fan fan = null;
        while (fan == null) {
            System.out.print("  Password : ");
            pass = sc.nextLine().trim();
            fan = fanCtrl.login(email, pass);
            if (fan == null) {
                System.out.println("[ERROR] Invalid email or password. Please try again.");
            }
        }
        return fan;
    }

    private boolean loginStaffFlow() {
        System.out.println("\n  ===== STAFF LOGIN =====");
        System.out.print("  Email: ");
        String email = sc.nextLine().trim();
        System.out.print("  Password: ");
        String pass = sc.nextLine().trim();
        if (isValidStaff(email, pass)) {
            System.out.println("[OK] Staff logged in successfully!");
            return true;
        }
        System.out.println("[ERROR] Invalid email or password.");
        return false;
    }

    private boolean isValidStaff(String email, String pass) {
        if (email == null || pass == null) return false;
        String e = email.trim().toLowerCase();
        if (e.equals("staff@gmail.com") && pass.equals("staff")) {
            return true;
        }
        if (e.matches("^staff(0[1-9]|1[0-5])@gmail\\.com$")) {
            String prefix = e.substring(0, e.indexOf('@'));
            return pass.equals("staff") || pass.equals(prefix);
        }
        return false;
    }

    private boolean loginAdminFlow() {
        System.out.println("\n  ===== ADMIN LOGIN =====");
        System.out.print("  Email: ");
        String email = sc.nextLine().trim();
        System.out.print("  Password: ");
        String pass = sc.nextLine().trim();
        if (isValidAdmin(email, pass)) {
            System.out.println("[OK] Admin logged in successfully!");
            return true;
        }
        System.out.println("[ERROR] Invalid email or password.");
        return false;
    }

    private boolean isValidAdmin(String email, String pass) {
        if (email == null || pass == null) return false;
        String e = email.trim().toLowerCase();
        if (e.equals("admin@gmail.com") && pass.equals("admin")) {
            return true;
        }
        if (e.matches("^admin0[1-6]@gmail\\.com$")) {
            String prefix = e.substring(0, e.indexOf('@'));
            return pass.equals("admin") || pass.equals(prefix);
        }
        return false;
    }

    private void forgotPasswordFlow() {
        System.out.println("\n  ===== FORGOT PASSWORD =====");
        System.out.print("  Enter your Email: ");
        String email = sc.nextLine().trim();
        System.out.print("  Enter your Phone: ");
        String phone = sc.nextLine().trim();

        Fan fan = null;
        for (Fan f : fanCtrl.getAllFans()) {
            boolean emailMatches = f.getEmail().equalsIgnoreCase(email);
            // If stored phone is empty, ignore the phone check (allow recovery by email only)
            boolean phoneMatches = f.getPhone().equals(phone) || f.getPhone().isEmpty();
            if (emailMatches && phoneMatches) {
                fan = f;
                break;
            }
        }

        if (fan == null) {
            System.out.println("[ERROR] No registered fan found with this Email and Phone number.");
            return;
        }

        System.out.println("[OK] Verification successful!");
        System.out.println("  Your current password is: " + fan.getPassword());
        System.out.print("  Do you want to reset your password? (y/n): ");
        String choice = sc.nextLine().trim().toLowerCase();
        if (choice.equals("y") || choice.equals("yes")) {
            System.out.print("  Enter new password (min 4 chars): ");
            String newPass = sc.nextLine().trim();
            if (newPass.length() < 4) {
                System.out.println("[ERROR] Password must be at least 4 characters.");
            } else {
                fan.setPassword(newPass);
                fanCtrl.updateProfile(fan);
                System.out.println("[OK] Password reset successful!");
            }
        }
    }

    private void browseMatchesFlow() {
        var matches = stadiumCtrl.getAllMatches();
        System.out.println("\n  ===== UPCOMING MATCHES =====");
        if (matches.isEmpty()) {
            System.out.println("  No matches found.");
            return;
        }
        int i = 1;
        for (var m : matches) {
            System.out.printf("  %2d. [%s] %s vs %s  |  Date: %s  |  Stadium: %s%n",
                    i++, m.getMatchId(), m.getHomeTeam(), m.getAwayTeam(),
                    m.getDate(), m.getStadiumId());
        }
    }

    private void viewMatchDetailsFlow() {
        System.out.println("\n  ===== MATCH DETAILS =====");
        var matches = stadiumCtrl.getAllMatches();
        if (matches.isEmpty()) {
            System.out.println("  No matches found.");
            return;
        }
        for (int i = 0; i < matches.size(); i++) {
            System.out.printf("  %2d. [%s] %s vs %s%n", i + 1, matches.get(i).getMatchId(),
                    matches.get(i).getHomeTeam(), matches.get(i).getAwayTeam());
        }
        System.out.print("  Choose match number: ");
        int idx = readInt() - 1;
        if (idx < 0 || idx >= matches.size()) {
            System.out.println("Invalid number.");
            return;
        }
        Match chosen = matches.get(idx);
        Stadium stadium = stadiumCtrl.getStadiumById(chosen.getStadiumId());

        System.out.println("\n  Match details:");
        System.out.println("    Match ID    : " + chosen.getMatchId());
        System.out.println("    Home Team   : " + chosen.getHomeTeam());
        System.out.println("    Away Team   : " + chosen.getAwayTeam());
        System.out.println("    Date        : " + chosen.getDate());
        System.out.println("    Stadium ID  : " + chosen.getStadiumId());
        if (stadium != null) {
            System.out.println("    Stadium Name: " + stadium.getName());
            System.out.println("    Location    : " + stadium.getLocation());
            System.out.println("    Capacity    : " + stadium.getCapacity());
        }
        System.out.println("    Available sections:");
        for (Section sec : stadiumCtrl.getSections(chosen.getStadiumId())) {
            int count = stadiumCtrl.getAvailableSeats(sec.getSectionId()).size();
            System.out.printf("      - [%s] %s (%s): %d available seats%n",
                    sec.getSectionId(), sec.getName(), sec.getType(), count);
        }
    }

    private void viewStadiumLayoutFlow() {
        System.out.println("\n  ===== STADIUM LAYOUT & SEAT MAP =====");
        var matches = stadiumCtrl.getAllMatches();
        if (matches.isEmpty()) {
            System.out.println("  No matches found.");
            return;
        }
        for (int i = 0; i < matches.size(); i++) {
            System.out.printf("  %2d. [%s] %s vs %s%n", i + 1, matches.get(i).getMatchId(),
                    matches.get(i).getHomeTeam(), matches.get(i).getAwayTeam());
        }
        System.out.print("  Choose match number: ");
        int idx = readInt() - 1;
        if (idx < 0 || idx >= matches.size()) {
            System.out.println("Invalid.");
            return;
        }
        Match chosen = matches.get(idx);

        List<Section> sections = stadiumCtrl.getSections(chosen.getStadiumId());
        if (sections.isEmpty()) {
            System.out.println("No sections found.");
            return;
        }

        for (int i = 0; i < sections.size(); i++) {
            System.out.printf("  %2d. [%s] %s (%s)%n", i + 1, sections.get(i).getSectionId(),
                    sections.get(i).getName(), sections.get(i).getType());
        }
        System.out.print("  Choose section number to view seat map: ");
        int sIdx = readInt() - 1;
        if (sIdx < 0 || sIdx >= sections.size()) {
            System.out.println("Invalid.");
            return;
        }
        Section sec = sections.get(sIdx);

        Map<String, List<Seat>> seatMap = stadiumCtrl.buildSeatMap(sec.getSectionId());
        
        List<String> bookedSeatIds = ticketRepo.findAll().stream()
                .filter(t -> t.getMatchId().equals(chosen.getMatchId()) && t.getStatus() == model.ticket.TicketStatus.SOLD)
                .map(Ticket::getSeatId)
                .toList();

        new view.SeatMapView().display(seatMap, sec, bookedSeatIds);
    }

    // ─────────────────────────────────────────────
    // FAN FLOWS
    // ─────────────────────────────────────────────
    private void viewBookingHistoryFlow(Fan fan) {
        System.out.println("\n  ===== MY BOOKING HISTORY =====");
        List<Ticket> tickets = fanCtrl.getMyTickets(fan.getId());
        if (tickets.isEmpty()) {
            System.out.println("  [!] You have no booking history.");
            return;
        }
        System.out.printf("  %-12s %-10s %-18s %-8s %12s %-12s%n",
                "TicketID", "MatchID", "SeatID", "Type", "Price", "Status");
        System.out.println("  " + "-".repeat(76));
        for (Ticket t : tickets) {
            String seatDisplay = t.getSeatId();
            model.seat.Seat seat = stadiumCtrl.getSeatById(t.getSeatId());
            if (seat != null) {
                seatDisplay += "(R" + seat.getRow() + "N" + seat.getNumber() + ")";
            }
            System.out.printf("  %-12s %-10s %-18s %-8s %,12.0f %-12s%n",
                    t.getTicketId(), t.getMatchId(), seatDisplay,
                    t.getSeatType(), t.getPrice(), t.getStatus());
        }
    }

    private void viewInvoiceFlow(Fan fan) {
        System.out.println("\n  ===== MY INVOICES =====");
        List<Ticket> tickets = fanCtrl.getMyTickets(fan.getId());
        if (tickets.isEmpty()) {
            System.out.println("  [!] You have no tickets, thus no invoices.");
            return;
        }
        List<model.invoice.Invoice> allInvoices = invoiceRepo.findAll();
        boolean found = false;
        System.out.printf("  %-12s %-12s %15s %-12s%n",
                "InvoiceID", "TicketID", "Total (VND)", "Issued Date");
        System.out.println("  " + "-".repeat(55));
        for (model.invoice.Invoice inv : allInvoices) {
            boolean ownsTicket = tickets.stream().anyMatch(t -> t.getTicketId().equalsIgnoreCase(inv.getBookingId()));
            if (ownsTicket) {
                System.out.printf("  %-12s %-12s %,15.0f %-12s%n",
                        inv.getInvoiceId(), inv.getBookingId(), inv.getTotalAmount(), inv.getIssuedDate());
                found = true;
            }
        }
        if (!found) {
            System.out.println("  No invoices generated for your bookings yet.");
        }
    }

    private void sendFeedbackFlow(Fan fan) {
        System.out.println("\n  ===== SEND FEEDBACK =====");
        System.out.print("  Enter feedback details: ");
        String content = sc.nextLine().trim();
        if (content.isEmpty()) {
            System.out.println("[ERROR] Content cannot be empty.");
            return;
        }
        String fbId = feedbackRepo.generateNextFeedbackId();
        model.feedback.Feedback fb = new model.feedback.Feedback(fbId, fan.getId(), content, "No response yet");
        feedbackRepo.addFeedback(fb);
        System.out.println("[OK] Feedback sent successfully! ID: " + fbId);
    }

    private void viewNotificationsFlow() {
        System.out.println("\n  ===== NOTIFICATIONS =====");
        List<model.notification.Notification> list = notificationRepo.findAll();
        if (list.isEmpty()) {
            System.out.println("  No new notifications.");
            return;
        }
        for (model.notification.Notification nt : list) {
            System.out.printf("  [%s] %s (Broadcast Date: %s)%n",
                    nt.getNotificationId(), nt.getMessage(), nt.getDate());
        }
    }

    private void changePasswordFlow(Fan fan) {
        System.out.println("\n  ===== CHANGE PASSWORD =====");
        System.out.print("  Enter old password: ");
        String old = sc.nextLine().trim();
        if (!fan.getPassword().equals(old)) {
            System.out.println("[ERROR] Password verification failed.");
            return;
        }
        System.out.print("  Enter new password (min 4 chars): ");
        String pass = sc.nextLine().trim();
        if (pass.length() < 4) {
            System.out.println("[ERROR] Password must be at least 4 characters.");
            return;
        }
        fan.setPassword(pass);
        fanCtrl.updateProfile(fan);
        System.out.println("[OK] Password changed successfully!");
    }

    // ─────────────────────────────────────────────
    // STAFF FLOWS
    // ─────────────────────────────────────────────
    private void staffManageBookingFlow() {
        System.out.println("\n  ===== MANAGE BOOKINGS =====");
        System.out.println("  1. View Booking List");
        System.out.println("  2. Confirm Pending Transaction (PENDING -> SUCCESS)");
        System.out.println("  3. Cancel Booking");
        System.out.print("  Chon so (1/2/3): ");
        int choice = readInt();

        if (choice == 1) {
            System.out.println("\n  ===== ALL BOOKINGS =====");
            List<Ticket> tickets = ticketRepo.findAll();
            if (tickets.isEmpty()) {
                System.out.println("  No bookings in the database.");
                return;
            }
            for (Ticket t : tickets) {
                String seatDisplay = t.getSeatId();
                model.seat.Seat seat = stadiumCtrl.getSeatById(t.getSeatId());
                if (seat != null) {
                    seatDisplay += "(R" + seat.getRow() + "N" + seat.getNumber() + ")";
                }
                System.out.printf("  Ticket: %s | Match: %s | Seat: %s | Type: %s | Status: %s%n",
                        t.getTicketId(), t.getMatchId(), seatDisplay, t.getSeatType(), t.getStatus());
            }

        } else if (choice == 2) {
            // Hiện danh sách PENDING
            List<Transaction> pending = bookingCtrl.getPendingTransactions();
            if (pending.isEmpty()) {
                System.out.println("  [INFO] Khong co giao dich nao dang PENDING.");
                return;
            }
            System.out.println("\n  ===== GIAO DICH CHO XAC NHAN =====");
            System.out.printf("  %-12s %-12s %-12s %15s %-10s%n",
                    "Trans ID", "Ticket ID", "Fan ID", "So tien (VND)", "Phuong thuc");
            System.out.println("  " + "-".repeat(70));
            for (Transaction t : pending) {
                System.out.printf("  %-12s %-12s %-12s %,15.0f %-10s%n",
                        t.getTransactionId(), t.getTicketId(), t.getFanId(),
                        t.getAmount(), t.getPaymentMethod());
            }
            System.out.print("\n  Nhap Transaction ID de xac nhan (0 de quay lai): ");
            String transId = sc.nextLine().trim();
            if (transId.equals("0") || transId.isEmpty()) return;
            boolean ok = bookingCtrl.staffConfirmTransaction(transId);
            if (ok) {
                System.out.println("[OK] Giao dich " + transId + " da duoc xac nhan -> SUCCESS.");
            } else {
                System.out.println("[ERROR] Khong the xac nhan. Kiem tra lai ID hoac trang thai.");
            }

        } else if (choice == 3) {
            System.out.print("  Enter Fan ID: ");
            String fanId = sc.nextLine().trim();
            staffCancelBookingFlow(fanId);
        }
    }


    private void staffSellTicketFlow() {
        System.out.println("\n  ===== BOOK TICKET FOR FAN =====");
        System.out.print("  Enter Fan ID: ");
        String fanId = sc.nextLine().trim();
        Fan fan = fanCtrl.findById(fanId);
        if (fan == null) {
            System.out.println("[ERROR] Fan not found.");
            return;
        }
        bookingView.startBookingFlow(fan, true);
    }

    private void staffVerifyTicketFlow() {
        System.out.println("\n  ===== VERIFY TICKET =====");
        System.out.print("  Enter Ticket ID to verify: ");
        String ticketId = sc.nextLine().trim();
        Ticket ticket = bookingCtrl.getTicketById(ticketId);
        if (ticket == null) {
            System.out.println("[ERROR] Ticket not found.");
            return;
        }
        System.out.println("\n  --- Ticket Details ---");
        System.out.println("  Ticket ID  : " + ticket.getTicketId());
        System.out.println("  Match ID   : " + ticket.getMatchId());
        String seatDisplay = ticket.getSeatId();
        model.seat.Seat seat = stadiumCtrl.getSeatById(ticket.getSeatId());
        if (seat != null) {
            seatDisplay += "(R" + seat.getRow() + "N" + seat.getNumber() + ")";
        }
        System.out.println("  Seat ID    : " + seatDisplay);
        System.out.println("  Seat Type  : " + ticket.getSeatType());
        System.out.println("  Price      : " + ticket.getPrice());
        System.out.println("  Date       : " + ticket.getDate());
        System.out.println("  Status     : " + ticket.getStatus());
        if (ticket.getStatus() == model.ticket.TicketStatus.SOLD) {
            System.out.println("[OK] Ticket is VALID.");
        } else {
            System.out.println("[WARNING] Ticket is NOT valid (Status: " + ticket.getStatus() + ").");
        }
    }

    private void staffCancelBookingFlow(String fanId) {
        System.out.print("  Enter Transaction ID to cancel: ");
        String transId = sc.nextLine().trim();
        boolean success = bookingCtrl.cancelBooking(transId, fanId);
        if (success) {
            System.out.println("[OK] Booking cancelled successfully.");
        } else {
            System.out.println("[ERROR] Failed to cancel booking.");
        }
    }

    private void staffViewInvoiceFlow() {
        System.out.println("\n  ===== ALL INVOICES =====");
        List<model.invoice.Invoice> invoices = invoiceRepo.findAll();
        if (invoices.isEmpty()) {
            System.out.println("  No invoices found in database.");
            return;
        }
        System.out.printf("  %-12s %-12s %15s %-12s%n",
                "InvoiceID", "TicketID", "Total (VND)", "Issued Date");
        System.out.println("  " + "-".repeat(55));
        for (model.invoice.Invoice inv : invoices) {
            System.out.printf("  %-12s %-12s %,15.0f %-12s%n",
                    inv.getInvoiceId(), inv.getBookingId(), inv.getTotalAmount(), inv.getIssuedDate());
        }
    }

    private void staffRespondFeedbackFlow() {
        System.out.println("\n  ===== RESPOND FEEDBACK =====");
        List<model.feedback.Feedback> list = feedbackRepo.findAll();
        if (list.isEmpty()) {
            System.out.println("  No feedback found.");
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            model.feedback.Feedback fb = list.get(i);
            System.out.printf("  %2d. [Fan: %s] %s | Reply: %s%n",
                    i + 1, fb.getFanId(), fb.getContent(), fb.getResponse());
        }
        System.out.print("  Select feedback number to reply (0 to back): ");
        int choice = readInt() - 1;
        if (choice < 0 || choice >= list.size())
            return;
        model.feedback.Feedback selected = list.get(choice);

        System.out.print("  Enter response: ");
        String reply = sc.nextLine().trim();
        if (!reply.isEmpty()) {
            selected.setResponse(reply);
            feedbackRepo.saveAll(list);
            System.out.println("[OK] Feedback response submitted!");
        }
    }

    private void staffSendNotificationFlow() {
        System.out.println("\n  ===== BROADCAST NOTIFICATION =====");
        System.out.print("  Enter notification message: ");
        String msg = sc.nextLine().trim();
        if (msg.isEmpty())
            return;
        String id = notificationRepo.generateNextNotificationId();
        model.notification.Notification nt = new model.notification.Notification(id, msg, "2026-06-29");
        notificationRepo.addNotification(nt);
        System.out.println("[OK] Broadcast notification successfully! ID: " + id);
    }

    // ─────────────────────────────────────────────
    // ADMIN CRUD FLOWS
    // ─────────────────────────────────────────────

    // 1. Stadium Management
    private void adminManageStadiums() {
        System.out.println("\n  ===== STADIUM MANAGEMENT =====");
        System.out.println("  1. View Stadium List");
        System.out.println("  2. Add Stadium");
        System.out.println("  3. Update Stadium");
        System.out.println("  4. Delete Stadium");
        System.out.print("  Choose: ");
        int choice = readInt();
        if (choice == 1) {
            System.out.println("\n  ----- Stadium List -----");
            for (Stadium s : stadiumRepo.findAll()) {
                System.out.println("    " + s);
            }
        } else if (choice == 2) {
            System.out.print("  Stadium ID: ");
            String id = sc.nextLine().trim();
            System.out.print("  Stadium Name: ");
            String name = sc.nextLine().trim();
            System.out.print("  Location: ");
            String loc = sc.nextLine().trim();
            System.out.print("  Capacity: ");
            int cap = readInt();
            stadiumRepo.addStadium(new Stadium(id, name, loc, cap));
            System.out.println("[OK] Stadium added successfully.");
        } else if (choice == 3) {
            System.out.print("  Enter Stadium ID to update: ");
            String id = sc.nextLine().trim();
            List<Stadium> list = stadiumRepo.findAll();
            Stadium target = list.stream().filter(s -> s.getStadiumId().equalsIgnoreCase(id)).findFirst().orElse(null);
            if (target != null) {
                System.out.print("  New Name [" + target.getName() + "]: ");
                String name = sc.nextLine().trim();
                if (!name.isEmpty())
                    target.setName(name);
                System.out.print("  New Location [" + target.getLocation() + "]: ");
                String loc = sc.nextLine().trim();
                if (!loc.isEmpty())
                    target.setLocation(loc);
                System.out.print("  New Capacity [" + target.getCapacity() + "]: ");
                String capStr = sc.nextLine().trim();
                if (!capStr.isEmpty())
                    target.setCapacity(Integer.parseInt(capStr));
                stadiumRepo.saveAll(list);
                System.out.println("[OK] Stadium updated.");
            } else {
                System.out.println("[ERROR] Stadium not found.");
            }
        } else if (choice == 4) {
            System.out.print("  Enter Stadium ID to delete: ");
            String id = sc.nextLine().trim();
            List<Stadium> list = stadiumRepo.findAll();
            boolean removed = list.removeIf(s -> s.getStadiumId().equalsIgnoreCase(id));
            if (removed) {
                stadiumRepo.saveAll(list);
                System.out.println("[OK] Stadium deleted.");
            } else {
                System.out.println("[ERROR] Stadium not found.");
            }
        }
    }

    // 2. Section Management
    private void adminManageSections() {
        System.out.println("\n  ===== SECTION MANAGEMENT =====");
        System.out.println("  1. View Section List");
        System.out.println("  2. Add Section");
        System.out.println("  3. Update Section");
        System.out.println("  4. Delete Section");
        System.out.print("  Choose: ");
        int choice = readInt();
        if (choice == 1) {
            System.out.println("\n  ----- Section List -----");
            for (Section s : sectionRepo.findAll()) {
                System.out.printf("    Section: %s | Stadium: %s | Name: %s | Type: %s%n",
                        s.getSectionId(), s.getStadiumId(), s.getName(), s.getType());
            }
        } else if (choice == 2) {
            System.out.print("  Section ID: ");
            String id = sc.nextLine().trim();
            System.out.print("  Stadium ID: ");
            String std = sc.nextLine().trim();
            System.out.print("  Name: ");
            String name = sc.nextLine().trim();
            System.out.print("  Type (VIP/NORMAL): ");
            String type = sc.nextLine().trim();
            sectionRepo.addSection(new Section(id, std, name, SeatType.fromString(type)));
            System.out.println("[OK] Section added.");
        } else if (choice == 3) {
            System.out.print("  Enter Section ID to update: ");
            String id = sc.nextLine().trim();
            List<Section> list = sectionRepo.findAll();
            Section target = list.stream().filter(s -> s.getSectionId().equalsIgnoreCase(id)).findFirst().orElse(null);
            if (target != null) {
                System.out.print("  New Name [" + target.getName() + "]: ");
                String name = sc.nextLine().trim();
                if (!name.isEmpty())
                    target.setName(name);
                System.out.print("  New Type (VIP/NORMAL) [" + target.getType() + "]: ");
                String type = sc.nextLine().trim();
                if (!type.isEmpty())
                    target.setType(SeatType.fromString(type));
                sectionRepo.saveAll(list);
                System.out.println("[OK] Section updated.");
            } else {
                System.out.println("[ERROR] Section not found.");
            }
        } else if (choice == 4) {
            System.out.print("  Enter Section ID to delete: ");
            String id = sc.nextLine().trim();
            List<Section> list = sectionRepo.findAll();
            boolean removed = list.removeIf(s -> s.getSectionId().equalsIgnoreCase(id));
            if (removed) {
                sectionRepo.saveAll(list);
                System.out.println("[OK] Section deleted.");
            } else {
                System.out.println("[ERROR] Section not found.");
            }
        }
    }

    // 3. Seat Management
    private void adminManageSeats() {
        System.out.println("\n  ===== SEAT MANAGEMENT =====");
        System.out.println("  1. View Seat List");
        System.out.println("  2. Add Seat");
        System.out.println("  3. Update Seat");
        System.out.println("  4. Delete Seat");
        System.out.println("  5. Update Seat Status");
        System.out.print("  Choose: ");
        int choice = readInt();
        try {
            List<Seat> seats = seatRepo.findAll();
            if (choice == 1) {
                System.out.println("\n  ----- Seat List -----");
                for (Seat s : seats) {
                    System.out.printf("    Seat: %s | Section: %s | Row: %s | Number: %s | Status: %s%n",
                            s.getSeatId(), s.getSectionId(), s.getRow(), s.getNumber(), s.getStatus());
                }
            } else if (choice == 2) {
                System.out.print("  Seat ID: ");
                String id = sc.nextLine().trim();
                System.out.print("  Section ID: ");
                String sec = sc.nextLine().trim();
                System.out.print("  Row: ");
                String row = sc.nextLine().trim();
                System.out.print("  Number: ");
                String num = sc.nextLine().trim();
                System.out.print("  Status (AVAILABLE/SOLD): ");
                String stat = sc.nextLine().trim();
                seats.add(new Seat(id, sec, row, num, stat));
                seatRepo.saveAll(seats);
                System.out.println("[OK] Seat added.");
            } else if (choice == 3) {
                System.out.print("  Enter Seat ID to update: ");
                String id = sc.nextLine().trim();
                Seat target = seats.stream().filter(s -> s.getSeatId().equalsIgnoreCase(id)).findFirst().orElse(null);
                if (target != null) {
                    System.out.print("  New Row [" + target.getRow() + "]: ");
                    String row = sc.nextLine().trim();
                    if (!row.isEmpty())
                        target.setRow(row);
                    System.out.print("  New Number [" + target.getNumber() + "]: ");
                    String num = sc.nextLine().trim();
                    if (!num.isEmpty())
                        target.setNumber(num);
                    System.out.print("  New Status [" + target.getStatus() + "]: ");
                    String stat = sc.nextLine().trim();
                    if (!stat.isEmpty())
                        target.setStatus(stat);
                    seatRepo.saveAll(seats);
                    System.out.println("[OK] Seat updated.");
                } else {
                    System.out.println("[ERROR] Seat not found.");
                }
            } else if (choice == 4) {
                System.out.print("  Enter Seat ID to delete: ");
                String id = sc.nextLine().trim();
                boolean removed = seats.removeIf(s -> s.getSeatId().equalsIgnoreCase(id));
                if (removed) {
                    seatRepo.saveAll(seats);
                    System.out.println("[OK] Seat deleted.");
                } else {
                    System.out.println("[ERROR] Seat not found.");
                }
            } else if (choice == 5) {
                System.out.print("  Enter Seat ID to change status: ");
                String id = sc.nextLine().trim();
                Seat target = seats.stream().filter(s -> s.getSeatId().equalsIgnoreCase(id)).findFirst().orElse(null);
                if (target != null) {
                    System.out.print("  New Status (AVAILABLE/SOLD): ");
                    String stat = sc.nextLine().trim();
                    if (!stat.isEmpty()) {
                        target.setStatus(stat.toUpperCase());
                        seatRepo.saveAll(seats);
                        System.out.println("[OK] Seat status updated.");
                    }
                } else {
                    System.out.println("[ERROR] Seat not found.");
                }
            }
        } catch (IOException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    // 4. Match Management (overwrites matchRepo and data/matches.csv)
    private void adminManageMatches() {
        System.out.println("\n  ===== MATCH MANAGEMENT =====");
        System.out.println("  1. View Match List");
        System.out.println("  2. Add Match");
        System.out.println("  3. Update Match");
        System.out.println("  4. Delete Match");
        System.out.print("  Choose: ");
        int choice = readInt();
        List<Match> matches = matchRepo.findAll();
        if (choice == 1) {
            browseMatchesFlow();
        } else if (choice == 2) {
            System.out.print("  Match ID (e.g. M26): ");
            String matchId = sc.nextLine().trim();
            System.out.print("  Home Team: ");
            String home = sc.nextLine().trim();
            System.out.print("  Away Team: ");
            String away = sc.nextLine().trim();
            System.out.print("  Date (YYYY-MM-DD): ");
            String date = sc.nextLine().trim();
            System.out.print("  Stadium ID: ");
            String stadiumId = sc.nextLine().trim();

            if (stadiumCtrl.getStadiumById(stadiumId) == null) {
                System.out.println("[ERROR] Stadium not found: " + stadiumId);
                return;
            }
            matchRepo.addMatch(new Match(matchId, home, away, date, stadiumId));
            System.out.println("[OK] Match added successfully.");
        } else if (choice == 3) {
            System.out.print("  Enter Match ID to update: ");
            String matchId = sc.nextLine().trim();
            Match target = matches.stream().filter(m -> m.getMatchId().equalsIgnoreCase(matchId)).findFirst()
                    .orElse(null);
            if (target != null) {
                System.out.print("  New Home Team [" + target.getHomeTeam() + "]: ");
                String home = sc.nextLine().trim();
                if (!home.isEmpty())
                    target.setHomeTeam(home);
                System.out.print("  New Away Team [" + target.getAwayTeam() + "]: ");
                String away = sc.nextLine().trim();
                if (!away.isEmpty())
                    target.setAwayTeam(away);
                System.out.print("  New Date [" + target.getDate() + "]: ");
                String date = sc.nextLine().trim();
                if (!date.isEmpty())
                    target.setDate(date);
                System.out.print("  New Stadium ID [" + target.getStadiumId() + "]: ");
                String stdId = sc.nextLine().trim();
                if (!stdId.isEmpty())
                    target.setStadiumId(stdId);
                matchRepo.saveAll(matches);
                System.out.println("[OK] Match updated.");
            } else {
                System.out.println("[ERROR] Match not found.");
            }
        } else if (choice == 4) {
            System.out.print("  Enter Match ID to delete: ");
            String matchId = sc.nextLine().trim();
            boolean removed = matches.removeIf(m -> m.getMatchId().equalsIgnoreCase(matchId));
            if (removed) {
                matchRepo.saveAll(matches);
                System.out.println("[OK] Match deleted.");
            } else {
                System.out.println("[ERROR] Match not found.");
            }
        }
    }

    // 5. Ticket Type Management
    private double vipPrice   = 800_000.0;
    private double normalPrice = 300_000.0;

    private void adminManageTicketTypes() {
        System.out.println("\n  ===== TICKET TYPE MANAGEMENT =====");
        System.out.println("  1. View Current Pricing");
        System.out.println("  2. Update Ticket Type Pricing");
        System.out.print("  Choose: ");
        int choice = readInt();
        if (choice == 1) {
            System.out.printf("    - [VIP]    Gia hien tai: %,.0f VND%n", vipPrice);
            System.out.printf("    - [NORMAL] Gia hien tai: %,.0f VND%n", normalPrice);
        } else if (choice == 2) {
            System.out.println("  Chon loai ve can cap nhat:");
            System.out.println("  1. VIP");
            System.out.println("  2. NORMAL");
            System.out.print("  Chon: ");
            int typeChoice = readInt();
            if (typeChoice == 1) {
                System.out.printf("  Gia VIP hien tai: %,.0f VND%n", vipPrice);
                System.out.print("  Nhap gia moi (VND): ");
                String priceStr = sc.nextLine().trim();
                try {
                    double newPrice = Double.parseDouble(priceStr);
                    if (newPrice <= 0) { System.out.println("[ERROR] Gia phai lon hon 0."); return; }
                    vipPrice = newPrice;
                    System.out.printf("[OK] Da cap nhat gia VIP thanh: %,.0f VND%n", vipPrice);
                } catch (NumberFormatException e) {
                    System.out.println("[ERROR] Gia khong hop le.");
                }
            } else if (typeChoice == 2) {
                System.out.printf("  Gia NORMAL hien tai: %,.0f VND%n", normalPrice);
                System.out.print("  Nhap gia moi (VND): ");
                String priceStr = sc.nextLine().trim();
                try {
                    double newPrice = Double.parseDouble(priceStr);
                    if (newPrice <= 0) { System.out.println("[ERROR] Gia phai lon hon 0."); return; }
                    normalPrice = newPrice;
                    System.out.printf("[OK] Da cap nhat gia NORMAL thanh: %,.0f VND%n", normalPrice);
                } catch (NumberFormatException e) {
                    System.out.println("[ERROR] Gia khong hop le.");
                }
            } else {
                System.out.println("[ERROR] Lua chon khong hop le.");
            }
        }
    }

    // 6. Booking Management
    private void adminManageBookings() {
        System.out.println("\n  ===== BOOKING MANAGEMENT (ADMIN) =====");
        System.out.println("  1. View All Bookings (Tickets)");
        System.out.println("  2. Confirm Pending Transaction (PENDING -> SUCCESS)");
        System.out.println("  3. Cancel/Void Ticket Booking");
        System.out.print("  Choose: ");
        int choice = readInt();
        List<Ticket> tickets = ticketRepo.findAll();
        if (choice == 1) {
            System.out.println("\n  ----- Booking list -----");
            System.out.printf("  %-12s %-10s %-12s %-8s %15s %-10s%n",
                    "Ticket ID", "Match ID", "Seat ID", "Type", "Price (VND)", "Status");
            System.out.println("  " + "-".repeat(75));
            for (Ticket t : tickets) {
                System.out.printf("  %-12s %-10s %-12s %-8s %,15.0f %-10s%n",
                        t.getTicketId(), t.getMatchId(), t.getSeatId(),
                        t.getSeatType(), t.getPrice(), t.getStatus());
            }
        } else if (choice == 2) {
            // Hien danh sach PENDING transactions
            List<Transaction> pending = bookingCtrl.getPendingTransactions();
            if (pending.isEmpty()) {
                System.out.println("  [INFO] Khong co giao dich nao dang PENDING.");
                return;
            }
            System.out.println("\n  ----- Giao dich PENDING -----");
            System.out.printf("  %-12s %-12s %-12s %15s %-10s%n",
                    "Trans ID", "Ticket ID", "Fan ID", "So tien (VND)", "Phuong thuc");
            System.out.println("  " + "-".repeat(70));
            for (Transaction t : pending) {
                System.out.printf("  %-12s %-12s %-12s %,15.0f %-10s%n",
                        t.getTransactionId(), t.getTicketId(), t.getFanId(),
                        t.getAmount(), t.getPaymentMethod());
            }
            System.out.print("\n  Nhap Transaction ID de xac nhan (0 de quay lai): ");
            String transId = sc.nextLine().trim();
            if (transId.equals("0") || transId.isEmpty()) return;
            boolean ok = bookingCtrl.staffConfirmTransaction(transId);
            if (ok) {
                System.out.println("[OK] Giao dich " + transId + " da xac nhan -> SUCCESS.");
            } else {
                System.out.println("[ERROR] Khong the xac nhan. Kiem tra lai Transaction ID.");
            }
        } else if (choice == 3) {
            System.out.print("  Nhap Ticket ID can huy: ");
            String id = sc.nextLine().trim();
            Ticket target = tickets.stream().filter(t -> t.getTicketId().equalsIgnoreCase(id)).findFirst().orElse(null);
            if (target != null) {
                target.setStatus(model.ticket.TicketStatus.CANCELLED);
                ticketRepo.removeTicket(id);
                ticketRepo.addTicket(target);
                stadiumCtrl.releaseSeat(target.getSeatId());
                System.out.println("[OK] Booking da bi huy va ghe da duoc giai phong.");
            } else {
                System.out.println("[ERROR] Ticket not found.");
            }
        }
    }

    // 7. Payment Management
    private void adminManagePayments() {
        System.out.println("\n  ===== PAYMENT MANAGEMENT =====");
        System.out.println("  1. View Payments List");
        System.out.println("  2. Verify Payment Success");
        System.out.println("  3. Refund Payment");
        System.out.print("  Choose: ");
        int choice = readInt();
        List<Transaction> list = transactionRepo.findAll();
        if (choice == 1) {
            System.out.println("\n  ----- Payments list -----");
            for (Transaction t : list) {
                System.out.printf("    Trans: %s | Ticket: %s | Fan: %s | Amount: %,.0f | Method: %s | Status: %s%n",
                        t.getTransactionId(), t.getTicketId(), t.getFanId(), t.getAmount(), t.getPaymentMethod(),
                        t.getStatus());
            }
        } else if (choice == 2) {
            System.out.print("  Enter Transaction ID to verify: ");
            String id = sc.nextLine().trim();
            Transaction target = list.stream().filter(t -> t.getTransactionId().equalsIgnoreCase(id)).findFirst()
                    .orElse(null);
            if (target != null) {
                target.setStatus(Transaction.Status.SUCCESS);
                transactionRepo.saveAll(list);
                System.out.println("[OK] Transaction verified as SUCCESS.");
            } else {
                System.out.println("[ERROR] Transaction not found.");
            }
        } else if (choice == 3) {
            System.out.print("  Enter Transaction ID to refund: ");
            String id = sc.nextLine().trim();
            Transaction target = list.stream().filter(t -> t.getTransactionId().equalsIgnoreCase(id)).findFirst()
                    .orElse(null);
            if (target != null) {
                bookingCtrl.cancelBooking(id, target.getFanId());
            } else {
                System.out.println("[ERROR] Transaction not found.");
            }
        }
    }

    // 8. Report Management
    private void adminManageReports() {
        System.out.println("\n  ===== REPORT MANAGEMENT =====");
        System.out.println("  1. View Revenue Report");
        System.out.println("  2. View Booking Report");
        System.out.println("  3. View Ticket Sales Report");
        System.out.print("  Choose: ");
        int choice = readInt();
        if (choice == 1) {
            reportView.showMenu();
        } else if (choice == 2) {
            List<Ticket> list = ticketRepo.findAll();
            long sold = list.stream().filter(t -> t.getStatus() == model.ticket.TicketStatus.SOLD).count();
            long cancelled = list.stream().filter(t -> t.getStatus() == model.ticket.TicketStatus.CANCELLED).count();
            System.out.println("\n  ----- Booking Report -----");
            System.out.println("    Total Tickets Issued: " + list.size());
            System.out.println("    Sold/Booked Tickets : " + sold);
            System.out.println("    Cancelled Tickets   : " + cancelled);
        } else if (choice == 3) {
            List<Ticket> list = ticketRepo.findAll();
            double vipSales = list.stream().filter(
                    t -> t.getSeatType().equalsIgnoreCase("VIP") && t.getStatus() == model.ticket.TicketStatus.SOLD)
                    .mapToDouble(Ticket::getPrice).sum();
            double normalSales = list.stream().filter(
                    t -> t.getSeatType().equalsIgnoreCase("NORMAL") && t.getStatus() == model.ticket.TicketStatus.SOLD)
                    .mapToDouble(Ticket::getPrice).sum();
            System.out.println("\n  ----- Ticket Sales Report -----");
            System.out.printf("    VIP ticket total sales   : %,.0f VND%n", vipSales);
            System.out.printf("    Normal ticket total sales: %,.0f VND%n", normalSales);
            System.out.printf("    Total System Sales       : %,.0f VND%n", vipSales + normalSales);
        }
    }

    // ─────────────────────────────────────────────
    // SHOW PROFILE
    // ─────────────────────────────────────────────
    private void showProfile(Fan fan) {
        System.out.println("\n  ===== MY PROFILE =====");
        System.out.println("  ID        : " + fan.getId());
        System.out.println("  Name      : " + fan.getName());
        System.out.println("  Email     : " + fan.getEmail());
        System.out.println("  Phone     : " + fan.getPhone());
        System.out.println("  Birth year: " + fan.getBirthYear());
    }

    // ─────────────────────────────────────────────
    // UPDATE PROFILE FLOW
    // ─────────────────────────────────────────────
    private void updateProfileFlow(Fan fan) {
        System.out.println("\n  ===== UPDATE PROFILE =====");
        System.out.println("  (Press Enter to keep current values)");

        System.out.print("  Name [" + fan.getName() + "]: ");
        String name = sc.nextLine().trim();
        if (!name.isEmpty())
            fan.setName(name);

        System.out.print("  Phone [" + fan.getPhone() + "]: ");
        String phone = sc.nextLine().trim();
        if (!phone.isEmpty())
            fan.setPhone(phone);

        System.out.print("  Birth year [" + fan.getBirthYear() + "]: ");
        String yearStr = sc.nextLine().trim();
        if (!yearStr.isEmpty()) {
            try {
                int year = Integer.parseInt(yearStr);
                if (year >= 1900 && year <= 2010) {
                    fan.setBirthYear(year);
                } else {
                    System.out.println("[WARNING] Invalid year. Kept current value.");
                }
            } catch (NumberFormatException e) {
                System.out.println("[WARNING] Invalid number. Kept current value.");
            }
        }

        boolean ok = fanCtrl.updateProfile(fan);
        if (ok) {
            System.out.println("[OK] Profile updated successfully!");
        } else {
            System.out.println("[ERROR] Failed to update profile.");
        }
    }

    // ─────────────────────────────────────────────
    // LOGOUT
    // ─────────────────────────────────────────────
    private Fan logout(Fan fan) {
        System.out.println("\n  Goodbye, " + fan.getName() + "! You have been logged out.");
        return null;
    }

    // ─────────────────────────────────────────────
    // BANNER / FOOTER
    // ─────────────────────────────────────────────
    private void printWelcomeBanner() {
        System.out.println();
        System.out.println("  +============================================+");
        System.out.println("  |   FOOTBALL TICKET BOOKING SYSTEM v1.0     |");
        System.out.println("  |         NHOM 04  -  LAB211                |");
        System.out.println("  +============================================+");
        System.out.println();
    }

    private void printGoodbye() {
        System.out.println("\n  Thank you for using the system. See you next match!");
    }

    // ─────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────
    private int readInt() {
        try {
            return Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private String trim(String s, int maxLen) {
        if (s == null)
            return "";
        return s.length() <= maxLen ? s : s.substring(0, maxLen);
    }
}
