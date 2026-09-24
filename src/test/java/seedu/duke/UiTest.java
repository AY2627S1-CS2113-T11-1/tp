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

    @Test
    public void showOrderCreated_twoItems_printsAlignedTable() {
        ui.showOrderCreated(sampleOrder());

        assertEquals(List.of(
                "Order 1 created for Jonas Low.",
                "+---------+-----+------------+----------+",
                "| Product | Qty | Unit price | Subtotal |",
                "+---------+-----+------------+----------+",
                "| Apple   |   3 |      $1.50 |    $4.50 |",
                "| Carrot  |   1 |      $1.88 |    $1.88 |",
                "+---------+-----+------------+----------+",
                "| Total   |   4 |            |    $6.38 |",
                "+---------+-----+------------+----------+"), outputLines());
    }

    @Test
    public void showOrderCreated_longProductName_widensFirstColumn() {
        ui.showOrderCreated(new Order(2, "Mei", List.of(
                new OrderItem(new Product("Organic Coffee Beans", 12.00), 10))));

        List<String> lines = outputLines();
        assertEquals("| Organic Coffee Beans |  10 |     $12.00 |  $120.00 |", lines.get(4));
        assertEquals(lines.get(1).length(), lines.get(4).length());
    }

    @Test
    public void showOrderCancelled_twoItems_printsRestoredTableAndAmount() {
        ui.showOrderCancelled(sampleOrder());

        assertEquals(List.of(
                "Order 1 for Jonas Low cancelled.",
                "Stock restored:",
                "+---------+----------+------------+----------+",
                "| Product | Restored | Unit price | Subtotal |",
                "+---------+----------+------------+----------+",
                "| Apple   |        3 |      $1.50 |    $4.50 |",
                "| Carrot  |        1 |      $1.88 |    $1.88 |",
                "+---------+----------+------------+----------+",
                "| Total   |        4 |            |    $6.38 |",
                "+---------+----------+------------+----------+",
                "Amount excluded from recorded sales: $6.38"), outputLines());
    }

    @Test
    public void showOrders_activeAndCancelled_printsAlignedTableWithStatus() throws SystemException {
        Order cancelled = sampleOrder();
        cancelled.cancel();
        Order active = new Order(2, "Mei", List.of(new OrderItem(new Product("Organic Coffee Beans", 12.00), 10)));

        ui.showOrders(List.of(cancelled, active));

        assertEquals(List.of(
                "Orders (2):",
                "+-----+-----------+-------+---------+-----------+",
                "| No. | Customer  | Units |   Total | Status    |",
                "+-----+-----------+-------+---------+-----------+",
                "|   1 | Jonas Low |     4 |   $6.38 | Cancelled |",
                "|   2 | Mei       |    10 | $120.00 | Active    |",
                "+-----+-----------+-------+---------+-----------+"), outputLines());
    }

    @Test
    public void showOrders_noOrders_printsZeroCountAndHint() {
        ui.showOrders(List.of());

        assertEquals("Orders (0):", outputLines().get(0));
        assertEquals(2, outputLines().size());
    }

    /**
     * Returns order 1 for Jonas Low: 3 apples at $1.50 and 1 carrot at $1.88, the UG example.
     */
    private static Order sampleOrder() {
        return new Order(1, "Jonas Low", List.of(
                new OrderItem(new Product("Apple", 1.50), 3),
                new OrderItem(new Product("Carrot", 1.88), 1)));
    }

    /**
     * Returns everything printed so far, split into lines, so tests do not depend on
     * whether the platform ends lines with \n or \r\n.
     */
    private List<String> outputLines() {
        return captured.toString(StandardCharsets.UTF_8).lines().toList();
    }
}
