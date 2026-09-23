package seedu.duke;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UiTest {
    private ByteArrayOutputStream captured;
    private Ui ui;

    /**
     * Wires the Ui to an in-memory output stream so each test can read back what was printed.
     */
    @BeforeEach
    public void setUp() {
        captured = new ByteArrayOutputStream();
        ui = new Ui(new ByteArrayInputStream(new byte[0]),
                new PrintStream(captured, true, StandardCharsets.UTF_8));
    }

    @Test
    public void showProductAdded_newProduct_printsNamePriceAndZeroStock() {
        ui.showProductAdded(new Product("Oat Milk", 3.50));

        assertEquals(List.of("Added product Oat Milk", "Price: $3.50", "Stock: 0"), outputLines());
    }

    @Test
    public void showProducts_threeProducts_printsAlignedColumns() {
        Product oatMilk = new Product("Oat Milk", 3.50);
        oatMilk.setStock(24);
        Product paperBag = new Product("Paper Bag", 0.10);
        paperBag.setStock(200);
        Product coffeeBeans = new Product("Coffee Beans", 12.00);

        ui.showProducts(List.of(oatMilk, paperBag, coffeeBeans));

        assertEquals(List.of(
                "Products (3):",
                "   Oat Milk          $3.50   stock 24",
                "   Paper Bag         $0.10   stock 200",
                "   Coffee Beans      $12.00  stock 0"), outputLines());
    }

    @Test
    public void showProducts_emptyCatalogue_printsZeroCountAndHint() {
        ui.showProducts(List.of());

        assertEquals("Products (0):", outputLines().get(0));
        assertEquals(2, outputLines().size());
    }

    @Test
    public void readCommand_noInput_returnsNull() {
        assertEquals(null, ui.readCommand());
    }

    /**
     * Returns everything printed so far, split into lines, so tests do not depend on
     * whether the platform ends lines with \n or \r\n.
     */
    private List<String> outputLines() {
        return captured.toString(StandardCharsets.UTF_8).lines().toList();
    }
}
