import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

class ProcessingResult {
    final String fileName;
    final boolean success;
    final int score;
    final String message;

    ProcessingResult(String fileName, boolean success, int score, String message) {
        this.fileName = fileName;
        this.success = success;
        this.score = score;
        this.message = message;
    }
}

class ScoreFileTask implements Callable<ProcessingResult> {
    private final Path file;

    ScoreFileTask(Path file) {
        this.file = file;
    }

    @Override
    public ProcessingResult call() {
        try {
            String content = Files.readString(file).trim();
            int score = Integer.parseInt(content);
            if (score < 0 || score > 100) {
                throw new IllegalArgumentException("Score must be between 0 and 100.");
            }
            return new ProcessingResult(file.getFileName().toString(),
                    true, score, "Processed");
        } catch (IOException | IllegalArgumentException e) {
            return new ProcessingResult(file.getFileName().toString(),
                    false, 0, e.getMessage());
        }
    }
}

public class ResultProcessor {
    // Use 10_000 for the full assignment-scale experiment.
    private static final int FILE_COUNT = 100;

    private static void createSampleFiles(Path folder) throws IOException {
        Files.createDirectories(folder);
        for (int i = 1; i <= FILE_COUNT; i++) {
            Path file = folder.resolve("student" + i + ".txt");
            if (i % 17 == 0) {
                Files.writeString(file, "CORRUPTED_SCORE");
            } else {
                Files.writeString(file, String.valueOf((i * 7) % 101));
            }
        }
        // Deliberately create no file named missing-student.txt.
    }

    private static void processWithPool(List<Path> files, int poolSize)
            throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(poolSize);
        ConcurrentLinkedQueue<ProcessingResult> results =
                new ConcurrentLinkedQueue<>();
        long start = System.nanoTime();

        try {
            List<Future<ProcessingResult>> futures = new ArrayList<>();
            for (Path file : files) {
                futures.add(executor.submit(new ScoreFileTask(file)));
            }

            // Include one missing-file case to test failure handling.
            futures.add(executor.submit(
                    new ScoreFileTask(Path.of("score-files", "missing-student.txt"))));

            for (Future<ProcessingResult> future : futures) {
                try {
                    results.add(future.get());
                } catch (ExecutionException e) {
                    results.add(new ProcessingResult("unknown", false, 0,
                            e.getCause().toString()));
                }
            }
        } finally {
            executor.shutdown();
        }

        long elapsedMs = (System.nanoTime() - start) / 1_000_000;
        int success = 0;
        int failure = 0;
        long totalScore = 0;

        for (ProcessingResult result : results) {
            if (result.success) {
                success++;
                totalScore += result.score;
            } else {
                failure++;
                System.out.println("Failed: " + result.fileName
                        + " | Reason: " + result.message);
            }
        }

        System.out.println("\nPool size: " + poolSize);
        System.out.println("Successful files: " + success);
        System.out.println("Failed files: " + failure);
        System.out.println("Total of valid scores: " + totalScore);
        System.out.println("Time taken: " + elapsedMs + " ms");
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Path folder = Path.of("score-files");
        createSampleFiles(folder);

        List<Path> files = new ArrayList<>();
        for (int i = 1; i <= FILE_COUNT; i++) {
            files.add(folder.resolve("student" + i + ".txt"));
        }

        System.out.println("Processing " + files.size()
                + " score files (plus one missing-file test).");

        for (int poolSize : new int[]{1, 2, 4, 8}) {
            processWithPool(files, poolSize);
        }
    }
}
