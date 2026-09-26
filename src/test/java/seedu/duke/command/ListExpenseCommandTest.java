package seedu.duke.command;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import seedu.duke.Expense;
import seedu.duke.ExpenseList;
import seedu.duke.OrderList;
import seedu.duke.ProductList;
import seedu.duke.Ui;

class ListExpenseCommandTest {
    @Test
    public void execute_expensesExist_displaysExpensesInInsertionOrder() {
        ExpenseList expenses = new ExpenseList();
        expenses.add(new Expense("Bread", 100.50));
        expenses.add(new Expense("Lettuce", 42.30));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = new Ui(new ByteArrayInputStream(new byte[0]),
                new PrintStream(output, true, StandardCharsets.UTF_8));

        new ListExpenseCommand().execute(new ProductList(), new OrderList(), expenses, ui);

        String result = output.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("1. Bread: $100.50"));
        assertTrue(result.contains("2. Lettuce: $42.30"));
    }
}
