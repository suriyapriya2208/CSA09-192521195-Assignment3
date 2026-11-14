import java.io.IOException;

class StorageExamException extends Exception {
    StorageExamException(String message) {
        super(message);
    }

    StorageExamException(String message, Throwable cause) {
        super(message, cause);
    }
}

class StorageSubmissionException extends StorageExamException {
    StorageSubmissionException(String message, Throwable cause) {
        super(message, cause);
    }
}

class Storage {
    void save(String fileName, long sizeKb) throws StorageSubmissionException {
        try {
            if (sizeKb > 500) {
                throw new IOException("Disk quota exceeded");
            }
            System.out.println("Saved " + fileName);
        } catch (IOException e) {
            // Keep the original IOException as the cause.
            throw new StorageSubmissionException("Could not store " + fileName, e);
        }
    }
}

public class StorageDemo {
    public static void main(String[] args) {
        Storage storage = new Storage();
        try {
            storage.save("answer01.pdf", 120);
            storage.save("answer02.pdf", 800);
        } catch (StorageExamException e) {
            System.out.println("Error: " + e.getMessage());
            System.out.println("Cause: " + e.getCause().getMessage());
            System.out.println("\nChained stack trace:");
            e.printStackTrace(System.out);
        }
    }
}
