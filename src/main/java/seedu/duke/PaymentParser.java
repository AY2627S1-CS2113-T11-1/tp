package seedu.duke;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.duke.command.AddPaymentCommand;

/**
 * Reads the customer name and amount for an order payment.
 */
public class PaymentParser {
    private static final String FORMAT = "Format: order add pay c/CUSTOMER a/AMOUNT";
    private static final Pattern FIELDS = Pattern.compile("c/(.*?)\\s+a/(.*)");
    private static final Pattern EXTRA_PREFIX = Pattern.compile("(?:^|\\s)[a-zA-Z]/");

    /**
     * Creates a payment command from customer and amount fields, in that order.
     *
     * @param arguments text after order add pay
     * @return the payment command
     * @throws SystemException if a field is missing, repeated, or invalid
     */
    public AddPaymentCommand parse(String arguments) throws SystemException {
        Matcher fields = FIELDS.matcher(arguments.trim());
        if (!fields.matches()) {
            throw new SystemException("Provide the customer before the payment amount. " + FORMAT);
        }
        String customer = fields.group(1).trim();
        String amountText = fields.group(2).trim();
        if (customer.isEmpty()) {
            throw new SystemException("The customer name cannot be empty. " + FORMAT);
        }
        if (EXTRA_PREFIX.matcher(customer).find()) {
            throw new SystemException("Provide exactly one customer. " + FORMAT);
        }
        if (!amountText.matches("[0-9]+(?:\\.[0-9]{1,2})?")) {
            throw new SystemException("The payment must be a non-negative amount with at most 2 decimal places. "
                    + FORMAT);
        }
        return new AddPaymentCommand(customer, new BigDecimal(amountText).setScale(2));
    }
}
