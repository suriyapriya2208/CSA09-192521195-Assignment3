import java.util.concurrent.CountDownLatch;

class BookingSystem {
    private final boolean[] booked;
    private int bookingsMade;

    BookingSystem(int numberOfSeats) {
        booked = new boolean[numberOfSeats];
    }

    // Intentionally unsafe: the check and update are not atomic.
    void bookWithoutSync(int seat) {
        if (!booked[seat]) {
            try {
                Thread.sleep(2); // widens the race window for demonstration
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            booked[seat] = true;
            bookingsMade++;
        }
    }

    // The whole method is protected by this object's intrinsic lock.
    synchronized void bookSynchronizedMethod(int seat) {
        if (!booked[seat]) {
            booked[seat] = true;
            bookingsMade++;
        }
    }

    // Only the critical section is protected by the intrinsic lock.
    void bookSynchronizedBlock(int seat) {
        synchronized (this) {
            if (!booked[seat]) {
                booked[seat] = true;
                bookingsMade++;
            }
        }
    }

    int getBookingsMade() {
        return bookingsMade;
    }
}

public class SeatBooking {
    private static final int SEATS = 10;
    private static final int USERS = 100;

    private static int runTest(int mode) throws InterruptedException {
        BookingSystem system = new BookingSystem(SEATS);
        CountDownLatch ready = new CountDownLatch(USERS);
        CountDownLatch start = new CountDownLatch(1);
        Thread[] users = new Thread[USERS];

        for (int i = 0; i < USERS; i++) {
            final int seat = i % SEATS;
            users[i] = new Thread(() -> {
                ready.countDown();
                try {
                    start.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                if (mode == 0) system.bookWithoutSync(seat);
                else if (mode == 1) system.bookSynchronizedMethod(seat);
                else system.bookSynchronizedBlock(seat);
            });
            users[i].start();
        }

        ready.await();
        start.countDown();
        for (Thread user : users) user.join();
        return system.getBookingsMade();
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Unsynchronized version – 10 runs:");
        for (int i = 1; i <= 10; i++) {
            System.out.println("Run " + i + ": Seats sold = " + runTest(0));
        }

        System.out.println("\nSynchronized method:");
        System.out.println("Seats sold = " + runTest(1));

        System.out.println("\nSynchronized block:");
        System.out.println("Seats sold = " + runTest(2));
        System.out.println("\nExpected for each synchronized version: 10");
    }
}
