package seedu.duke.command;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.duke.Expense;
import seedu.duke.ExpenseList;
import seedu.duke.OrderItem;
import seedu.duke.OrderList;
import seedu.duke.Product;
import seedu.duke.ProductList;
import seedu.duke.SystemException;
import seedu.duke.Ui;

/** Checks the financial report's totals and profit/loss label. */
class FinancesCommandTest {
    @Test
    public void execute_expensesExceedSales_displaysLoss() throws SystemException {
        OrderList orders = new OrderList();
        var order = orders.create("Alice", List.of(new OrderItem(new Product("Apple", 2.50), 2)));
        order.recordPayment(java.math.BigDecimal.valueOf(5).setScale(2));
        ExpenseList expenses = new ExpenseList();
        expenses.add(new Expense("Rent", 10.00));

        String output = executeReport(orders, expenses);

        assertTrue(output.contains("Total Orders (including unpaid): $5.00"));
        assertTrue(output.contains("Total Sales: $5.00"));
        assertTrue(output.contains("Total Expenses: $10.00"));
        assertTrue(output.contains("Overall Loss: $5.00"));
    }

    @Test
    public void execute_salesExceedExpenses_displaysProfit() throws SystemException {
        OrderList orders = new OrderList();
        var order = orders.create("Alice", List.of(new OrderItem(new Product("Apple", 10.00), 2)));
        order.recordPayment(java.math.BigDecimal.valueOf(20).setScale(2));
        ExpenseList expenses = new ExpenseList();
        expenses.add(new Expense("Rent", 5.00));

        String output = executeReport(orders, expenses);

        assertTrue(output.contains("Overall Profit: $15.00"));
    }

    @Test
    public void execute_salesEqualExpenses_displaysProfit() throws SystemException {
        OrderList orders = new OrderList();
        var order = orders.create("Alice", List.of(new OrderItem(new Product("Apple", 10.00), 1)));
        order.recordPayment(java.math.BigDecimal.valueOf(10).setScale(2));
        ExpenseList expenses = new ExpenseList();
        expenses.add(new Expense("Rent", 10.00));

        String output = executeReport(orders, expenses);

        assertTrue(output.contains("Overall Profit: $0.00"));
    }

    @Test
    public void execute_partialPayment_reportsOnlyPaidAmountAsSales() throws SystemException {
        OrderList orders = new OrderList();
        var order = orders.create("Bob", List.of(new OrderItem(new Product("Apple", 5.00), 999)));
        order.recordPayment(java.math.BigDecimal.valueOf(4000).setScale(2));
        ExpenseList expenses = new ExpenseList();
        expenses.add(new Expense("Rent", java.math.BigDecimal.ZERO.setScale(2)));

        String output = executeReport(orders, expenses);

        assertTrue(output.contains("Total Orders (including unpaid): $4995.00"));
        assertTrue(output.contains("Total Sales: $4000.00"));
        assertTrue(output.contains("Overall Profit: $4000.00"));
    }

    private String executeReport(OrderList orders, ExpenseList expenses) throws SystemException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = new Ui(new ByteArrayInputStream(new byte[0]),
                new PrintStream(output, true, StandardCharsets.UTF_8));
        new FinancesCommand().execute(new ProductList(), orders, expenses, ui);
        return output.toString(StandardCharsets.UTF_8);
    }
}
