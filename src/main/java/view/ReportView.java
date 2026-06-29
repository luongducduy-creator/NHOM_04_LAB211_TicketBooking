package view;

import controller.BookingController;
import controller.StadiumController;
import model.match.Match;
import model.ticket.Ticket;
import model.ticket.TicketStatus;
import model.transaction.Transaction;

import java.util.*;

/**
 * T6 – ReportView
 * Displays:
 *   1. Revenue report per match
 *   2. Ticket status report (sold / available / cancelled)
 *   3. Top revenue matches
 */
public class ReportView {

    private final BookingController bookingCtrl;
    private final StadiumController stadiumCtrl;
    private final Scanner sc;

    public ReportView(BookingController bookingCtrl, StadiumController stadiumCtrl, Scanner sc) {
        this.bookingCtrl = bookingCtrl;
        this.stadiumCtrl = stadiumCtrl;
        this.sc          = sc;
    }

    // ─────────────────────────────────────────────
    //  REPORT MENU
    // ─────────────────────────────────────────────
    public void showMenu() {
        int choice = -1;
        do {
            System.out.println("\n+===========================+");
            System.out.println("|        REPORTS            |");
            System.out.println("+===========================+");
            System.out.println("|  1. Revenue by match      |");
            System.out.println("|  2. Ticket status summary |");
            System.out.println("|  3. Top 5 revenue matches |");
            System.out.println("|  0. Back                  |");
            System.out.println("+===========================+");
            System.out.print("Choose: ");

            choice = readInt();
            switch (choice) {
                case 1 -> showRevenueByMatch();
                case 2 -> showTicketStatusSummary();
                case 3 -> showTopRevenueMatches();
                case 0 -> System.out.println("Returning...");
                default -> System.out.println("Invalid option.");
            }
        } while (choice != 0);
    }

    // ─────────────────────────────────────────────
    //  1. REVENUE BY MATCH
    // ─────────────────────────────────────────────
    private void showRevenueByMatch() {
        List<Match> matches = stadiumCtrl.getAllMatches();
        List<Transaction> allTrans = getAllTransactions();

        // Group transactions by matchId (via ticketId lookup)
        Map<String, Double> revenueMap = new LinkedHashMap<>();
        Map<String, Integer> ticketCountMap = new LinkedHashMap<>();

        for (Match m : matches) {
            revenueMap.put(m.getMatchId(), 0.0);
            ticketCountMap.put(m.getMatchId(), 0);
        }

        for (Transaction tr : allTrans) {
            if (tr.getStatus() == Transaction.Status.CANCELLED) continue;
            Ticket ticket = bookingCtrl.getTicketById(tr.getTicketId());
            if (ticket == null) continue;
            String matchId = ticket.getMatchId();
            revenueMap.merge(matchId, tr.getAmount(), Double::sum);
            ticketCountMap.merge(matchId, 1, Integer::sum);
        }

        System.out.println("\n  ===== REVENUE REPORT BY MATCH =====");
        System.out.printf("  %-8s %-22s %-12s %15s %8s%n",
                "MatchID", "Teams", "Date", "Revenue (VND)", "Tickets");
        System.out.println("  " + "─".repeat(72));

        double totalRevenue = 0;
        int totalTickets = 0;
        for (Match m : matches) {
            double rev   = revenueMap.getOrDefault(m.getMatchId(), 0.0);
            int tickets  = ticketCountMap.getOrDefault(m.getMatchId(), 0);
            totalRevenue += rev;
            totalTickets += tickets;
            System.out.printf("  %-8s %-22s %-12s %,15.0f %8d%n",
                    m.getMatchId(),
                    m.getHomeTeam() + " vs " + m.getAwayTeam(),
                    m.getDate(), rev, tickets);
        }
        System.out.println("  " + "─".repeat(72));
        System.out.printf("  %-43s %,15.0f %8d%n", "TOTAL", totalRevenue, totalTickets);
    }

    // ─────────────────────────────────────────────
    //  2. TICKET STATUS SUMMARY
    // ─────────────────────────────────────────────
    private void showTicketStatusSummary() {
        List<Transaction> allTrans = getAllTransactions();
        int sold = 0, cancelled = 0, confirmed = 0;

        for (Transaction tr : allTrans) {
            switch (tr.getStatus()) {
                case SUCCESS   -> sold++;
                case CONFIRMED -> confirmed++;
                case CANCELLED -> cancelled++;
                default -> {}
            }
        }
        int total = sold + cancelled + confirmed;

        System.out.println("\n  ===== TICKET STATUS SUMMARY =====");
        System.out.printf("  %-20s %8d  (%5.1f%%)%n", "SUCCESS (Sold):",   sold,      pct(sold, total));
        System.out.printf("  %-20s %8d  (%5.1f%%)%n", "CONFIRMED:",        confirmed, pct(confirmed, total));
        System.out.printf("  %-20s %8d  (%5.1f%%)%n", "CANCELLED:",        cancelled, pct(cancelled, total));
        System.out.println("  " + "─".repeat(40));
        System.out.printf("  %-20s %8d%n", "TOTAL transactions:", total);
    }

    // ─────────────────────────────────────────────
    //  3. TOP 5 REVENUE MATCHES
    // ─────────────────────────────────────────────
    private void showTopRevenueMatches() {
        List<Match> matches = stadiumCtrl.getAllMatches();
        List<Transaction> allTrans = getAllTransactions();

        Map<String, Double> revenueMap = new HashMap<>();
        for (Transaction tr : allTrans) {
            if (tr.getStatus() == Transaction.Status.CANCELLED) continue;
            Ticket ticket = bookingCtrl.getTicketById(tr.getTicketId());
            if (ticket == null) continue;
            revenueMap.merge(ticket.getMatchId(), tr.getAmount(), Double::sum);
        }

        // Sort by revenue desc
        List<Map.Entry<String, Double>> sorted = new ArrayList<>(revenueMap.entrySet());
        sorted.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        System.out.println("\n  ===== TOP 5 REVENUE MATCHES =====");
        System.out.printf("  %-4s %-8s %-25s %15s%n", "Rank", "MatchID", "Teams", "Revenue (VND)");
        System.out.println("  " + "─".repeat(58));

        int rank = 1;
        for (Map.Entry<String, Double> entry : sorted) {
            if (rank > 5) break;
            String matchId = entry.getKey();
            Match m = matches.stream()
                    .filter(x -> x.getMatchId().equals(matchId))
                    .findFirst().orElse(null);
            String teams = (m != null) ? m.getHomeTeam() + " vs " + m.getAwayTeam() : "Unknown";
            System.out.printf("  %-4d %-8s %-25s %,15.0f%n", rank++, matchId, teams, entry.getValue());
        }
    }

    // ─────────────────────────────────────────────
    //  HELPERS
    // ─────────────────────────────────────────────
    private List<Transaction> getAllTransactions() {
        // Access via bookingCtrl – use a dummy fanId approach or expose repo
        // We expose getMyTransactions with null guard via a special all-fan call
        // Since BookingController's getMyTransactions filters by fanId,
        // we re-read from TransactionRepository directly here.
        repository.TransactionRepository repo = new repository.TransactionRepository();
        return repo.findAll();
    }

    private double pct(int part, int total) {
        return total == 0 ? 0 : (100.0 * part / total);
    }

    private int readInt() {
        try { return Integer.parseInt(sc.nextLine().trim()); }
        catch (NumberFormatException e) { return -1; }
    }
}
