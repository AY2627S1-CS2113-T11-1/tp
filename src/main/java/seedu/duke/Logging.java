package seedu.duke;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Sends application diagnostics to rotating files instead of the terminal.
 */
public final class Logging {
    private static final Logger applicationLogger = Logger.getLogger("seedu.duke");
    private static final int FILE_LIMIT = 1_000_000;
    private static final int FILE_COUNT = 3;
    private static boolean isConfigured;

    private Logging() {
    }

    /**
     * Configures the shared application logger once, retaining existing feature log statements.
     * If files cannot be opened, the application continues without console diagnostics.
     */
    public static synchronized void configure() {
        if (isConfigured) {
            return;
        }
        applicationLogger.setUseParentHandlers(false);
        applicationLogger.setLevel(Level.FINE);
        try {
            Path directory = Path.of("logs");
            Files.createDirectories(directory);
            FileHandler handler = new FileHandler(directory.resolve("duke-%g.log").toString(),
                    FILE_LIMIT, FILE_COUNT, true);
            handler.setLevel(Level.FINE);
            handler.setFormatter(new SimpleFormatter());
            applicationLogger.addHandler(handler);
        } catch (IOException | SecurityException e) {
            // Logging must not interrupt commands when the working directory is not writable.
        }
        isConfigured = true;
    }
}
