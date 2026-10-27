package seedu.duke.command;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import seedu.duke.ExpenseList;
import seedu.duke.Order;
import seedu.duke.OrderItem;
import seedu.duke.OrderList;
import seedu.duke.ProductList;
import seedu.duke.StockList;
import seedu.duke.SystemException;
import seedu.duke.Ui;

/**
 * Records a payment against the customer's unpaid, non-cancelled orders.
 */
public class AddPaymentCommand extends Command {
    private static final Logger logger = Logger.getLogger(AddPaymentCommand.class.getName());

    private final String customer;
    private final BigDecimal amount;

    /**
     * Creates a payment command with values checked by the parser.
     *
     * @param customer full customer name
     * @param amount non-negative payment with at most two decimal places
     */
    public AddPaymentCommand(String customer, BigDecimal amount) {
        assert customer != null && !customer.isBlank() : "Payment requires a customer name";
        assert amount != null && amount.signum() >= 0 && amount.scale() <= 2 : "Payment requires a valid amount";
        this.customer = customer.trim();
        this.amount = amount.setScale(2);
    }

    /**
     * Applies the payment to matching orders in creation order. A payment may be partial;
     * any amount above the outstanding balance is returned as change.
     *
     * @throws SystemException if the customer is unknown or the payment cannot be accepted
     */
    @Override
    public void execute(ProductList products, OrderList orders, ExpenseList expenses, StockList stocks, Ui ui)
            throws SystemException {
        boolean customerFound = false;
        List<Order> unpaidOrders = new ArrayList<>();
        BigDecimal outstanding = BigDecimal.ZERO.setScale(2);
        for (Order order : orders.getAll()) {
            if (order.getCustomer().equalsIgnoreCase(customer)) {
                customerFound = true;
                if (!order.isCancelled() && !order.isPaid()) {
                    unpaidOrders.add(order);
                    outstanding = outstanding.add(getAmountDue(order));
                }
            }
        }
        if (!customerFound) {
            throw new SystemException("Customer " + customer + " is not a registered customer.");
        }
        if (unpaidOrders.isEmpty()) {
            throw new SystemException("Customer " + customer + " has no unpaid, non-cancelled orders.");
        }
        if (amount.signum() == 0 && outstanding.signum() > 0) {
            logger.fine("Payment rejected: insufficient amount");
            throw new SystemException("Insufficient payment. Customer " + customer + " still needs to pay $"
                    + outstanding.subtract(amount).toPlainString() + ".");
        }
        BigDecimal remaining = amount;
        for (Order order : unpaidOrders) {
            BigDecimal due = getAmountDue(order);
            BigDecimal applied = remaining.min(due);
            if (applied.signum() > 0 || due.signum() == 0) {
                order.recordPayment(applied);
            }
            remaining = remaining.subtract(applied);
            if (remaining.signum() == 0) {
                break;
            }
        }
        logger.fine(() -> "Recorded payment for " + amount + " for " + unpaidOrders.size() + " order(s)");
        ui.showPayment(customer, amount, amount.subtract(outstanding).max(BigDecimal.ZERO));
    }

    /**
     * Calculates the amount using the saved exact decimal unit prices.
     *
     * @param order order to calculate
     * @return total amount rounded to cents
     * @throws SystemException if a saved price cannot be used for payment
     */
    private BigDecimal getAmountDue(Order order) throws SystemException {
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem item : order.getItems()) {
            BigDecimal price = item.getUnitPrice();
            if (price == null || price.signum() < 0) {
                throw new SystemException("Order " + order.getId()
                        + " has an invalid price. Payment was not recorded.");
            }
            total = total.add(price.multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        return total.setScale(2, RoundingMode.HALF_UP).subtract(order.getPaidAmount());
    }
}
