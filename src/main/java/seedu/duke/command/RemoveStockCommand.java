package seedu.duke.command;

import seedu.duke.ExpenseList;
import seedu.duke.OrderList;
import seedu.duke.ProductList;
import seedu.duke.Stock;
import seedu.duke.StockList;
import seedu.duke.SystemException;
import seedu.duke.Ui;
import seedu.duke.Product;

public class RemoveStockCommand extends Command {
    private final String productName;
    private final int quantityToRemove;
    private final boolean defaultBehavior;

    public RemoveStockCommand(String productName, int quantityToRemove, boolean defaultBehavior) {
        this.productName = productName;
        this.quantityToRemove = quantityToRemove;
        this.defaultBehavior = defaultBehavior;
    }

    @Override
    public void execute(ProductList products, OrderList orders, ExpenseList expenses, StockList stocks, Ui ui) 
            throws SystemException {
        Product product = products.findByName(productName);
        Stock existing = stocks.findByProduct(productName);
        if (product == null && existing == null) {
            ui.showStockNotFound();
            return;
        }

        int currentStock = product != null ? product.getStock() : existing.quantity;
        int newStock = Math.max(0, currentStock - quantityToRemove);

        if (product != null) {
            product.setStock(newStock);
        }
        if (existing != null) {
            existing.quantity = newStock;
        }

        ui.showStockRemoved(productName, quantityToRemove, defaultBehavior);
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantityToRemove() {
        return quantityToRemove;
    }
}

