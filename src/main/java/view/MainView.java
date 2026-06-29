package view;

import controller.BookingController;
import controller.FanController;
import controller.StadiumController;
import model.fan.Fan;

import java.util.Scanner;

/**
 * T6 – MainView
 * Entry-point View, handles:
 *   - Guest menu (Register, Login, Browse matches/stadiums)
 *   - Fan menu   (Book ticket, My tickets, Reports, Logout)
 *
 * MVC Wiring: MainView → FanController / StadiumController / BookingController
 */
public class MainView {

    private final FanController     fanCtrl;
    private final StadiumController stadiumCtrl;
    private final BookingController bookingCtrl;
    private final Scanner           sc;

    // Sub-views
    private final BookingView bookingView;
    private final ReportView  reportView;

    public MainView(FanController fanCtrl,
                    StadiumController stadiumCtrl,
                    BookingController bookingCtrl,
                    Scanner sc) {
        this.fanCtrl     = fanCtrl;
        this.stadiumCtrl = stadiumCtrl;
        this.bookingCtrl = bookingCtrl;
        this.sc          = sc;

        this.bookingView = new BookingView(bookingCtrl, stadiumCtrl, fanCtrl, sc);
        this.reportView  = new ReportView(bookingCtrl, stadiumCtrl, sc);
    }

    // ─────────────────────────────────────────────
    //  APPLICATION ENTRY POINT
    // ─────────────────────────────────────────────
    public void start() {
        printWelcomeBanner();
        Fan currentFan = null;
        boolean running = true;

        while (running) {
            if (currentFan == null) {
                // Guest menu
                int choice = showGuestMenu();
                switch (choice) {
                    case 1  -> currentFan = registerFlow();
                    case 2  -> currentFan = loginFlow();
                    case 3  -> browseMatchesFlow();
                    case 0  -> { running = false; printGoodbye(); }
                    default -> System.out.println("Invalid option. Please try again.");
                }
            } else {
                // Logged-in Fan menu
                int choice = showFanMenu(currentFan);
                switch (choice) {
                    case 1  -> bookingView.showMenu(currentFan);
                    case 2  -> bookingView.showMyTickets(currentFan);
                    case 3  -> reportView.showMenu();
                    case 4  -> showProfile(currentFan);
                    case 0  -> { currentFan = logout(currentFan); }
                    default -> System.out.println("Invalid option.");
                }
            }
        }
    }

    // ─────────────────────────────────────────────
    //  GUEST MENU
    // ─────────────────────────────────────────────
    private int showGuestMenu() {
        System.out.println("\n+==================================+");
        System.out.println("|    FOOTBALL TICKET BOOKING       |");
        System.out.println("+==================================+");
        System.out.println("|  1. Register                     |");
        System.out.println("|  2. Login                        |");
        System.out.println("|  3. Browse matches (guest)       |");
        System.out.println("|  0. Exit                         |");
        System.out.println("+==================================+");
        System.out.print("Choose: ");
        return readInt();
    }

    // ─────────────────────────────────────────────
    //  FAN MENU (after login)
    // ─────────────────────────────────────────────
    private int showFanMenu(Fan fan) {
        System.out.println("\n+==================================+");
        System.out.printf ("|  Logged in as: %-18s|%n", trim(fan.getName(), 18));
        System.out.println("+==================================+");
        System.out.println("|  1. Book a ticket                |");
        System.out.println("|  2. My tickets                   |");
        System.out.println("|  3. Reports                      |");
        System.out.println("|  4. My profile                   |");
        System.out.println("|  0. Logout                       |");
        System.out.println("+==================================+");
        System.out.print("Choose: ");
        return readInt();
    }

    // ─────────────────────────────────────────────
    //  REGISTER FLOW
    // ─────────────────────────────────────────────
    private Fan registerFlow() {
        System.out.println("\n  ===== REGISTER =====");
        System.out.print("  Full name  : "); String name  = sc.nextLine().trim();
        System.out.print("  Email      : "); String email = sc.nextLine().trim();
        System.out.print("  Phone      : "); String phone = sc.nextLine().trim();
        System.out.print("  Birth year : "); int year = readInt();
        System.out.print("  Password   : "); String pass  = sc.nextLine().trim();

        return fanCtrl.register(name, email, phone, year, pass);
    }

    // ─────────────────────────────────────────────
    //  LOGIN FLOW
    // ─────────────────────────────────────────────
    private Fan loginFlow() {
        System.out.println("\n  ===== LOGIN =====");
        System.out.print("  Email    : "); String email = sc.nextLine().trim();
        System.out.print("  Password : "); String pass  = sc.nextLine().trim();
        return fanCtrl.login(email, pass);
    }

    // ─────────────────────────────────────────────
    //  BROWSE MATCHES (guest, no booking)
    // ─────────────────────────────────────────────
    private void browseMatchesFlow() {
        var matches = stadiumCtrl.getAllMatches();
        System.out.println("\n  ===== UPCOMING MATCHES =====");
        if (matches.isEmpty()) { System.out.println("  No matches found."); return; }
        int i = 1;
        for (var m : matches) {
            System.out.printf("  %2d. [%s] %s vs %s  |  Date: %s  |  Stadium: %s%n",
                    i++, m.getMatchId(), m.getHomeTeam(), m.getAwayTeam(),
                    m.getDate(), m.getStadiumId());
        }
        System.out.println("\n  [Login to book a ticket]");
    }

    // ─────────────────────────────────────────────
    //  SHOW PROFILE
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
    //  LOGOUT
    // ─────────────────────────────────────────────
    private Fan logout(Fan fan) {
        System.out.println("\n  Goodbye, " + fan.getName() + "! You have been logged out.");
        return null;
    }

    // ─────────────────────────────────────────────
    //  BANNER / FOOTER
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
    //  HELPERS
    // ─────────────────────────────────────────────
    private int readInt() {
        try { return Integer.parseInt(sc.nextLine().trim()); }
        catch (NumberFormatException e) { return -1; }
    }

    private String trim(String s, int maxLen) {
        if (s == null) return "";
        return s.length() <= maxLen ? s : s.substring(0, maxLen);
    }
}
