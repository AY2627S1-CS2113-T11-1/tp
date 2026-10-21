package seedu.duke;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link Product}.
 *
 * Gradle runs tests with assertions enabled, so the AssertionError tests below confirm that the
 * assert statements in Product catch callers that skip validation.
 */
class ProductTest {
    @Test
    public void constructor_blankName_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new Product("   ", 3.50));
    }

    @Test
    public void constructor_negativePrice_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new Product("Oat Milk", -1));
    }

    @Test
    public void setStock_positiveValue_updatesStock() {
        Product product = new Product("Oat Milk", 3.50);
        product.setStock(24);
        assertEquals(24, product.getStock());
    }

    @Test
    public void setStock_negativeValue_throwsAssertionError() {
        Product product = new Product("Oat Milk", 3.50);
        assertThrows(AssertionError.class, () -> product.setStock(-1));
    }

    @Test
    public void constructor_newProduct_startsAtZeroStock() {
        Product product = new Product("Oat Milk", 3.50);
        assertEquals(0, product.getStock());
    }

    @Test
    public void getPriceString_priceWithOneDecimal_padsToTwoDecimals() {
        Product product = new Product("Oat Milk", 3.5);
        assertEquals("$3.50", product.getPriceString());
    }

    @Test
    public void getPriceString_wholeDollarPrice_showsTwoDecimals() {
        Product product = new Product("Coffee Beans", 12);
        assertEquals("$12.00", product.getPriceString());
    }
}
