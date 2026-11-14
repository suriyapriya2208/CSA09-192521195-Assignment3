import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

class InsufficientFundsException extends Exception {
    public InsufficientFundsException(String message) {
        super(message);
    }
}

public class ATM {
    private static final double BALANCE = 5000.00;

    public static void main(String[] args) {
        try (Scanner input = new Scanner(System.in)) {
            System.out.print("Enter account number: ");
            String accountNumber = input.nextLine().trim();

            if (!accountNumber.matches("\\d{6,12}")) {
                throw new IllegalArgumentException("Account number must contain 6 to 12 digits.");
            }

            System.out.print("Enter card-reader file name (card-reader.txt): ");
            String fileName = input.nextLine().trim();
            if (fileName.isEmpty()) {
                fileName = "card-reader.txt";
            }

            // Reading the file represents checking that the card reader is available.
            String cardStatus = Files.readString(Path.of(fileName)).trim();
            if (!cardStatus.equalsIgnoreCase("READY")) {
                throw new IOException("Card reader is not ready.");
            }

            System.out.print("Enter withdrawal amount: ");
            String amountText = input.nextLine().trim();
            double amount = Double.parseDouble(amountText);

            if (amount <= 0) {
                throw new IllegalArgumentException("Withdrawal amount must be greater than zero.");
            }
            if (amount > BALANCE) {
                throw new InsufficientFundsException("Insufficient account balance.");
            }

            System.out.printf("Withdrawal successful. Cash dispensed: Rs. %.2f%n", amount);
            System.out.printf("Remaining balance: Rs. %.2f%n", BALANCE - amount);

        } catch (NumberFormatException e) {
            System.out.println("Invalid input: enter the withdrawal amount using numbers only.");
        } catch (InsufficientFundsException e) {
            System.out.println("Transaction declined: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Card-reader/file error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid details: " + e.getMessage());
        } finally {
            System.out.println("ATM session ended. Please collect your card.");
        }
    }
}
