class Wallet {
    private final int id;
    private int balance;

    Wallet(int id, int balance) {
        this.id = id;
        this.balance = balance;
    }

    int getId() {
        return id;
    }

    synchronized void deposit(int amount) {
        balance += amount;
    }

    synchronized boolean withdraw(int amount) {
        if (balance < amount) return false;
        balance -= amount;
        return true;
    }

    int getBalance() {
        return balance;
    }
}

public class WalletTransferDemo {
    // Safe: both threads acquire locks in ascending wallet-id order.
    static void transferSafely(Wallet first, Wallet second, int amount) {
        Wallet low = first.getId() < second.getId() ? first : second;
        Wallet high = first.getId() < second.getId() ? second : first;

        synchronized (low) {
            synchronized (high) {
                if (first.withdraw(amount)) {
                    second.deposit(amount);
                }
            }
        }
    }

    // Unsafe pattern for analysis only: opposite transfer directions can
    // acquire the same two locks in opposite orders and deadlock.
    static void transferUnsafely(Wallet from, Wallet to, int amount) {
        synchronized (from) {
            synchronized (to) {
                if (from.withdraw(amount)) to.deposit(amount);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Wallet walletA = new Wallet(1, 1000);
        Wallet walletB = new Wallet(2, 1000);

        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                transferSafely(walletA, walletB, 1);
            }
        });
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                transferSafely(walletB, walletA, 1);
            }
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("Wallet A balance: " + walletA.getBalance());
        System.out.println("Wallet B balance: " + walletB.getBalance());
        System.out.println("Combined balance: "
                + (walletA.getBalance() + walletB.getBalance()));
        System.out.println("The combined balance should remain 2000.");
    }
}
