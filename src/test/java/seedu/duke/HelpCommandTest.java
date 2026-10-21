package seedu.duke;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.duke.command.Command;

/**
 * Checks help topics, syntax, formatting, and read-only execution.
 */
class HelpCommandTest {
    private ByteArrayOutputStream output;
    private Ui ui;

    /**
     * Captures help output for each test.
     */
    @BeforeEach
    public void setUp() {
        output = new ByteArrayOutputStream();
        ui = new Ui(new ByteArrayInputStream(new byte[0]),
                new PrintStream(output, true, StandardCharsets.UTF_8));
    }

    @Test
    public void execute_noArea_listsAreasAndUsage() throws SystemException {
        String text = runHelp("help");
        assertTrue(text.contains("Format: help {AREA}"));
        assertTrue(text.contains("product, stock, order, sales, expense, generate, general"));
        assertTrue(text.contains("Example: help stock"));
    }

    @Test
    public void execute_supportedAreas_listsEveryCurrentCommand() throws SystemException {
        String[][] topics = {
            {"product", "product add p/PRODUCT a/AMOUNT", "product list"},
            {"stock", "stock add p/PRODUCT q/QUANTITY", "stock remove p/PRODUCT [q/QUANTITY]", "stock list",
                "stock low [q/THRESHOLD]"},
            {"order", "order add c/CUSTOMER p/PRODUCT q/QUANTITY [p/PRODUCT q/QUANTITY]...",
                "order cancel ORDER_NUMBER", "order list"},
            {"sales", "sales list"},
            {"expense", "expense add p/PRODUCT a/AMOUNT", "expense list"},
            {"generate", "generate receipts c/CUSTOMER"},
            {"general", "help [AREA]", "bye"}
        };
        for (String[] topic : topics) {
            output.reset();
            String text = runHelp("help " + topic[0]);
            for (int i = 1; i < topic.length; i++) {
                assertTrue(text.contains("       " + topic[i] + System.lineSeparator()), topic[i]);
            }
            assertEquals(1 + 2 * (topic.length - 1), text.lines().count());
            assertTrue(text.lines().allMatch(line -> line.startsWith("    ")));
        }
    }

    @Test
    public void execute_stock_printsNumberedDescriptionsAndOptionalQuantity() throws SystemException {
        assertEquals(List.of(
                "    Stock commands:",
                "    1. Adds stock for an existing catalogue product using its selling price.",
                "       stock add p/PRODUCT q/QUANTITY",
                "    2. Removes stock, defaulting to one unit when quantity is omitted."
                        + " Stock cannot fall below zero.",
                "       stock remove p/PRODUCT [q/QUANTITY]",
                "    3. Lists products currently in the stock list and their quantities.",
                "       stock list",
                "    4. Lists products with stock at or below a threshold (default 5).",
                "       stock low [q/THRESHOLD]"), runHelp("help stock").lines().toList());
    }

    @Test
    public void execute_mixedCaseAndWhitespace_matchesLowercaseOutput() throws SystemException {
        String expected = runHelp("help stock");
        output.reset();
        assertEquals(expected, runHelp("  HeLp   StOcK  "));
    }

    @Test
    public void execute_unknownArea_reportsAreasAndFormat() {
        SystemException error = assertThrows(SystemException.class, () -> runHelp("help unknown"));
        assertTrue(error.getMessage().contains("Unknown help area"));
        assertTrue(error.getMessage().contains("Available areas:"));
        assertTrue(error.getMessage().contains("Format: help {AREA}"));
        assertEquals("", output.toString(StandardCharsets.UTF_8));
    }

    @Test
    public void parse_multipleAreas_rejectsExtraArguments() {
        SystemException error = assertThrows(SystemException.class, () -> new Parser().parse("help stock order"));
        assertTrue(error.getMessage().contains("Choose one help area"));
        assertTrue(error.getMessage().contains("Available areas:"));
    }

    @Test
    public void execute_help_doesNotChangeDataOrExit() throws SystemException {
        ProductList products = new ProductList();
        Product product = new Product("Apple", 1);
        product.setStock(5);
        products.add(product);
        OrderList orders = new OrderList();
        Order order = orders.create("You Jie", List.of(new OrderItem(product, 1)));
        ExpenseList expenses = new ExpenseList();
        StockList stocks = new StockList();
        Command command = new Parser().parse("help order");
        command.execute(products, orders, expenses, stocks, ui);
        assertFalse(command.isExit());
        assertEquals(5, product.getStock());
        assertEquals(1, orders.size());
        assertFalse(order.isCancelled());
        assertEquals(0, expenses.size());
        assertEquals(0, stocks.getStocks().size());
    }

    @Test
    public void execute_help_doesNotAdvertiseOtherFeatureBranches() throws SystemException {
        String text = runHelp("help order");

        assertFalse(text.contains("order add pay"));

        assertThrows(SystemException.class, () -> new Parser().parse("order add pay c/You Jie a/20"));
    }

    @Test
    public void execute_areaCase_isIndependentOfLocale() throws SystemException {
        Locale previous = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            assertTrue(runHelp("help GENERAL").contains("General commands:"));
        } finally {
            Locale.setDefault(previous);
        }
    }

    @Test
    public void parse_examplesFromHelp_acceptsImplementedSyntax() throws SystemException {
        for (String example : List.of("product add p/Apple a/1", "product list", "stock add p/Apple q/1",
                "stock remove p/Apple", "stock remove p/Apple q/2", "stock list", "order add c/You Jie p/Apple q/1",
                "order cancel 1", "order list", "sales list", "expense add p/Rent a/10", "expense list",
                "generate receipts c/You Jie", "help", "help stock", "bye")) {
            new Parser().parse(example);
        }
    }

    /**
     * Runs help through the normal parser and command interface.
     */
    private String runHelp(String input) throws SystemException {
        new Parser().parse(input).execute(new ProductList(), new OrderList(), new ExpenseList(), new StockList(), ui);
        return output.toString(StandardCharsets.UTF_8);
    }
}
