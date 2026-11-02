package seedu.duke.command;

import java.util.LinkedHashMap;
import java.util.Map;

import seedu.duke.Stock;
import seedu.duke.StockList;
import seedu.duke.Order;
import seedu.duke.OrderItem;
import seedu.duke.OrderList;
import seedu.duke.ExpenseList;
import seedu.duke.Product;
import seedu.duke.ProductList;
import seedu.duke.SystemException;
import seedu.duke.Ui;

/**
 * Cancels an active order and puts its quantities back into stock.
 *
 * Corresponds to {@code order cancel ORDER_NUMBER}. The order stays in the order list, marked as
 * cancelled, so its number is not reused and sales reporting can leave its total out.
 */
public class CancelOrderCommand extends Command {
    /** Stock levels are ints, so this is the most a product can hold. */
    private static final int MAX_STOCK = Integer.MAX_VALUE;

    private final int orderId;

    /**
     * Constructs a command that cancels the order with the given number.
     *
     * @param orderId the order number, already validated as a positive integer by the parser
     */
    public CancelOrderCommand(int orderId) {
        this.orderId = orderId;
    }

    public int getOrderId() {
        return orderId;
    }

    @Override
    public void execute(ProductList products, OrderList orders, ExpenseList expenses, StockList stocks, Ui ui)
            throws SystemException {
        Order order = orders.findById(orderId);
        if (order == null) {
            throw new SystemException("    There is no order " + orderId + ".");
        }

        // An already-cancelled order is left for order.cancel() to reject with its own message.
        if (!order.isCancelled()) {
            checkStockCanTakeBack(order);
        }

        // Cancel first: if the order was already cancelled this throws before stock is restored twice.
        order.cancel();
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            if (stocks != null) {
                Stock stock = stocks.findByProduct(product.getName());
                if (stock != null) {
                    stock.setQuantity(product.getStock());
                }
            }
        }
        ui.showOrderCancelled(order);
    }

    /**
     * Rejects the cancellation if putting the order's units back would push any product's stock
     * past {@link #MAX_STOCK}.
     *
     * Stock can be restocked up to the maximum while an order holds some units, so returning those
     * units on cancel could overflow the int and wrap to a negative stock level. Units are totaled
     * per product as longs (in case one product appears on more than one line) and everything is
     * checked before any stock changes, so a rejected cancel leaves the order and stock untouched.
     *
     * @param order the active order about to be canceled
     * @throws SystemException if any product would end up with more than {@link #MAX_STOCK} units
     */
    private static void checkStockCanTakeBack(Order order) throws SystemException {
        Map<Product, Long> unitsToReturn = new LinkedHashMap<>();
        for (OrderItem item : order.getItems()) {
            unitsToReturn.merge(item.getProduct(), (long) item.getQuantity(), Long::sum);
        }
        for (Map.Entry<Product, Long> entry : unitsToReturn.entrySet()) {
            Product product = entry.getKey();
            long returned = entry.getValue();
            if (product.getStock() + returned > MAX_STOCK) {
                throw new SystemException("Can't cancel order " + order.getId() + ": returning " + returned
                        + " of " + product.getName() + " would take its stock past " + MAX_STOCK
                        + " (it has " + product.getStock() + ").\nNothing was changed.");
            }
        }
    }
}
