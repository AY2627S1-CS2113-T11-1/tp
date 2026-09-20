package seedu.duke;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.duke.command.AddProductCommand;
import seedu.duke.command.Command;
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
}
