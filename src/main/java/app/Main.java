package app;

import controller.BookingController;
import controller.FanController;
import controller.StadiumController;
import view.MainView;

import java.util.Scanner;

/**
 * Application entry point.
 * MVC wiring:
 *   Repositories ← Controllers ← Views ← MainView
 */
public class Main {

    public static void main(String[] args) {

        // ── Shared scanner (one instance for whole app) ──
        Scanner sc = new Scanner(System.in);

        // ── Controller layer (shared repos so both controllers see same data) ──
        StadiumController stadiumCtrl = new StadiumController();
        BookingController bookingCtrl = new BookingController(stadiumCtrl);
        FanController     fanCtrl     = new FanController(bookingCtrl.getTransactionRepo(),
                                                          bookingCtrl.getTicketRepo());

        // ── View layer (MVC entry point) ──
        MainView mainView = new MainView(fanCtrl, stadiumCtrl, bookingCtrl, sc);

        // ── Start application ──
        mainView.start();

        sc.close();
    }
}