package seedu.duke.command;

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
    public void execute(ProductList products, OrderList orders, ExpenseList expenses, Ui ui)
            throws SystemException {
        Product product = new Product(name, price);
        products.add(product);
        ui.showProductAdded(product);
    }
}
