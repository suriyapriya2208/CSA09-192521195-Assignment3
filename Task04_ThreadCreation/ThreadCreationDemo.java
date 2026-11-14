class ScannerThread extends Thread {
    private final int stationId;
    private final int scripts;

    ScannerThread(int stationId, int scripts) {
        super("Thread-Station-" + stationId);
        this.stationId = stationId;
        this.scripts = scripts;
    }

    @Override
    public void run() {
        scan();
    }

    private void scan() {
        int scanned = 0;
        try {
            for (int i = 1; i <= scripts; i++) {
                Thread.sleep(80 + (stationId * 25L));
                scanned++;
            }
            System.out.println(getName() + " scanned " + scanned + " scripts.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println(getName() + " was interrupted.");
        }
    }
}

class RunnableScanner implements Runnable {
    private final int stationId;
    private final int scripts;

    RunnableScanner(int stationId, int scripts) {
        this.stationId = stationId;
        this.scripts = scripts;
    }

    @Override
    public void run() {
        int scanned = 0;
        try {
            for (int i = 1; i <= scripts; i++) {
                Thread.sleep(70 + (stationId * 20L));
                scanned++;
            }
            System.out.println("Runnable-Station-" + stationId
                    + " scanned " + scanned + " scripts.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Runnable-Station-" + stationId + " interrupted.");
        }
    }
}

public class ThreadCreationDemo {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Extending Thread ===");
        Thread[] stations = new Thread[4];

        for (int i = 0; i < stations.length; i++) {
            stations[i] = new ScannerThread(i + 1, 3);
            stations[i].start();
        }

        for (Thread station : stations) {
            System.out.println(station.getName() + " alive before join: "
                    + station.isAlive());
        }
        for (Thread station : stations) {
            station.join();
        }
        System.out.println("All Thread-based stations finished.");

        System.out.println("\n=== Implementing Runnable ===");
        Thread[] runnableStations = new Thread[4];
        for (int i = 0; i < runnableStations.length; i++) {
            Runnable job = new RunnableScanner(i + 1, 3);
            runnableStations[i] = new Thread(job, "Runnable-Worker-" + (i + 1));
            runnableStations[i].start();
        }
        for (Thread station : runnableStations) {
            station.join();
        }
        System.out.println("All Runnable-based stations finished.");
    }
}
