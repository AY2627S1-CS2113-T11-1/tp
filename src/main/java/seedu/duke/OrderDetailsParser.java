package seedu.duke;

import java.util.regex.Pattern;

import seedu.duke.command.OrderDetailsCommand;

/**
 * Reads the customer name for an order details command.
 */
public class OrderDetailsParser {
    private static final String FORMAT = "Format: order details c/CUSTOMER";
    private static final Pattern EXTRA_PREFIX = Pattern.compile("(?:^|\\s)[a-zA-Z]/");

    /**
     * Creates a details command with one non-empty customer name.
     *
     * @param arguments text after order details
     * @return command for the requested customer
     * @throws SystemException if the customer field is missing or invalid
     */
    public OrderDetailsCommand parse(String arguments) throws SystemException {
        String input = arguments.trim();
        if (!input.startsWith("c/")) {
            throw new SystemException("Provide the customer using c/CUSTOMER. " + FORMAT);
        }
        String customer = input.substring(2).trim();
        if (customer.isEmpty()) {
            throw new SystemException("The customer name cannot be empty. " + FORMAT);
        }
        if (EXTRA_PREFIX.matcher(customer).find()) {
            throw new SystemException("Provide exactly one customer and no other fields. " + FORMAT);
        }
        return new OrderDetailsCommand(customer);
    }
}
