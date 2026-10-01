package seedu.duke;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.duke.command.AddOrderCommand;
import seedu.duke.command.AddExpenseCommand;
import seedu.duke.command.AddProductCommand;
import seedu.duke.command.CancelOrderCommand;
import seedu.duke.command.Command;
import seedu.duke.command.ListOrderCommand;
import seedu.duke.command.ListExpenseCommand;
import seedu.duke.command.ListProductCommand;

class ParserTest {
    private static final double DELTA = 1e-9;

    private final Parser parser = new Parser();

    @Test
    public void parse_validAddCommand_returnsAddCommandWithNameAndPrice() throws SystemException {
        Command command = parser.parse("product add p/Oat Milk a/3.50");

        AddProductCommand addCommand = assertInstanceOf(AddProductCommand.class, command);
        assertEquals("Oat Milk", addCommand.getName());
        assertEquals(3.50, addCommand.getPrice(), DELTA);
    }

    @Test
    public void parse_prefixesInReverseOrder_returnsAddCommandWithNameAndPrice() throws SystemException {
        Command command = parser.parse("product add a/3.50 p/Oat Milk");

        AddProductCommand addCommand = assertInstanceOf(AddProductCommand.class, command);
        assertEquals("Oat Milk", addCommand.getName());
        assertEquals(3.50, addCommand.getPrice(), DELTA);
    }

    @Test
    public void parse_extraSpacingAroundValues_trimsNameAndPrice() throws SystemException {
        Command command = parser.parse("   product   add   p/  Oat Milk   a/ 3.50 ");

        AddProductCommand addCommand = assertInstanceOf(AddProductCommand.class, command);
        assertEquals("Oat Milk", addCommand.getName());
        assertEquals(3.50, addCommand.getPrice(), DELTA);
    }

    @Test
    public void parse_priceWithoutDecimals_returnsAddCommandWithWholeDollarPrice() throws SystemException {
        Command command = parser.parse("product add p/Paper Bag a/12");

        AddProductCommand addCommand = assertInstanceOf(AddProductCommand.class, command);
        assertEquals(12.0, addCommand.getPrice(), DELTA);
    }

    @Test
    public void parse_missingNamePrefix_throwsSystemException() {
        SystemException exception = assertThrows(SystemException.class, () -> parser.parse("product add a/3.50"));
        assertTrue(exception.getMessage().contains("p/"));
    }

    @Test
    public void parse_missingPricePrefix_throwsSystemException() {
        SystemException exception = assertThrows(SystemException.class, () -> parser.parse("product add p/Oat Milk"));
        assertTrue(exception.getMessage().contains("a/"));
    }

    @Test
    public void parse_emptyName_throwsSystemException() {
        assertThrows(SystemException.class, () -> parser.parse("product add p/ a/3.50"));
    }

    @Test
    public void parse_nonNumericPrice_throwsSystemException() {
        assertThrows(SystemException.class, () -> parser.parse("product add p/Oat Milk a/free"));
    }

    @Test
    public void parse_negativePrice_throwsSystemException() {
        assertThrows(SystemException.class, () -> parser.parse("product add p/Oat Milk a/-3.50"));
    }

    @Test
    public void parse_priceWithThreeDecimals_throwsSystemException() {
        assertThrows(SystemException.class, () -> parser.parse("product add p/Oat Milk a/3.505"));
    }

    @Test
    public void parse_validExpenseAdd_returnsAddExpenseCommand() throws SystemException {
        AddExpenseCommand command = assertInstanceOf(AddExpenseCommand.class,
                parser.parse("expense add p/Bread a/100.50"));

        assertEquals("Bread", command.getProductName());
        assertEquals(100.50, command.getAmount(), DELTA);
    }

    @Test
    public void parse_expenseList_returnsListExpenseCommand() throws SystemException {
        assertInstanceOf(ListExpenseCommand.class, parser.parse("expense list"));
    }

    @Test
    public void parse_expenseMissingProduct_throwsSystemException() {
        assertThrows(SystemException.class, () -> parser.parse("expense add a/100.50"));
    }

    @Test
    public void parse_expenseMissingAmount_throwsSystemException() {
        assertThrows(SystemException.class, () -> parser.parse("expense add p/Bread"));
    }

    @Test
    public void parse_expenseListWithExtraInput_throwsSystemException() {
        assertThrows(SystemException.class, () -> parser.parse("expense list extra"));
    }

    @Test
    public void parse_listCommand_returnsListCommand() throws SystemException {
        assertInstanceOf(ListProductCommand.class, parser.parse("product list"));
    }

    @Test
    public void parse_listCommandInMixedCase_returnsListCommand() throws SystemException {
        assertInstanceOf(ListProductCommand.class, parser.parse("Product LIST"));
    }

    @Test
    public void parse_listCommandWithExtraInput_throwsSystemException() {
        assertThrows(SystemException.class, () -> parser.parse("product list p/Oat Milk"));
    }

    @Test
    public void parse_productWithoutVerb_throwsSystemException() {
        assertThrows(SystemException.class, () -> parser.parse("product"));
    }

    @Test
    public void parse_unknownCommand_throwsSystemException() {
        assertThrows(SystemException.class, () -> parser.parse("bake bread"));
    }

    @Test
    public void parse_emptyInput_throwsSystemException() {
        assertThrows(SystemException.class, () -> parser.parse("   "));
    }

    @Test
    public void parse_orderWithTwoItems_returnsAddOrderCommandWithItemsInOrder() throws SystemException {
        Command command = parser.parse("order add c/Jonas Low p/Apple q/3 p/Carrot q/1");

        AddOrderCommand orderCommand = assertInstanceOf(AddOrderCommand.class, command);
        assertEquals("Jonas Low", orderCommand.getCustomer());
        assertEquals(List.of(new AddOrderCommand.RequestedItem("Apple", 3),
                new AddOrderCommand.RequestedItem("Carrot", 1)), orderCommand.getRequestedItems());
    }

    @Test
    public void parse_orderProductNameWithSpaces_keepsNameWhole() throws SystemException {
        AddOrderCommand orderCommand = assertInstanceOf(AddOrderCommand.class,
                parser.parse("order add c/Jonas p/Oat Milk q/2"));
        assertEquals("Oat Milk", orderCommand.getRequestedItems().get(0).productName());
    }

    @Test
    public void parse_orderWithoutItems_throwsSystemException() {
        assertThrows(SystemException.class, () -> parser.parse("order add c/Jonas Low"));
    }

    @Test
    public void parse_orderWithoutCustomer_throwsSystemException() {
        assertThrows(SystemException.class, () -> parser.parse("order add p/Apple q/3"));
    }

    @Test
    public void parse_orderProductMissingQuantity_throwsSystemException() {
        assertThrows(SystemException.class, () -> parser.parse("order add c/Jonas p/Apple q/3 p/Carrot"));
    }

    @Test
    public void parse_orderQuantityBeforeProduct_throwsSystemException() {
        assertThrows(SystemException.class, () -> parser.parse("order add c/Jonas q/3 p/Apple"));
    }

    @Test
    public void parse_orderSameProductTwice_throwsSystemException() {
        assertThrows(SystemException.class, () -> parser.parse("order add c/Jonas p/Apple q/1 p/apple q/2"));
    }

    @Test
    public void parse_orderDecimalQuantity_throwsSystemException() {
        assertThrows(SystemException.class, () -> parser.parse("order add c/Jonas p/Apple q/1.5"));
    }

    @Test
    public void parse_orderZeroQuantity_throwsSystemException() {
        assertThrows(SystemException.class, () -> parser.parse("order add c/Jonas p/Apple q/0"));
    }

    @Test
    public void parse_cancelOrder_returnsCancelCommandWithNumber() throws SystemException {
        CancelOrderCommand cancel = assertInstanceOf(CancelOrderCommand.class, parser.parse("order cancel 1"));
        assertEquals(1, cancel.getOrderId());
    }

    @Test
    public void parse_cancelOrderNonNumeric_throwsSystemException() {
        assertThrows(SystemException.class, () -> parser.parse("order cancel one"));
    }

    @Test
    public void parse_cancelOrderMissingNumber_throwsSystemException() {
        assertThrows(SystemException.class, () -> parser.parse("order cancel"));
    }

    @Test
    public void parse_orderList_returnsListOrderCommand() throws SystemException {
        assertInstanceOf(ListOrderCommand.class, parser.parse("Order LIST"));
    }

    @Test
    public void parse_orderListWithExtraInput_throwsSystemException() {
        assertThrows(SystemException.class, () -> parser.parse("order list 1"));
    }
}
