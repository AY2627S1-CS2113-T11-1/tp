package seedu.duke;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
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

        assertEquals(indentedLines("Added product Oat Milk", "Price: $3.50", "Stock: 0"), outputLines());
    }

    @Test
    public void showProducts_threeProducts_printsAlignedColumns() {
        Product oatMilk = new Product("Oat Milk", 3.50);
        oatMilk.setStock(24);
        Product paperBag = new Product("Paper Bag", 0.10);
        paperBag.setStock(200);
        Product coffeeBeans = new Product("Coffee Beans", 12.00);

        ui.showProducts(List.of(oatMilk, paperBag, coffeeBeans));

        assertEquals(indentedLines(
                "Products (3):",
                "   Oat Milk          $3.50   stock 24",
                "   Paper Bag         $0.10   stock 200",
                "   Coffee Beans      $12.00  stock 0"), outputLines());
    }

    @Test
    public void showProducts_emptyCatalogue_printsZeroCountAndHint() {
        ui.showProducts(List.of());

        assertEquals("    Products (0):", outputLines().get(0));
        assertEquals(2, outputLines().size());
    }

    @Test
    public void readCommand_noInput_returnsNull() {
        assertEquals(null, ui.readCommand());
    }

    @Test
    public void showOrderCreated_twoItems_printsAlignedTable() {
        ui.showOrderCreated(sampleOrder());

        assertEquals(indentedLines(
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
        assertEquals("    | Organic Coffee Beans |  10 |     $12.00 |  $120.00 |", lines.get(4));
        assertEquals(lines.get(1).length(), lines.get(4).length());
    }

    @Test
    public void showOrderCancelled_twoItems_printsRestoredTableAndAmount() {
        ui.showOrderCancelled(sampleOrder());

        assertEquals(indentedLines(
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

        assertEquals(indentedLines(
                "Orders (2):",
                "+-----+-----------+-------+---------+-----------+",
                "| No. | Customer  | Units |   Total | Status    |",
                "+-----+-----------+-------+---------+-----------+",
                "|   1 | Jonas Low |     4 |   $6.38 | Cancelled |",
                "|   2 | Mei       |    10 | $120.00 | Active    |",
                "+-----+-----------+-------+---------+-----------+"), outputLines());
    }

    @Test
    public void showSales_cancelledOrderIsExcluded_printsConfirmedSalesAndTotal() throws SystemException {
        OrderList orders = new OrderList();
        Order cancelledSale = orders.create("Jonas Low", sampleOrder().getItems());
        cancelledSale.cancel();
        orders.create("Mei", List.of(
                new OrderItem(new Product("Organic Coffee Beans", 12.00), 10)));
        orders.create("Mei", List.of(
                new OrderItem(new Product("Organic Coffee Beans", 12.00), 1)));

        ui.showSales(orders.getConfirmedSales());

        assertEquals(indentedLines(
                "Sales (2):",
                "+-------+----------+-------+----------+",
                "|   No. | Customer | Units | Subtotal |",
                "+-------+----------+-------+----------+",
                "|     1 | Mei      |    10 |  $120.00 |",
                "|     2 | Mei      |     1 |   $12.00 |",
                "+-------+----------+-------+----------+",
                "| Total |          |       |  $132.00 |",
                "+-------+----------+-------+----------+",
                "Total sales: $132.00"), outputLines());
        assertEquals(2, orders.getConfirmedSales().get(0).getId());
        assertEquals(3, orders.getConfirmedSales().get(1).getId());
    }

    @Test
    public void showOrders_noOrders_printsZeroCountAndHint() {
        ui.showOrders(List.of());

        assertEquals("    Orders (0):", outputLines().get(0));
        assertEquals(2, outputLines().size());
    }

    @Test
    public void showStocks_twoStocks_printsAlignedTable() {
        Stock apple = new Stock("Apple", 1.50, 10);
        Stock carrot = new Stock("Carrot", 1.88, 5);

        ui.showStocks(List.of(apple, carrot));

        assertEquals(indentedLines(
                "+---------+-----+",
                "| Product | Qty |",
                "+---------+-----+",
                "| Apple   |  10 |",
                "| Carrot  |   5 |",
                "+---------+-----+"), outputLines());
    }

    @Test
    public void showStockCreated_wholeDollar_printsFormattedPrice() {
        ui.showStockCreated(new Stock("Apple", 2.0, 10));

        assertEquals(indentedLines(
                "Creates stock for a new product Apple with quantity 10 and cost $2 each."), outputLines());
    }

    @Test
    public void showStockUpdated_existingStock_printsNewQuantity() {
        ui.showStockUpdated(new Stock("Apple", 2.0, 15));

        assertEquals(indentedLines("Updated stock for Apple. New quantity: 15"), outputLines());
    }

    @Test
    public void showStockRemoved_defaultBehavior_printsOneUnit() {
        ui.showStockRemoved("Apple", 1, true);

        assertEquals(indentedLines("Removes 1 unit (default behaviour) of Apple"), outputLines());
    }

    @Test
    public void showStockRemoved_customQuantity_printsQuantity() {
        ui.showStockRemoved("Apple", 5, false);

        assertEquals(indentedLines("Removes 5 units of Apple"), outputLines());
    }

    @Test
    public void showOrderUpdated_editedOrder_printsCustomerUnitsTotalAndStatus() throws SystemException {
        Product carrot = new Product("Carrot", 1.88);
        Order order = new Order(2, "Jonas Low", List.of(new OrderItem(carrot, 4)));
        order.update("Mei Tan", List.of(new OrderItem(carrot, 5)));

        ui.showOrderUpdated(order);

        assertEquals(indentedLines("Order 2 updated.", "Customer: Mei Tan", "Units: 5", "Total: $9.40",
                "Status: Active"), outputLines());
    }

    @Test
    public void showError_multiLineMessage_indentsEveryLine() {
        ui.showError("Unable to edit order: insufficient stock.\nNo changes were made to the order or stock.");

        assertEquals(indentedLines("Sorry! Unable to edit order: insufficient stock.",
                "No changes were made to the order or stock."), outputLines());
    }

    @Test
    public void showDiscountUpdated_tenPercent_printsSubtotalDiscountAndTotal() throws SystemException {
        Order order = new Order(2, "Mei Tan",
                List.of(new OrderItem(new Product("Carrot", 1.88), 5)));
        order.setDiscountPercent(new BigDecimal("10"));

        ui.showDiscountUpdated(order);

        assertEquals(indentedLines("Discount updated for order 2.", "Subtotal: $9.40", "Discount: 10% ($0.94)",
                "Total: $8.46"), outputLines());
    }

    @Test
    public void showDiscountUpdated_zeroPercent_printsRemovedMessage() throws SystemException {
        Order order = new Order(2, "Mei Tan",
                List.of(new OrderItem(new Product("Carrot", 1.88), 5)));
        order.setDiscountPercent(new BigDecimal("10"));
        order.setDiscountPercent(BigDecimal.ZERO);

        ui.showDiscountUpdated(order);

        assertEquals(indentedLines("Discount removed from order 2.", "Subtotal: $9.40", "Discount: 0% ($0.00)",
                "Total: $9.40"), outputLines());
    }

    @Test
    public void showReceipts_discountedOrder_showsSubtotalRowThenDiscountAndTotal() throws SystemException {
        Order order = new Order(2, "Mei Tan",
                List.of(new OrderItem(new Product("Carrot", 1.88), 5)));
        order.setDiscountPercent(new BigDecimal("12.50"));

        ui.showReceipts("Mei Tan", List.of(order));

        List<String> lines = outputLines();
        assertEquals("    | Subtotal |   5 |            |    $9.40 |", lines.get(lines.size() - 4));
        assertEquals("    Discount: 12.5% ($1.18)", lines.get(lines.size() - 2));
        assertEquals("    Total: $8.22", lines.get(lines.size() - 1));
    }

    @Test
    public void showLowStock_emptyStockList_printsNoStockMessage() {
        ui.showLowStock(List.of(), 5, true);

        assertEquals(indentedLines("There are currently no stocks in the list."), outputLines());
    }

    @Test
    public void showLowStock_noLowStockItems_printsAllSufficientMessage() {
        ui.showLowStock(List.of(), 5, false);

        assertEquals(indentedLines("All products have sufficient stock (more than 5)."), outputLines());
    }

    @Test
    public void showLowStock_itemsFound_printsLowStockHeaderAndTable() {
        ui.showLowStock(List.of(new Stock("Apple", 2.0, 3)), 5, false);

        List<String> lines = outputLines();
        assertEquals("    Low stock items (quantity <= 5):", lines.get(0));
        assertEquals("    | Product | Qty |", lines.get(2));
        assertEquals("    | Apple   |   3 |", lines.get(4));
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
     * Adds the UI's four-space margin to expected lines while preserving table and column spacing.
     */
    private static List<String> indentedLines(String... lines) {
        return java.util.Arrays.stream(lines).map(line -> "    " + line).toList();
    }

    /**
     * Returns everything printed so far, split into lines, so tests do not depend on
     * whether the platform ends lines with \n or \r\n.
     */
    private List<String> outputLines() {
        return captured.toString(StandardCharsets.UTF_8).lines().toList();
    }
}
