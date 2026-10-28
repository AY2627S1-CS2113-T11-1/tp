package seedu.duke.command;

import java.util.List;
import java.util.logging.Logger;

import seedu.duke.ExpenseList;
import seedu.duke.Order;
import seedu.duke.OrderItem;
import seedu.duke.OrderList;
import seedu.duke.Product;
import seedu.duke.ProductList;
import seedu.duke.Stock;
import seedu.duke.StockList;
import seedu.duke.SystemException;
import seedu.duke.Ui;

/**
 * Changes the customer, product or quantity of an active order, keeping its order number.
 *
 * Corresponds to {@code order edit ORDER_NUMBER [c/CUSTOMER] [p/PRODUCT] [q/QUANTITY]}.
 *
 * <p>Like {@link AddOrderCommand}, the command works in two passes: every check runs first, and only
 * once all of them pass is any stock moved or the order changed. That way a failed edit leaves the
 * order and stock exactly as they were, without needing an undo step.
 *
 * <p>Product and quantity changes are only supported for orders with a single product line, because
 * {@code p/} and {@code q/} cannot say which line of a multi-product order they refer to. The customer
 * can be changed on any active order.
 */
public class EditOrderCommand extends Command {
    /** Appended to every rejection so the user knows nothing was partially applied. */
    public static final String NO_CHANGES_MESSAGE = "No changes were made to the order or stock.";

    private static final Logger logger = Logger.getLogger(EditOrderCommand.class.getName());

    private final int orderId;
    private final String newCustomer;
    private final String newProductName;
    private final Integer newQuantity;

    /**
     * Constructs a command that edits the given order. A null field means "leave unchanged".
     *
     * @param orderId        the order number, validated as positive by the parser
     * @param newCustomer    the new customer name, or null to keep the current one
     * @param newProductName the name of the replacement product, or null to keep the current one
     * @param newQuantity    the new quantity, or null to keep the current one
     */
    public EditOrderCommand(int orderId, String newCustomer, String newProductName, Integer newQuantity) {
        assert orderId > 0 : "Parser must supply a positive order number";
        assert newCustomer != null || newProductName != null || newQuantity != null
                : "Parser must supply at least one field to edit";
        assert newCustomer == null || !newCustomer.isBlank() : "Parser must reject a blank customer name";
        assert newProductName == null || !newProductName.isBlank() : "Parser must reject a blank product name";
        assert newQuantity == null || newQuantity > 0 : "Parser must reject a quantity below 1";

        this.orderId = orderId;
        this.newCustomer = newCustomer;
        this.newProductName = newProductName;
        this.newQuantity = newQuantity;
    }

    public int getOrderId() {
        return orderId;
    }

    public String getNewCustomer() {
        return newCustomer;
    }

    public String getNewProductName() {
        return newProductName;
    }

    public Integer getNewQuantity() {
        return newQuantity;
    }

    /**
     * Validates the edit, moves stock to match it, and updates the order.
     *
     * @throws SystemException if the order does not exist, is cancelled, has several product lines while
     *                         p/ or q/ is given, names an unknown product, or needs more stock than is available
     */
    @Override
    public void execute(ProductList products, OrderList orders, ExpenseList expenses, StockList stocks, Ui ui)
            throws SystemException {
        // ---- Pass 1: checks only. Nothing below may change state until pass 2. ----
        Order order = orders.findById(orderId);
        if (order == null) {
            logger.fine(() -> "Order edit rejected: no order " + orderId);
            throw reject("There is no order " + orderId + ".");
        }
        if (order.isCancelled()) {
            logger.fine(() -> "Order edit rejected: order " + orderId + " is cancelled");
            throw reject("Order " + orderId + " is cancelled and cannot be edited.");
        }

        String customer = newCustomer == null ? order.getCustomer() : newCustomer;
        boolean isChangingLines = newProductName != null || newQuantity != null;

        List<OrderItem> updatedItems = order.getItems();
        if (isChangingLines) {
            updatedItems = List.of(planLineChange(order, products));
        }

        // ---- Pass 2: every check passed, so apply the stock movement and the new details. ----
        OrderItem oldLine = order.getItems().get(0);
        if (isChangingLines) {
            OrderItem newLine = updatedItems.get(0);
            moveStock(oldLine, newLine, stocks);
        }
        order.update(customer, updatedItems);

        assert order.getId() == orderId : "Editing must never change the order number";
        assert !order.isCancelled() : "Editing must never cancel an order";
        logger.fine(() -> "Edited order " + orderId + ": customer=" + order.getCustomer()
                + ", units=" + order.getTotalUnits());

        ui.showOrderUpdated(order);
    }

    /**
     * Works out the replacement order line and checks that stock can cover it, without changing anything.
     *
     * @param order    the active order being edited
     * @param products the catalogue, used to look up a replacement product
     * @return the line the order should have after the edit
     * @throws SystemException if the order has several lines, the product is unknown, or stock is short
     */
    private OrderItem planLineChange(Order order, ProductList products) throws SystemException {
        if (order.getItems().size() != 1) {
            logger.fine(() -> "Order edit rejected: order " + orderId + " has several product lines");
            throw reject("Order " + orderId + " has " + order.getItems().size() + " products, so p/ and q/ "
                    + "cannot tell which one to change. Only c/ can be edited on this order.");
        }

        OrderItem oldLine = order.getItems().get(0);
        Product oldProduct = oldLine.getProduct();
        Product product = oldProduct;
        if (newProductName != null) {
            product = products.findByName(newProductName);
            if (product == null) {
                logger.fine(() -> "Order edit rejected: unknown product " + newProductName);
                throw reject("There is no product named \"" + newProductName + "\".");
            }
        }
        int quantity = newQuantity == null ? oldLine.getQuantity() : newQuantity;

        // Same product (names match ignoring case, so findByName returns the same object):
        // only the difference in quantity needs to come from stock.
        boolean isSameProduct = product == oldProduct;
        int unitsNeededFromStock = isSameProduct ? quantity - oldLine.getQuantity() : quantity;
        if (unitsNeededFromStock > product.getStock()) {
            logger.fine(() -> "Order edit rejected: insufficient stock for order " + orderId);
            throw reject("Unable to edit order: insufficient stock.");
        }

        // An unchanged product keeps the price the customer was quoted; a replacement uses today's price.
        return isSameProduct
                ? new OrderItem(product, quantity, oldLine.getUnitPrice())
                : new OrderItem(product, quantity);
    }

    /**
     * Returns the old line's units to stock and takes the new line's units out of stock.
     *
     * For an unchanged product the two steps cancel out except for the difference, which is exactly
     * "increase deducts the extra units, decrease returns the difference".
     */
    private static void moveStock(OrderItem oldLine, OrderItem newLine, StockList stocks) {
        Product oldProduct = oldLine.getProduct();
        Product newProduct = newLine.getProduct();
        int combinedStockBefore = oldProduct.getStock() + (oldProduct == newProduct ? 0 : newProduct.getStock());

        oldProduct.setStock(oldProduct.getStock() + oldLine.getQuantity());
        newProduct.setStock(newProduct.getStock() - newLine.getQuantity());

        if (stocks != null) {
            Stock oldStock = stocks.findByProduct(oldProduct.getName());
            if (oldStock != null) {
                oldStock.setQuantity(oldProduct.getStock());
            }
            Stock newStock = stocks.findByProduct(newProduct.getName());
            if (newStock != null) {
                newStock.setQuantity(newProduct.getStock());
            }
        }

        // Units are only moved between the order and the shelf, never created or lost.
        int combinedStockAfter = oldProduct.getStock() + (oldProduct == newProduct ? 0 : newProduct.getStock());
        assert combinedStockAfter == combinedStockBefore + oldLine.getQuantity() - newLine.getQuantity()
                : "Editing an order must conserve the total number of units";
    }

    /**
     * Returns a user-facing error that states the reason and confirms nothing was changed.
     */
    private static SystemException reject(String reason) {
        return new SystemException(reason + "\n" + NO_CHANGES_MESSAGE);
    }
}
