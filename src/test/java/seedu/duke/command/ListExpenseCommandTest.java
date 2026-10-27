package seedu.duke.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.duke.Expense;
import seedu.duke.ExpenseList;
import seedu.duke.OrderList;
import seedu.duke.ProductList;
import seedu.duke.SystemException;
import seedu.duke.Ui;

class ListExpenseCommandTest {
    @Test
    public void execute_expensesExist_displaysExpensesInInsertionOrder() throws SystemException {
        ExpenseList expenses = new ExpenseList();
        expenses.add(new Expense("Bread", 100.50));
        expenses.add(new Expense("Lettuce", 42.30));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = new Ui(new ByteArrayInputStream(new byte[0]),
                new PrintStream(output, true, StandardCharsets.UTF_8));

        new ListExpenseCommand().execute(new ProductList(), new OrderList(), expenses, ui);

        assertEquals(List.of("    Expenses:", "    1. Bread: $100.50", "    2. Lettuce: $42.30"),
                output.toString(StandardCharsets.UTF_8).lines().toList());
    }

    @Test
    public void execute_noExpenses_displaysNoExpensesMessage() throws SystemException {
        ExpenseList expenses = new ExpenseList();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = new Ui(new ByteArrayInputStream(new byte[0]),
                new PrintStream(output, true, StandardCharsets.UTF_8));

        new ListExpenseCommand().execute(new ProductList(), new OrderList(), expenses, ui);

        assertEquals(List.of("    Expenses:", "    No expenses have been added"),
                output.toString(StandardCharsets.UTF_8).lines().toList());
    }
}
