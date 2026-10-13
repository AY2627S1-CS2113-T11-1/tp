package seedu.duke;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ExpenseTest {

    @Test
    public void constructor_validValues_storesValues() {
        Expense expense = new Expense("Bread", 100.50);

        assertEquals("Bread", expense.getProductName());
        assertEquals(100.50, expense.getAmount());
    }

    @Test
    public void constructor_productNameWithWhitespace_trimsName() {
        Expense expense = new Expense("  Bread  ", 100.50);

        assertEquals("Bread", expense.getProductName());
    }

    @Test
    public void constructor_emptyProductName_throwsException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Expense("", 100.50));
    }

    @Test
    public void constructor_negativeAmount_throwsException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Expense("Bread", -1.00));
    }

    @Test
    public void constructor_invalidAmount_throwsException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Expense("Bread", Double.NaN));
    }
}
