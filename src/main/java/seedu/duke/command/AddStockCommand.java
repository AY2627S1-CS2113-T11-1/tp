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
        Product product = products.findByName(productName);
        if (product == null) {
            throw new SystemException("Product \"" + productName + "\" does not exist in the catalogue. "
                    + "Add it first with: product add p/" + productName + " a/PRICE");
        }

        product.setStock(product.getStock() + quantity);

        Stock existing = stocks.findByProduct(productName);
        if (existing != null) {
            existing.quantity = product.getStock();
            existing.price = price;
            ui.showStockUpdated(existing);
        } else {
            Stock created = new Stock(productName, price, product.getStock());
            stocks.add(created);
            ui.showStockCreated(created);
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

