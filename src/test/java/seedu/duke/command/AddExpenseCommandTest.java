package seedu.duke.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.duke.ExpenseList;
import seedu.duke.OrderList;
import seedu.duke.ProductList;
import seedu.duke.SystemException;
import seedu.duke.Ui;

class AddExpenseCommandTest {
    @Test
    public void execute_validExpense_addsExpense() throws SystemException {
        ExpenseList expenses = new ExpenseList();
        AddExpenseCommand command = new AddExpenseCommand("Bread", 100.50);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = new Ui(new ByteArrayInputStream(new byte[0]),
                new PrintStream(output, true, StandardCharsets.UTF_8));

        command.execute(new ProductList(), new OrderList(), expenses, ui);

        assertEquals(1, expenses.size());
        assertEquals("Bread", expenses.getExpenses().get(0).getProductName());
        assertEquals(100.50, expenses.getExpenses().get(0).getAmount());
        assertEquals(List.of("    Expense added:", "    Bread: $100.50"),
                output.toString(StandardCharsets.UTF_8).lines().toList());
    }
}
