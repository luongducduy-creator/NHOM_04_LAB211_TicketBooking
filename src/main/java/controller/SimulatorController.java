package controller;

import model.transaction.Transaction;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Simulates concurrent seat‑booking operations using {@link CountDownLatch}
 * and an {@link ExecutorService}. Each worker thread attempts to book a seat
 * via {@link BookingController#bookSeat(String, String, String, Transaction.PaymentMethod)}.
 * The outcomes are collected and can be rendered by {@link view.SimulatorView}.
 */
public class SimulatorController {

    private final BookingController bookingController;

    public SimulatorController(BookingController bookingController) {
        this.bookingController = bookingController;
    }

    /**
     * Runs a booking simulation.
     *
     * @param fanIds   list of fan identifiers (one per thread)
     * @param matchId  the match for which seats are booked
     * @param seatIds  list of seat identifiers to attempt booking (same size as fanIds)
     * @param threads  number of parallel threads – typically equals fanIds.size()
     * @return a list of {@link BookingResult} describing each attempt
     */
    public List<BookingResult> runSimulation(List<String> fanIds,
                                            String matchId,
                                            List<String> seatIds,
                                            int threads) {
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
                    // Use CASH as a placeholder payment method
                    Transaction tx = bookingController.bookSeat(fanId, matchId, seatId,
                            Transaction.PaymentMethod.CASH);
                    BookingResult res;
                    if (tx != null) {
                        res = new BookingResult(fanId, matchId, seatId, true, tx.getTransactionId());
                    } else {
                        res = new BookingResult(fanId, matchId, seatId, false, null);
                    }
                    synchronized (lock) { results.add(res); }
                } catch (Exception e) {
                    synchronized (lock) { results.add(new BookingResult(fanId, matchId, seatId, false, null, e.getMessage())); }
                } finally {
                    latch.countDown();
                }
            });
        }
        try { latch.await(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        executor.shutdown();
        return results;
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
    }
}
