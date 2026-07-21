package ticket;

import java.util.concurrent.*;

public class SyncBookingTest {
    public static void main(String[] args) throws InterruptedException {
        BookingRepository repo = new BookingRepository(3); // capacity = 3 seats
        ExecutorService executor = Executors.newFixedThreadPool(5);

        for (int i = 0; i < 5; i++) {
            final int userId = i;
            executor.submit(() -> repo.bookTicket("User-" + userId));
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        System.out.println("Tổng số vé đặt thành công: " + repo.getBookedSeats());
    }
}

class BookingRepository {
    private int capacity;
    private int bookedSeats = 0;

    public BookingRepository(int capacity) {
        this.capacity = capacity;
    }

    public synchronized boolean bookTicket(String user) {
        if (bookedSeats < capacity) {
            bookedSeats++;
            System.out.println(user + " đặt vé thành công. Tổng vé: " + bookedSeats);
            return true;
        } else {
            System.out.println(user + " không thể đặt vé (hết chỗ).");
            return false;
        }
    }

    public int getBookedSeats() {
        return bookedSeats;
    }
}

