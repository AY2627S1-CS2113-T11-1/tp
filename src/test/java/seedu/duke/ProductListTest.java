package seedu.duke;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

class ProductListTest {
    @Test
    public void add_newProduct_increasesSize() throws SystemException {
        ProductList products = new ProductList();
        products.add(new Product("Oat Milk", 3.50));
        assertEquals(1, products.size());
    }

    @Test
    public void add_duplicateNameIgnoringCase_throwsSystemException() throws SystemException {
        ProductList products = new ProductList();
        products.add(new Product("Oat Milk", 3.50));

        assertThrows(SystemException.class, () -> products.add(new Product("oat milk", 4.00)));
        assertEquals(1, products.size());
    }

    @Test
    public void findByName_differentCase_returnsProduct() throws SystemException {
        ProductList products = new ProductList();
        products.add(new Product("Oat Milk", 3.50));
        assertNotNull(products.findByName("OAT MILK"));
    }

    @Test
    public void findByName_absentProduct_returnsNull() {
        assertNull(new ProductList().findByName("Oat Milk"));
    }

    @Test
    public void getSortedByName_productsAddedOutOfOrder_returnsAlphabeticalOrder() throws SystemException {
        ProductList products = new ProductList();
        products.add(new Product("Oat Milk", 3.50));
        products.add(new Product("paper Bag", 0.10));
        products.add(new Product("Coffee Beans", 12.00));

        List<Product> sorted = products.getSortedByName();

        assertEquals("Coffee Beans", sorted.get(0).getName());
        assertEquals("Oat Milk", sorted.get(1).getName());
        assertEquals("paper Bag", sorted.get(2).getName());
    }

    @Test
    public void getSortedByName_sortedCopy_leavesStoredOrderUnchanged() throws SystemException {
        ProductList products = new ProductList();
        products.add(new Product("Oat Milk", 3.50));
        products.add(new Product("Coffee Beans", 12.00));

        products.getSortedByName();

        assertEquals("Oat Milk", products.getSortedByName().get(1).getName());
        assertEquals(2, products.size());
    }

    @Test
    public void getSortedByName_emptyCatalogue_returnsEmptyList() {
        assertEquals(0, new ProductList().getSortedByName().size());
    }
}
