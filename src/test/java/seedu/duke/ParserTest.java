package seedu.duke;

import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.duke.command.AddOrderCommand;
import seedu.duke.command.AddExpenseCommand;
import seedu.duke.command.AddProductCommand;
import seedu.duke.command.AddStockCommand;
import seedu.duke.command.CancelOrderCommand;
import seedu.duke.command.Command;
import seedu.duke.command.ListExpenseCommand;
import seedu.duke.command.ListOrderCommand;
import seedu.duke.command.ListProductCommand;
import seedu.duke.command.ListSalesCommand;
import seedu.duke.command.ListStockCommand;
import seedu.duke.command.RemoveStockCommand;
import seedu.duke.command.EditOrderCommand;
import seedu.duke.command.DiscountOrderCommand;
import seedu.duke.command.LowStockCommand;

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
    public void parse_salesListCommand_returnsListSalesCommand() throws SystemException {
        assertInstanceOf(ListSalesCommand.class, parser.parse("sales list"));
    }

    @Test
    public void parse_salesListCommandWithExtraInput_throwsSystemException() {
        assertThrows(SystemException.class, () -> parser.parse("sales list today"));
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
    public void parse_editOrderCustomerAndQuantity_returnsEditCommandWithProductUnchanged() throws SystemException {
        EditOrderCommand edit = assertInstanceOf(EditOrderCommand.class,
                parser.parse("order edit 2 c/Mei Tan q/5"));
        assertEquals(2, edit.getOrderId());
        assertEquals("Mei Tan", edit.getNewCustomer());
        assertEquals(null, edit.getNewProductName());
        assertEquals(5, edit.getNewQuantity());
    }

    @Test
    public void parse_editOrderFieldsInAnyOrder_readsEachField() throws SystemException {
        EditOrderCommand edit = assertInstanceOf(EditOrderCommand.class,
                parser.parse("order edit 1 q/3 p/Oat Milk c/Raj s/o Kumar"));
        assertEquals("Raj s/o Kumar", edit.getNewCustomer());
        assertEquals("Oat Milk", edit.getNewProductName());
        assertEquals(3, edit.getNewQuantity());
    }

    @Test
    public void parse_editOrderWithoutFields_throwsSystemException() {
        assertThrows(SystemException.class, () -> parser.parse("order edit 2"));
        assertThrows(SystemException.class, () -> parser.parse("order edit 2   "));
    }

    @Test
    public void parse_editOrderWithoutNumber_throwsSystemException() {
        assertThrows(SystemException.class, () -> parser.parse("order edit"));
        assertThrows(SystemException.class, () -> parser.parse("order edit c/Mei"));
        assertThrows(SystemException.class, () -> parser.parse("order edit 0 c/Mei"));
    }

    @Test
    public void parse_editOrderRepeatedPrefix_throwsSystemException() {
        SystemException e = assertThrows(SystemException.class,
                () -> parser.parse("order edit 2 c/Mei c/Tan"));
        assertTrue(e.getMessage().contains("more than once"));
    }

    @Test
    public void parse_editOrderUnsupportedPrefix_throwsSystemException() {
        assertThrows(SystemException.class, () -> parser.parse("order edit 2 a/3.50"));
        assertThrows(SystemException.class, () -> parser.parse("order edit 2 c/Mei d/10"));
        assertThrows(SystemException.class, () -> parser.parse("order edit 2 x/5"));
        assertThrows(SystemException.class, () -> parser.parse("order edit 2 extra c/Mei"));
    }

    @Test
    public void parse_editOrderBlankValues_throwsSystemException() {
        assertThrows(SystemException.class, () -> parser.parse("order edit 2 c/"));
        assertThrows(SystemException.class, () -> parser.parse("order edit 2 p/ q/3"));
        assertThrows(SystemException.class, () -> parser.parse("order edit 2 q/"));
    }

    @Test
    public void parse_editOrderInvalidQuantity_throwsSystemException() {
        assertThrows(SystemException.class, () -> parser.parse("order edit 2 q/0"));
        assertThrows(SystemException.class, () -> parser.parse("order edit 2 q/-1"));
        assertThrows(SystemException.class, () -> parser.parse("order edit 2 q/1.5"));
        assertThrows(SystemException.class, () -> parser.parse("order edit 2 q/five"));
    }

    @Test
    public void parse_discountOrder_returnsDiscountCommand() throws SystemException {
        DiscountOrderCommand discount = assertInstanceOf(DiscountOrderCommand.class,
                parser.parse("order discount 2 d/10"));
        assertEquals(2, discount.getOrderId());
        assertEquals(new BigDecimal("10"), discount.getPercent());
    }

    @Test
    public void parse_discountBoundaryAndDecimalValues_accepted() throws SystemException {
        for (String percent : new String[] {"0", "100", "100.00", "12.5", "0.01", "99.99"}) {
            DiscountOrderCommand discount = assertInstanceOf(DiscountOrderCommand.class,
                    parser.parse("order discount 1 d/" + percent));
            assertEquals(new BigDecimal(percent), discount.getPercent());
        }
    }

    @Test
    public void parse_discountOutOfRange_throwsRangeMessage() {
        for (String percent : new String[] {"101", "100.01", "-1", "-0.5"}) {
            String input = "order discount 2 d/" + percent;
            SystemException e = assertThrows(SystemException.class, () -> parser.parse(input));
            assertEquals("Discount must be between 0 and 100.\n" + DiscountOrderCommand.NO_CHANGES_MESSAGE,
                    e.getMessage());
        }
    }

    @Test
    public void parse_discountInvalidNumber_throwsSystemException() {
        for (String percent : new String[] {"10.555", "10.500", "abc", "10%", "", "1e2", "+5", "10.", ".5"}) {
            String input = "order discount 2 d/" + percent;
            SystemException e = assertThrows(SystemException.class, () -> parser.parse(input));
            assertTrue(e.getMessage().endsWith(DiscountOrderCommand.NO_CHANGES_MESSAGE), percent);
        }
    }

    @Test
    public void parse_discountUnexpectedArguments_throwsSystemException() {
        assertThrows(SystemException.class, () -> parser.parse("order discount"));
        assertThrows(SystemException.class, () -> parser.parse("order discount 2"));
        assertThrows(SystemException.class, () -> parser.parse("order discount d/10"));
        assertThrows(SystemException.class, () -> parser.parse("order discount two d/10"));
        assertThrows(SystemException.class, () -> parser.parse("order discount 2 extra d/10"));
        assertThrows(SystemException.class, () -> parser.parse("order discount 2 d/10 extra"));
        assertThrows(SystemException.class, () -> parser.parse("order discount 2 d/10 d/5"));
        assertThrows(SystemException.class, () -> parser.parse("order discount 2 c/Mei d/10"));
        assertThrows(SystemException.class, () -> parser.parse("order discount 2 d/10 q/3"));
    }

    @Test
    public void parse_discountWithOtherPrefixBeforeD_reportsThatPrefix() {
        SystemException e = assertThrows(SystemException.class,
                () -> parser.parse("order discount 2 c/Mei d/10"));
        assertTrue(e.getMessage().contains("only accepts d/, but I found c/"));
    }

    @Test
    public void parse_orderList_returnsListOrderCommand() throws SystemException {
        assertInstanceOf(ListOrderCommand.class, parser.parse("Order LIST"));
    }

    @Test
    public void parse_orderListWithExtraInput_throwsSystemException() {
        assertThrows(SystemException.class, () -> parser.parse("order list 1"));
    }

    @Test
    public void parse_validStockAdd_returnsAddStockCommand() throws SystemException {
        Command command = parser.parse("stock add p/as q/12");
        AddStockCommand addStock = assertInstanceOf(AddStockCommand.class, command);
        assertEquals("as", addStock.getProductName());
        assertEquals(12, addStock.getQuantity());
    }

    @Test
    public void parse_stockAddMissingPrefixes_throwsSystemException() {
        SystemException e = assertThrows(SystemException.class, () -> parser.parse("stock add"));
        assertTrue(e.getMessage().contains("Format: stock add p/PRODUCT q/QUANTITY"));
    }

    @Test
    public void parse_stockRemove_returnsRemoveStockCommand() throws SystemException {
        Command command = parser.parse("stock remove p/as q/3");
        RemoveStockCommand removeStock = assertInstanceOf(RemoveStockCommand.class, command);
        assertEquals("as", removeStock.getProductName());
        assertEquals(3, removeStock.getQuantityToRemove());
    }

    @Test
    public void parse_stockList_returnsListStockCommand() throws SystemException {
        assertInstanceOf(ListStockCommand.class, parser.parse("stock list"));
    }

    @Test
    public void parse_stockLowDefault_returnsLowStockCommand() throws SystemException {
        Command command = parser.parse("stock low");
        LowStockCommand lowStock = assertInstanceOf(LowStockCommand.class, command);
        assertEquals(5, lowStock.getThreshold());
    }

    @Test
    public void parse_stockLowWithPrefix_returnsLowStockCommand() throws SystemException {
        Command command = parser.parse("stock low q/10");
        LowStockCommand lowStock = assertInstanceOf(LowStockCommand.class, command);
        assertEquals(10, lowStock.getThreshold());
    }

    @Test
    public void parse_stockLowWithoutPrefix_returnsLowStockCommand() throws SystemException {
        Command command = parser.parse("stock low 15");
        LowStockCommand lowStock = assertInstanceOf(LowStockCommand.class, command);
        assertEquals(15, lowStock.getThreshold());
    }

    @Test
    public void parse_stockLowZeroThreshold_returnsLowStockCommand() throws SystemException {
        Command command = parser.parse("stock low q/0");
        LowStockCommand lowStock = assertInstanceOf(LowStockCommand.class, command);
        assertEquals(0, lowStock.getThreshold());
    }

    @Test
    public void parse_stockLowInvalidInputs_throwsSystemException() {
        assertThrows(SystemException.class, () -> parser.parse("stock low q/-1"));
        assertThrows(SystemException.class, () -> parser.parse("stock low -1"));
        assertThrows(SystemException.class, () -> parser.parse("stock low q/"));
        assertThrows(SystemException.class, () -> parser.parse("stock low abc"));
        assertThrows(SystemException.class, () -> parser.parse("stock low 5 extra"));
    }
}
