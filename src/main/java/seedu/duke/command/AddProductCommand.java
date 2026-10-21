package seedu.duke.command;

import java.util.logging.Logger;

import seedu.duke.StockList;
import seedu.duke.Product;
import seedu.duke.ExpenseList;
import seedu.duke.OrderList;
import seedu.duke.ProductList;
import seedu.duke.SystemException;
import seedu.duke.Ui;

/**
 * Adds a new product to the catalogue at zero stock.
 *
 * Corresponds to {@code product add p/PRODUCT a/AMOUNT}.
 */
public class AddProductCommand extends Command {
    /**
     * Records catalogue changes for debugging.
     *
     * Messages are logged at FINE, which the default console handler does not print, so they never
     * appear in the user's CLI output. Raise the level to FINE in a logging config to see them.
     */
    private static final Logger logger = Logger.getLogger(AddProductCommand.class.getName());

    private final String name;
    private final double price;

    /**
     * Constructs a command that adds a product with the given name and unit price.
     *
     * @param name  the product name, already validated as non-empty by the parser
     * @param price the unit price in dollars, already validated as non-negative by the parser
     */
    public AddProductCommand(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public void execute(ProductList products, OrderList orders, ExpenseList expenses, StockList stocks, Ui ui)
            throws SystemException {
        Product product = new Product(name, price);
        try {
            products.add(product);
        } catch (SystemException e) {
            // A duplicate name is ordinary user error, so it is only logged quietly before being reported.
            logger.fine("Rejected product add, name already in catalogue: " + name);
            throw e;
        }
        logger.fine("Added product " + name + " at " + product.getPriceString()
                + "; catalogue size is now " + products.size());
        ui.showProductAdded(product);
    }
}
