package controller;

import model.fan.Fan;
import java.util.regex.Pattern;
import model.ticket.Ticket;
import model.transaction.Transaction;
import repository.FanRepository;
import repository.TicketRepository;
import repository.TransactionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * T5 – FanController
 * Handles: register, login, getMyTickets, updateProfile
 */
public class FanController {

    private final FanRepository fanRepo;
    private final TicketRepository ticketRepo;
    private final TransactionRepository transactionRepo;

    public FanController() {
        this.fanRepo         = new FanRepository();
        this.ticketRepo      = new TicketRepository(System.getProperty("user.dir") + "/data/tickets.csv");
        this.transactionRepo = new TransactionRepository();
    }

    // ─────────────────────────────────────────────
    //  REGISTER
    // ─────────────────────────────────────────────
    /**
     * Register a new Fan.
     * @return the created Fan, or null if email already exists / invalid input
     */
    public boolean isAdminOrStaffEmail(String email) {
        if (email == null) return false;
        String e = email.trim().toLowerCase();
        if (e.equals("admin@gmail.com") || e.equals("staff@gmail.com")) return true;
        if (e.matches("^admin0[1-6]@gmail\\.com$")) return true;
        if (e.matches("^staff(0[1-9]|1[0-5])@gmail\\.com$")) return true;
        return false;
    }

    public Fan register(String name, String email, String phone, int birthYear, String password) {
        // Validate input fields
        // Name: must not be empty and cannot start with a special character
        if (name == null || name.isBlank()) {
            System.out.println("[ERROR] Name cannot be empty.");
            return null;
        }
        if (!Pattern.matches("^[A-Za-z0-9].*", name)) {
            System.out.println("[ERROR] Name must start with a letter or number, no leading special characters.");
            return null;
        }
        // Email: basic format validation
        if (email == null || !Pattern.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", email)) {
            System.out.println("[ERROR] Invalid email format.");
            return null;
        }
        if (isAdminOrStaffEmail(email)) {
            System.out.println("[ERROR] Admin and Staff accounts cannot be registered. They are pre-assigned by the system.");
            return null;
        }
        // Phone: exactly 10 digits
        if (phone == null || !Pattern.matches("\\d{10}", phone)) {
            System.out.println("[ERROR] Phone must be exactly 10 digits.");
            return null;
        }
        // Birth year: 4 digits, between 1930 and current year (2026)
        if (birthYear < 1930 || birthYear > 2026) {
            System.out.println("[ERROR] Birth year must be between 1930 and 2026.");
            return null;
        }
        // Password: at least 4 characters
        if (password == null || password.length() < 4) {
            System.out.println("[ERROR] Password must be at least 4 characters.");
            return null;
        }

        // Check duplicate email
        List<Fan> all = fanRepo.getAllFans();
        for (Fan f : all) {
            if (f.getEmail().equalsIgnoreCase(email)) {
                System.out.println("[ERROR] Email already registered.");
                return null;
            }
        }

        // Generate ID
        String newId = generateNextFanId(all);
        Fan newFan = new Fan(newId, name.trim(), email.trim(), phone.trim(), birthYear, password);
        fanRepo.addFan(newFan);
        System.out.println("[OK] Registration successful! Your Fan ID: " + newId);
        return newFan;
    }

    // ─────────────────────────────────────────────
    //  LOGIN
    // ─────────────────────────────────────────────
    /**
     * Authenticate by email + password.
     * @return Fan object if credentials match, null otherwise
     */
    public Fan login(String email, String password) {
        // Basic validation for email format before authentication
        if (email == null || password == null) return null;
        if (!Pattern.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", email)) {
            System.out.println("[ERROR] Invalid email format.");
            return null;
        }
        if (isAdminOrStaffEmail(email)) {
            System.out.println("[ERROR] Admin and Staff accounts cannot login as a Fan.");
            return null;
        }

        for (Fan f : fanRepo.getAllFans()) {
            if (f.getEmail().equalsIgnoreCase(email.trim()) && f.getPassword().equals(password)) {
                System.out.println("[OK] Welcome back, " + f.getName() + "!");
                return f;
            }
        }
        System.out.println("[ERROR] Invalid email or password.");
        return null;
    }

    // ─────────────────────────────────────────────
    //  GET MY TICKETS
    // ─────────────────────────────────────────────
    /**
     * Get all tickets belonging to a fan (via transactions).
     */
    public List<Ticket> getMyTickets(String fanId) {
        List<Ticket> myTickets = new ArrayList<>();
        List<Transaction> myTransactions = transactionRepo.findByFanId(fanId);

        for (Transaction tr : myTransactions) {
            if (tr.getStatus() == Transaction.Status.CANCELLED) continue;
            Ticket t = ticketRepo.findById(tr.getTicketId());
            if (t != null) myTickets.add(t);
        }
        return myTickets;
    }

    // ─────────────────────────────────────────────
    //  UPDATE PROFILE
    // ─────────────────────────────────────────────
    public boolean updateProfile(Fan updatedFan) {
        Fan existing = fanRepo.findById(updatedFan.getId());
        if (existing == null) {
            System.out.println("[ERROR] Fan not found.");
            return false;
        }
        fanRepo.updateFan(updatedFan);
        System.out.println("[OK] Profile updated.");
        return true;
    }

    private Fan loginFlow(Scanner sc) {
        Fan fan = null;
        while (fan == null) {
            System.out.println("\n  ===== LOGIN =====");
            System.out.print("  Email    : ");
            String email = sc.nextLine().trim();
            System.out.print("  Password : ");
            String pass = sc.nextLine().trim();
            fan = this.login(email, pass);
            if (fan == null) {
                System.out.println("[INFO] Please correct the above errors and try again.");
            }
        }
        return fan;
    }

    // ─────────────────────────────────────────────
    //  HELPERS
    // ─────────────────────────────────────────────
    public Fan findById(String fanId) {
        return fanRepo.findById(fanId);
    }

    public List<Fan> getAllFans() {
        return fanRepo.getAllFans();
    }

    private String generateNextFanId(List<Fan> fans) {
        int max = 0;
        for (Fan f : fans) {
            String num = f.getId().replaceAll("[^0-9]", "");
            try { max = Math.max(max, Integer.parseInt(num)); } catch (Exception ignored) {}
        }
        return "FAN" + (max + 1);
    }
}
