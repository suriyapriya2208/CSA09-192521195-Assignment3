import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class SalesReportGenerator {
    private static final List<String> INPUTS =
            List.of("store1.txt", "store2.txt", "store3.txt");

    private static double readTotal(Path file) throws IOException {
        double total = 0;
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line;
            while ((line = reader.readLine()) != null) {
                total += Double.parseDouble(line.trim());
            }
        }
        return total;
    }

    // Manual close: the finally block attempts to close resources.
    private static void generateManualReport(Path output) {
        BufferedWriter writer = null;
        try {
            writer = Files.newBufferedWriter(output,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            double grandTotal = 0;
            for (String name : INPUTS) {
                Path file = Path.of(name);
                System.out.println("Manual version reading: " + name);
                double storeTotal = readTotal(file);
                writer.write(name + " total = " + storeTotal);
                writer.newLine();
                grandTotal += storeTotal;
            }
            writer.write("GRAND TOTAL = " + grandTotal);
            writer.newLine();
        } catch (IOException | NumberFormatException e) {
            System.out.println("Manual report failed: " + e.getMessage());
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException closeError) {
                    System.out.println("Manual close failed: " + closeError.getMessage());
                }
            }
        }
    }

    // try-with-resources closes the writer automatically, including on failure.
    private static void generateSafeReport(Path output) {
        try (BufferedWriter writer = Files.newBufferedWriter(output,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            double grandTotal = 0;
            for (String name : INPUTS) {
                Path file = Path.of(name);
                System.out.println("Safe version reading: " + name);
                double storeTotal = readTotal(file);
                writer.write(name + " total = " + storeTotal);
                writer.newLine();
                grandTotal += storeTotal;
            }
            writer.write("GRAND TOTAL = " + grandTotal);
            writer.newLine();
        } catch (IOException | NumberFormatException e) {
            System.out.println("Safe report failed: " + e.getMessage());
            System.out.println("Log: failure occurred while processing one of the store files.");
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Manual close version ===");
        generateManualReport(Path.of("manual-report.txt"));

        System.out.println("\n=== Try-with-resources version ===");
        generateSafeReport(Path.of("safe-report.txt"));

        System.out.println("\nCompare manual-report.txt and safe-report.txt.");
        System.out.println("Both should contain the same totals when all input files are valid.");
    }
}
