package seedu.duke.command;

import seedu.duke.ExpenseList;
import seedu.duke.OrderList;
import seedu.duke.Product;
import seedu.duke.ProductList;
import seedu.duke.Stock;
import seedu.duke.StockList;
import seedu.duke.SystemException;
import seedu.duke.Ui;

public class AddStockCommand extends Command {
    private final String productName;
    private final int quantity;

    public AddStockCommand(String productName, int quantity) {
        this.productName = productName;
        this.quantity = quantity;
    }

    @Override
    public void execute(ProductList products, OrderList orders, ExpenseList expenses, StockList stocks, Ui ui)
            throws SystemException {
        Product product = products.findByName(productName);
        if (product == null) {
            throw new SystemException("Product \"" + productName + "\" does not exist in the catalogue. "
                    + "Add it first with: product add p/" + productName + " a/PRICE");
        }

        product.setStock(product.getStock() + quantity);

        Stock existing = stocks.findByProduct(productName);
        if (existing != null) {
            existing.quantity = product.getStock();
            existing.price = product.getPrice();
            ui.showStockUpdated(existing);
        } else {
            Stock created = new Stock(productName, product.getPrice(), product.getStock());
            stocks.add(created);
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

