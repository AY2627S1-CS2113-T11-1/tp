package seedu.duke;

import java.util.Scanner;
import seedu.duke.command.Command;


/**
 * Entry point of the system.
 *
 * Runs the read-parse-execute loop: a line is read from the user, turned into a
 * {@link Command} by the {@link Parser}, and executed against the {@link ProductList}.
 */
public class Duke {

    /**
     * Starts the system and runs until the user exits or input ends.
     *
     * @param args command line arguments, which the system does not use
     */
    public static void main(String[] args) {
        //@@author BA-reused
        //Reused from /ip/src/main/java/bryte/Bryte.java
        //with minor modifications
        Scanner scanner = new Scanner(System.in);
        boolean isRunning = true;

        Ui ui = new Ui();
        ui.printDivider();
        ui.showWelcome();
        ui.printDivider();
        Parser parser = new Parser();
        ProductList products = new ProductList();
        OrderList orders = new OrderList();
        ExpenseList expenses = new ExpenseList();

        StockList stocks = new StockList();

        while (isRunning && scanner.hasNextLine()) {
            String command = scanner.nextLine().trim();
            if (command.isEmpty()) {
                continue;
            }
            ui.printDivider();
            try {
                Command parsedCommand = parser.parse(command);
                parsedCommand.execute(products, orders, expenses, stocks, ui);
                isRunning = !parsedCommand.isExit();
            } catch (SystemException e) {
                // A mistake in one command should never end the session.
                ui.showError(e.getMessage());
            }
            ui.printDivider();
            System.out.println();
        }
        scanner.close();
    }

    private static String parseField(String input, String prefix) {
        String match = " " + prefix;
        String paddedInput = " " + input;
        int startIndex = paddedInput.indexOf(match);
        if (startIndex == -1) {
            return null;
        }
        startIndex += match.length();

        int endIndex = paddedInput.length();
        String[] otherPrefixes = {" p/", " a/", " q/"};
        for (String other : otherPrefixes) {
            if (other.equals(match)) {
                continue;
            }
            int nextIndex = paddedInput.indexOf(other, startIndex);
            if (nextIndex != -1 && nextIndex < endIndex) {
                endIndex = nextIndex;
            }
        }

        return paddedInput.substring(startIndex, endIndex).trim();
    }

}
