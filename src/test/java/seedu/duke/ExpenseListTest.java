package seedu.duke;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ExpenseListTest {

    @Test
    public void newList_hasSizeZero() {
        ExpenseList expenses = new ExpenseList();

        assertEquals(0, expenses.size());
    }

    @Test
    public void add_expense_increasesSize() {
        ExpenseList expenses = new ExpenseList();

        expenses.add(new Expense("Bread", 100.50));

        assertEquals(1, expenses.size());
    }

    @Test
    public void getExpenses_returnsExpensesInInsertionOrder() {
        ExpenseList expenses = new ExpenseList();
        Expense bread = new Expense("Bread", 100.50);
        Expense lettuce = new Expense("Lettuce", 42.30);

        expenses.add(bread);
        expenses.add(lettuce);

        assertEquals(bread, expenses.getExpenses().get(0));
        assertEquals(lettuce, expenses.getExpenses().get(1));
    }
}
