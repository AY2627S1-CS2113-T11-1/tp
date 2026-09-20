package seedu.duke;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ProductTest {
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
