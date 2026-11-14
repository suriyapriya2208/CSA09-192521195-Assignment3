import java.io.IOException;

class ExamException extends Exception {
    public ExamException(String message) {
        super(message);
    }

    public ExamException(String message, Throwable cause) {
        super(message, cause);
    }
}

class ExamWindowClosedException extends ExamException {
    public ExamWindowClosedException(String message) {
        super(message);
    }
}

class StudentNotRegisteredException extends ExamException {
    public StudentNotRegisteredException(String message) {
        super(message);
    }
}

class SubmissionStorageException extends ExamException {
    public SubmissionStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}

class ExamStorage {
    void save(String fileName, long sizeKb) throws SubmissionStorageException {
        try {
            if (sizeKb > 500) {
                throw new IOException("Disk quota exceeded");
            }
            System.out.println("Storage: saved " + fileName);
        } catch (IOException e) {
            throw new SubmissionStorageException("Could not store " + fileName, e);
        }
    }
}

class ExamService {
    private final ExamStorage storage = new ExamStorage();

    void submit(String studentId, boolean registered, boolean examOpen,
                String fileName, long sizeKb) throws ExamException {
        if (!examOpen) {
            throw new ExamWindowClosedException("The examination window has closed.");
        }
        if (!registered) {
            throw new StudentNotRegisteredException("Student is not registered for this course.");
        }
        storage.save(fileName, sizeKb);
    }
}

class ExamController {
    private final ExamService service = new ExamService();

    void submitRequest(String studentId, boolean registered, boolean examOpen,
                       String fileName, long sizeKb) {
        try {
            service.submit(studentId, registered, examOpen, fileName, sizeKb);
            System.out.println("Controller: submission accepted for " + studentId);
        } catch (ExamException e) {
            System.out.println("Student sees: " + e.getMessage());
            if (e.getCause() != null) {
                System.out.println("Technical cause: " + e.getCause().getMessage());
            }
        }
    }
}

public class ExamPortalDemo {
    public static void main(String[] args) {
        ExamController controller = new ExamController();

        System.out.println("CASE 1 – Valid submission");
        controller.submitRequest("STU101", true, true, "answer01.pdf", 120);

        System.out.println("\nCASE 2 – Exam window closed");
        controller.submitRequest("STU102", true, false, "answer02.pdf", 120);

        System.out.println("\nCASE 3 – Student not registered");
        controller.submitRequest("STU103", false, true, "answer03.pdf", 120);

        System.out.println("\nCASE 4 – File exceeds storage limit");
        controller.submitRequest("STU104", true, true, "answer04.pdf", 800);
    }
}
