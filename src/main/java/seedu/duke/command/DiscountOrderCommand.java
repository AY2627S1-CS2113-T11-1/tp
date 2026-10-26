package seedu.duke.command;

import java.math.BigDecimal;
import java.util.logging.Logger;

import seedu.duke.ExpenseList;
import seedu.duke.Order;
import seedu.duke.OrderList;
import seedu.duke.ProductList;
import seedu.duke.StockList;
import seedu.duke.SystemException;
import seedu.duke.Ui;

/**
 * Applies, replaces or removes a percentage discount on an active order.
 *
 * Corresponds to {@code order discount ORDER_NUMBER d/PERCENTAGE}. Only the order's total changes: stock and
 * the stored unit prices are left alone. A percentage of 0 removes the discount.
 */
public class DiscountOrderCommand extends Command {
    /** Appended to every rejection so the user knows the order was left as it was. */
    public static final String NO_CHANGES_MESSAGE = "No changes were made to the order.";

    private static final Logger logger = Logger.getLogger(DiscountOrderCommand.class.getName());

    private final int orderId;
    private final BigDecimal percent;

    /**
     * Constructs a command that sets the discount on the given order.
     *
     * @param orderId the order number, validated as positive by the parser
     * @param percent the discount percentage, validated by the parser as 0 to 100 with at most two decimals
     */
    public DiscountOrderCommand(int orderId, BigDecimal percent) {
        assert orderId > 0 : "Parser must supply a positive order number";
        assert percent != null : "Parser must supply a discount percentage";
        assert percent.signum() >= 0 && percent.compareTo(BigDecimal.valueOf(100)) <= 0
                : "Parser must keep the discount percentage between 0 and 100";

        this.orderId = orderId;
        this.percent = percent;
    }

    public int getOrderId() {
        return orderId;
    }

    public BigDecimal getPercent() {
        return percent;
    }

    /**
     * Sets the discount on the order and shows the resulting subtotal, discount and total.
     *
     * @throws SystemException if the order does not exist or has been cancelled
     */
    @Override
    public void execute(ProductList products, OrderList orders, ExpenseList expenses, StockList stocks, Ui ui)
            throws SystemException {
        Order order = orders.findById(orderId);
        if (order == null) {
            logger.fine(() -> "Discount rejected: no order " + orderId);
            throw new SystemException("There is no order " + orderId + ".\n" + NO_CHANGES_MESSAGE);
        }
        if (order.isCancelled()) {
            logger.fine(() -> "Discount rejected: order " + orderId + " is cancelled");
            throw new SystemException("Order " + orderId + " is cancelled and cannot receive a discount.\n"
                    + NO_CHANGES_MESSAGE);
        }

        double subtotalBefore = order.getSubtotal();
        order.setDiscountPercent(percent);

        // A discount only ever changes the amount paid, never what was bought or at what unit price.
        assert order.getSubtotal() == subtotalBefore : "Setting a discount must not change the subtotal";
        assert order.getTotal() <= order.getSubtotal() : "A discount can never raise the total";
        logger.fine(() -> "Set discount on order " + orderId + " to " + percent.toPlainString() + "%");

        ui.showDiscountUpdated(order);
    }
}
