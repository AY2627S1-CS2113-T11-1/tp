package seedu.duke;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Checks that names accepted for orders remain usable for payment, details and receipts.
 */
class CustomerNameValidationTest {
    @Test
    public void parse_extraCustomerFields_rejectedByEveryCommand() {
        Parser parser = new Parser();
        for (String name : List.of("Bob x/1", "Bob X/1", "Bob s/1", "Bob c/Alice")) {
            for (String command : List.of("order add c/" + name + " p/Apple q/1",
                    "order edit 1 c/" + name, "order details c/" + name,
                    "order add pay c/" + name + " a/1", "generate receipts c/" + name)) {
                assertThrows(SystemException.class, () -> parser.parse(command), command);
            }
        }
    }

    @Test
    public void execute_supportedCustomerNames_workAcrossOrderLifecycle() throws SystemException {
        for (String name : List.of("Alice Smith", "Raj s/o Kumar", "Raj S/O Kumar")) {
            ProductList products = new ProductList();
            Product apple = new Product("Apple", 1);
            apple.setStock(2);
            products.add(apple);
            OrderList orders = new OrderList();
            ExpenseList expenses = new ExpenseList();
            StockList stocks = new StockList();
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            Ui ui = new Ui(new ByteArrayInputStream(new byte[0]),
                    new PrintStream(output, true, StandardCharsets.UTF_8));
            Parser parser = new Parser();

            parser.parse("order add c/" + name + " p/Apple q/1")
                    .execute(products, orders, expenses, stocks, ui);
            parser.parse("order edit 1 c/" + name + " Jr")
                    .execute(products, orders, expenses, stocks, ui);
            String editedName = name + " Jr";
            parser.parse("order add pay c/" + editedName + " a/1")
                    .execute(products, orders, expenses, stocks, ui);
            parser.parse("order details c/" + editedName).execute(products, orders, expenses, stocks, ui);
            parser.parse("generate receipts c/" + editedName).execute(products, orders, expenses, stocks, ui);

            assertEquals(editedName, orders.findById(1).getCustomer());
            assertTrue(orders.findById(1).isPaid());
            assertEquals(new BigDecimal("1.00"), orders.findById(1).getPaidAmount());
            assertEquals(1, apple.getStock());
            String text = output.toString(StandardCharsets.UTF_8);
            assertTrue(text.contains("Here are " + editedName + "'s order details:"));
            assertTrue(text.contains("Receipt for order 1 - " + editedName));
        }
    }
}
