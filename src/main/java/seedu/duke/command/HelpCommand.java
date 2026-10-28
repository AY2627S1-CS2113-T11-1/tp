package seedu.duke.command;

import java.util.List;
import java.util.Locale;

import seedu.duke.ExpenseList;
import seedu.duke.OrderList;
import seedu.duke.ProductList;
import seedu.duke.StockList;
import seedu.duke.SystemException;
import seedu.duke.Ui;

/**
 * Displays command descriptions and formats for one area of the application.
 */
public class HelpCommand extends Command {
    public static final String USAGE = "Format: help {AREA}. Available areas: "
            + "product, stock, order, sales, generate, expense, finances, general.";
    private final String area;

    /**
     * Creates a help command for an area, or an overview when no area is supplied.
     *
     * @param area requested area name
     */
    public HelpCommand(String area) {
        this.area = area.trim().toLowerCase(Locale.ROOT);
    }

    /**
     * Displays help without changing application data.
     *
     * @throws SystemException if the area is not supported
     */
    @Override
    public void execute(ProductList products, OrderList orders, ExpenseList expenses, StockList stocks, Ui ui)
            throws SystemException {
        if (area.isEmpty()) {
            ui.showHelpOverview(USAGE);
            return;
        }
        List<String[]> commands;
        switch (area) {
        case "product":
            commands = List.of(
                    new String[] {"Adds a product to the catalogue with its selling price and zero stock.",
                        "product add p/PRODUCT a/AMOUNT"},
                    new String[] {"Deletes a product and its stock record. Not allowed while an active order has it.",
                        "product delete p/PRODUCT"},
                    new String[] {"Lists products alphabetically with their prices and stock quantities.",
                        "product list"});
            break;
        case "stock":
            commands = List.of(
                    new String[] {"Adds stock for an existing catalogue product using its selling price.",
                        "stock add p/PRODUCT q/QUANTITY"},
                    new String[] {"Removes stock, defaulting to one unit when quantity is omitted."
                            + " Stock cannot fall below zero.", "stock remove p/PRODUCT [q/QUANTITY]"},
                    new String[] {"Lists products currently in the stock list and their quantities.", "stock list"},
                    new String[] {"Lists products with stock at or below a threshold (default 5).",
                        "stock low [q/THRESHOLD]"});
            break;
        case "order":
            commands = List.of(
                    new String[] {"Creates an order and deducts stock. Repeat product and quantity pairs to add items.",
                        "order add c/CUSTOMER p/PRODUCT q/QUANTITY [p/PRODUCT q/QUANTITY]..."},
                    new String[] {"Cancels an order and restores its stock. Order numbers are kept in the history.",
                        "order cancel ORDER_NUMBER"},
                    new String[] {"Lists all orders, including cancelled orders, oldest first.", "order list"},
                    new String[] {"Edits an active order's customer, product or quantity, adjusting stock to match.",
                        "order edit ORDER_NUMBER [c/CUSTOMER] [p/PRODUCT] [q/QUANTITY]"},
                    new String[] {"Applies, replaces or removes (d/0) a percentage discount on an active order.",
                        "order discount ORDER_NUMBER d/PERCENTAGE"},
                    new String[] {"Records a customer's payment for all of their unpaid, non-cancelled orders.",
                        "order add pay c/CUSTOMER a/AMOUNT"},
                    new String[] {"Shows each of a customer's orders with its items, total and status.",
                        "order details c/CUSTOMER"});
            break;
        case "sales":
            commands = List.<String[]>of(new String[] {
                "Lists confirmed sales and their total, excluding cancelled orders.", "sales list"});
            break;
        case "finances":
            commands = List.<String[]>of(new String[] {
                "Shows total orders, payments received, expenses and the overall profit or loss.", "finances"});
            break;
        case "expense":
            commands = List.of(
                    new String[] {"Records an operating expense with its description and amount.",
                        "expense add p/PRODUCT a/AMOUNT"},
                    new String[] {"Lists recorded expenses in the order they were added.", "expense list"});
            break;
        case "generate":
            commands = List.<String[]>of(new String[] {
                "Shows individual receipts for a customer's non-cancelled orders.", "generate receipts c/CUSTOMER"});
            break;
        case "general":
            commands = List.of(
                    new String[] {"Lists help areas, or shows commands for the requested area.", "help [AREA]"},
                    new String[] {"Exits the application.", "exit"});
            break;
        default:
            throw new SystemException("Unknown help area \"" + area + "\". " + USAGE);
        }
        String title = area.substring(0, 1).toUpperCase(Locale.ROOT) + area.substring(1);
        ui.showHelp(title, commands);
    }
}
