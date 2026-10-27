package seedu.duke.command;

import seedu.duke.StockList;
import seedu.duke.Expense;
import seedu.duke.ExpenseList;
import seedu.duke.OrderList;
import seedu.duke.ProductList;
import seedu.duke.Ui;

/**
 * Adds an operating expense to the expense list.
 *
 * Corresponds to {@code expense add p/PRODUCT a/AMOUNT}.
 */
public class AddExpenseCommand extends Command {
    private final String productName;
    private final double amount;

    /**
     * Creates an add-expense command.
     *
     * @param productName the product associated with the expense
     * @param amount the expense amount in dollars
     */
    public AddExpenseCommand(String productName, double amount) {
        this.productName = productName;
        this.amount = amount;
    }

    public String getProductName() {
        return productName;
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public void execute(ProductList products, OrderList orders, ExpenseList expenses, StockList stocks, Ui ui) {
        assert expenses != null : "Expense list must be available when adding an expense";
        assert ui != null : "UI must be available when adding an expense";

        Expense expense = new Expense(productName, amount);
        expenses.add(expense);
        ui.showExpenseAdded(expense);
    }
}
