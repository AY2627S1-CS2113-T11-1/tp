package seedu.duke.command;

import java.util.logging.Logger;

import seedu.duke.ExpenseList;
import seedu.duke.OrderList;
import seedu.duke.Product;
import seedu.duke.ProductList;
import seedu.duke.Stock;
import seedu.duke.StockList;
import seedu.duke.SystemException;
import seedu.duke.Ui;

/**
 * Adds stock to an existing product in the catalogue and updates or creates a corresponding stock entry.
 *
 * Corresponds to {@code stock add p/PRODUCT q/QUANTITY}.
 */
public class AddStockCommand extends Command {
    /** Records stock modification events for debugging and auditing. */
    private static final Logger logger = Logger.getLogger(AddStockCommand.class.getName());

    private final String productName;
    private final int quantity;

    /**
     * Constructs a command that adds a given quantity of stock.
     *
     * @param productName the name of the product, validated as non-empty by the parser
     * @param quantity    the quantity to add, validated as positive
     */
    public AddStockCommand(String productName, int quantity) {
        assert productName != null && !productName.isBlank()
                : "Parser must supply a non-empty product name";
        assert quantity > 0 : "Quantity added must be positive";

        this.productName = productName;
        this.quantity = quantity;
    }

    /**
     * Executes the stock addition against the product catalogue and stock list.
     *
     * @param products the product catalogue
     * @param orders   the order list
     * @param expenses the shared expense list
     * @param stocks   the stock list
     * @param ui       the user interface
     * @throws SystemException if the product does not exist in the catalogue
     */
    @Override
    public void execute(ProductList products, OrderList orders, ExpenseList expenses, StockList stocks, Ui ui)
            throws SystemException {
        assert products != null : "Product catalogue must not be null";
        assert stocks != null : "Stock list must not be null";
        assert ui != null : "Ui must not be null";

        Product product = products.findByName(productName);
        if (product == null) {
            logger.warning("Stock addition rejected: product \"" + productName + "\" does not exist in catalogue");
            throw new SystemException("Product \"" + productName + "\" does not exist in the catalogue. "
                    + "Add it first with: product add p/" + productName + " a/PRICE");
        }

        int previousStock = product.getStock();
        product.setStock(previousStock + quantity);
        assert product.getStock() >= quantity : "Updated stock must be at least the added quantity";

        Stock existing = stocks.findByProduct(productName);
        if (existing != null) {
            existing.setQuantity(product.getStock());
            existing.setPrice(product.getPrice());
            logger.info("Updated existing stock for product \"" + productName + "\" to quantity "
                    + product.getStock());
            ui.showStockUpdated(existing);
        } else {
            Stock created = new Stock(product, product.getStock());
            stocks.add(created);
            logger.info("Created new stock entry for product \"" + productName + "\" with quantity "
                    + product.getStock());
            ui.showStockCreated(created);
        }
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }
}

