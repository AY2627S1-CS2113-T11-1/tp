package seedu.duke;

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
        Ui ui = new Ui();
        Parser parser = new Parser();
        ProductList products = new ProductList();

        ui.showWelcome();
        boolean isExit = false;
        while (!isExit) {
            String input = ui.readCommand();
            if (input == null) {
                break;
            }
            try {
                Command command = parser.parse(input);
                command.execute(products, ui);
                isExit = command.isExit();
            } catch (SystemException e) {
                // A mistake in one command should never end the session.
                ui.showError(e.getMessage());
            }
        }
    }
}
