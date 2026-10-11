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
        Stock existing = stocks.findByProduct(productName);
        if (existing == null) {
            ui.showStockNotFound();
            return;
        }

        refreshFromProduct(products, existing);
        existing.quantity -= quantityToRemove;
        if (existing.quantity < 0) {
            existing.quantity = 0;
        }
        syncProductStock(products, existing);

        ui.showStockRemoved(productName, quantityToRemove, defaultBehavior);
    }

    private void syncProductStock(ProductList products, Stock stock) {
        Product product = products.findByName(stock.product);
        if (product != null) {
            product.setStock(stock.quantity);
        }
    }

    private void refreshFromProduct(ProductList products, Stock stock) {
        Product product = products.findByName(stock.product);
        if (product != null) {
            stock.quantity = product.getStock();
        }
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantityToRemove() {
        return quantityToRemove;
    }
}

