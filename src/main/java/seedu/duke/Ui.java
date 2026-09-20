package seedu.duke;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

/**
 * Handles all reading from and writing to the user.
 *
 * Keeping every {@code System.out.println} in this one class means the rest of the system can be
 * unit-tested without capturing console output, and the wording of a message only ever
 * changes in one place.
 */
public class Ui {
    /**
     * Column layout of one catalogue line: name padded to 18 characters, then price padded to 8.
     */
    private static final String PRODUCT_LINE_FORMAT = "   %-18s%-8sstock %d";

    private final Scanner scanner;
    private final PrintStream out;

    /**
     * Constructs a Ui that reads from standard input and writes to standard output.
     */
    public Ui() {
        this(System.in, System.out);
    }

    /**
     * Constructs a Ui that reads from and writes to the given streams.
     *
     * Tests use this constructor to feed in scripted input and inspect the exact output.
     *
     * @param in  the stream commands are read from
     * @param out the stream messages are written to
     */
    public Ui(InputStream in, PrintStream out) {
        this.scanner = new Scanner(in);
        this.out = out;
    }

    /**
     * Returns the next line typed by the user, or null if there is no more input.
     *
     * Null signals end of input (for example, the user pressed Ctrl-D or input was piped in),
     * which lets the main loop stop instead of throwing.
     */
    public String readCommand() {
        if (!scanner.hasNextLine()) {
            return null;
        }
        return scanner.nextLine();
    }

    public void showWelcome() {
        out.println("Welcome to the management system.");
        out.println("Type a command, or \"bye\" to exit.");
    }

    public void showGoodbye() {
        out.println("Goodbye!");
    }

    /**
     * Shows the confirmation for a newly added product.
     *
     * @param product the product that was just added
     */
    public void showProductAdded(Product product) {
        out.println("Added product " + product.getName());
        out.println("Price: " + product.getPriceString());
        out.println("Stock: " + product.getStock());
    }

    /**
     * Shows the catalogue, one product per line, in the order given.
     *
     * @param products the products to display, already sorted by the caller
     */
    public void showProducts(List<Product> products) {
        out.println("Products (" + products.size() + "):");
        if (products.isEmpty()) {
            out.println("   Nothing here yet. Add one with: product add p/PRODUCT a/AMOUNT");
        } else {
            for (Product product : products) {
                out.println(String.format(PRODUCT_LINE_FORMAT,
                        product.getName(), product.getPriceString(), product.getStock()));
            }
        }
    }

    public void showError(String message) {
        out.println("Sorry! " + message);
    }
}
