package seedu.duke.command;

import seedu.duke.OrderList;
import seedu.duke.ProductList;
import seedu.duke.SystemException;
import seedu.duke.Ui;

/**
 * Represents a user command that has been parsed and is ready to run.
 *
 * Each command is its own subclass so that adding a feature means adding a class rather than
 * extending a growing switch statement, and so each command can be tested on its own.
 */
public abstract class Command {
    /**
     * Executes this command against the system's data, reporting the outcome through the UI.
     *
     * Every command receives all the data lists, even ones it does not use, so that the main loop
     * can run any command the same way.
     *
     * @param products the catalogue the command acts on
     * @param orders   the orders the command acts on
     * @param ui       the UI used to show the outcome
     * @throws SystemException if the command cannot be carried out, e.g. a duplicate product
     */
    public abstract void execute(ProductList products, OrderList orders, Ui ui) throws SystemException;

    /**
     * Returns true if the system should exit after running this command.
     *
     * Only {@link ExitCommand} overrides this, so the default is false.
     */
    public boolean isExit() {
        return false;
    }
}
