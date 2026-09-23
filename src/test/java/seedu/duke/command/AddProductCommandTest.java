package seedu.duke.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.duke.Product;
import seedu.duke.ProductList;
import seedu.duke.SystemException;
import seedu.duke.Ui;

class AddProductCommandTest {
    private ProductList products;
    private Ui ui;

    @BeforeEach
    public void setUp() {
        products = new ProductList();
        ui = new Ui(new ByteArrayInputStream(new byte[0]),
                new PrintStream(new ByteArrayOutputStream(), true, StandardCharsets.UTF_8));
    }

    @Test
    public void execute_newProduct_addsProductAtZeroStock() throws SystemException {
        new AddProductCommand("Oat Milk", 3.50).execute(products, ui);

        Product added = products.findByName("Oat Milk");
        assertNotNull(added);
        assertEquals(3.50, added.getPrice(), 1e-9);
        assertEquals(0, added.getStock());
    }

    @Test
    public void execute_duplicateProduct_throwsSystemException() throws SystemException {
        new AddProductCommand("Oat Milk", 3.50).execute(products, ui);

        AddProductCommand duplicate = new AddProductCommand("Oat Milk", 4.00);
        assertThrows(SystemException.class, () -> duplicate.execute(products, ui));
    }

    @Test
    public void isExit_addCommand_returnsFalse() {
        assertEquals(false, new AddProductCommand("Oat Milk", 3.50).isExit());
    }
}
