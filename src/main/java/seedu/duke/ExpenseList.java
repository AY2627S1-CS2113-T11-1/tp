package seedu.duke;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores operating expenses in the order they were added.
 */
public class ExpenseList {
    private final ArrayList<Expense> expenses = new ArrayList<>();

    /**
     * Adds an expense to the end of the list.
     *
     * @param expense the expense to add
     */
    public void add(Expense expense) {
        expenses.add(expense);
    }

    /**
     * Returns all expenses in insertion order.
     *
     * @return a copy of the expenses
     */
    public List<Expense> getExpenses() {
        return new ArrayList<>(expenses);
    }

    /**
     * Returns the number of recorded expenses.
     *
     * @return the number of expenses
     */
    public int size() {
        return expenses.size();
    }
}
