package seedu.duke;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.duke.command.GenerateReceiptsCommand;

/**
 * Validates the receipt command while preserving spaces inside customer names.
 */
public class ReceiptParser {
    private static final String FORMAT = "Format: generate receipts c/CUSTOMER";
    private static final Pattern COMMAND = Pattern.compile("(?i:generate\\s+receipts)\\s+c/(.*)");
    private static final Pattern EXTRA_PREFIX = Pattern.compile("(?:^|\\s)[a-zA-Z]/");

    /**
     * Parses a complete receipt command with exactly one nonempty customer field.
     *
     * @param input the command typed by the user
     * @throws SystemException if the command or its customer field is invalid
     */
    public GenerateReceiptsCommand parse(String input) throws SystemException {
        Matcher matcher = COMMAND.matcher(input.trim());
        if (!matcher.matches()) {
            throw new SystemException(FORMAT);
        }
        String customer = matcher.group(1).trim();
        if (customer.isEmpty()) {
            throw new SystemException("The customer name cannot be empty. " + FORMAT);
        }
        if (EXTRA_PREFIX.matcher(customer).find()) {
            throw new SystemException("Specify exactly one customer and no other fields. " + FORMAT);
        }
        return new GenerateReceiptsCommand(customer);
    }
}
