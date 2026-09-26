package seedu.duke.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import seedu.duke.ExpenseList;
import seedu.duke.OrderList;
import seedu.duke.ProductList;
import seedu.duke.Ui;

class AddExpenseCommandTest {
    @Test
    public void execute_validExpense_addsExpense() {
        ExpenseList expenses = new ExpenseList();
        AddExpenseCommand command = new AddExpenseCommand("Bread", 100.50);

        command.execute(new ProductList(), new OrderList(), expenses, new Ui());

        assertEquals(1, expenses.size());
        assertEquals("Bread", expenses.getExpenses().get(0).getProductName());
        assertEquals(100.50, expenses.getExpenses().get(0).getAmount());
    }
}
