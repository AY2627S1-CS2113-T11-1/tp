package seedu.duke.command;

import seedu.duke.ExpenseList;
import seedu.duke.OrderList;
import seedu.duke.ProductList;
import seedu.duke.Stock;
import seedu.duke.StockList;
import seedu.duke.SystemException;
import seedu.duke.Ui;
import seedu.duke.Product;

public class AddStockCommand extends Command {
    private final String productName;
    private final double price;
    private final int quantity;

    public AddStockCommand(String productName, double price, int quantity) {
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
    }

    @Override
    public void execute(ProductList products, OrderList orders, ExpenseList expenses, StockList stocks, Ui ui) 
            throws SystemException {
        Stock existing = stocks.findByProduct(productName);
        if (existing != null) {
            refreshFromProduct(products, existing);
            existing.quantity += quantity;
            existing.price = price;
            syncProductStock(products, existing);
            ui.showStockUpdated(existing);
        } else {
            Stock created = new Stock(productName, price, quantity);
            stocks.add(created);
            syncProductStock(products, created);
            ui.showStockCreated(created);
        }
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

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }
}

