package seedu.duke.command;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

        command.execute(new ProductList(), new OrderList(), expenses, new Ui());

        assertEquals(1, expenses.size());
        assertEquals("Bread", expenses.getExpenses().get(0).getProductName());
        assertEquals(BigDecimal.valueOf(100.50).setScale(2), expenses.getExpenses().get(0).getAmount());
    }
}
