package seedu.duke;

import seedu.duke.command.OrderDetailsCommand;

/**
 * Reads the customer name for an order details command.
 */
public class OrderDetailsParser {
    private static final String FORMAT = "Format: order details c/CUSTOMER";

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
        CustomerNameValidator.validate(customer, FORMAT);
        return new OrderDetailsCommand(customer);
    }
}
