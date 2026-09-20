package seedu.duke;

/**
 * Signals an error that the user can fix, such as a malformed command or a duplicate product.
 *
 * The message is shown to the user as-is, so it should say what went wrong and how to correct it.
 * Errors that the user cannot act on (bugs) should stay as ordinary unchecked exceptions.
 */
public class SystemException extends Exception {
    /**
     * Constructs an exception carrying a message meant to be read by the user.
     *
     * @param message the explanation shown in the CLI
     */
    public SystemException(String message) {
        super(message);
    }
}
