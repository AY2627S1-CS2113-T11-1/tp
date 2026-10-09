package seedu.duke.command;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import seedu.duke.Order;
import seedu.duke.ExpenseList;
import seedu.duke.OrderItem;
import seedu.duke.OrderList;
import seedu.duke.Product;
import seedu.duke.ProductList;
import seedu.duke.SystemException;
import seedu.duke.Ui;

/**
 * Records a customer's purchase and deducts the bought quantities from stock.
 *
 * Corresponds to
 * {@code order add c/CUSTOMER p/PRODUCT q/QUANTITY [p/PRODUCT q/QUANTITY]...}.
 * Every line is checked before any stock is changed.
 */
public class AddOrderCommand extends Command {
    /** Records order creation events for debugging. */
    private static final Logger logger =
            Logger.getLogger(AddOrderCommand.class.getName());

    /**
     * Represents one product-quantity pair entered by the user.
     *
     * @param productName the product name, validated as non-empty by the parser
     * @param quantity the quantity, validated as positive by the parser
     */
    public record RequestedItem(String productName, int quantity) {
    }

    private final String customer;
    private final List<RequestedItem> requestedItems;

    /**
     * Constructs a command that creates an order for the given customer.
     *
     * @param customer the customer's name, validated as non-empty by the parser
     * @param requestedItems at least one item, with no product named twice
     */
    public AddOrderCommand(String customer, List<RequestedItem> requestedItems) {
        assert customer != null && !customer.isBlank()
                : "Parser must supply a non-empty customer name";
        assert requestedItems != null && !requestedItems.isEmpty()
                : "Parser must supply at least one order item";

        this.customer = customer;
        this.requestedItems = List.copyOf(requestedItems);
    }

    public String getCustomer() {
        return customer;
    }

    public List<RequestedItem> getRequestedItems() {
        return requestedItems;
    }

    /**
     * Validates the requested products, deducts stock, and records the order.
     *
     * @param products the product catalogue
     * @param orders the order list
     * @param expenses the shared expense list
     * @param ui the user interface
     * @throws SystemException if a product does not exist or has insufficient stock
     */
    @Override
    public void execute(ProductList products, OrderList orders,
                        ExpenseList expenses, Ui ui) throws SystemException {
        // Pass 1: validate every line before changing any stock.
        List<Product> matchedProducts = new ArrayList<>();

        for (RequestedItem requested : requestedItems) {
            assert requested.quantity() > 0
                    : "Parser must supply a positive quantity";

            Product product = products.findByName(requested.productName());

            if (product == null) {
                logger.warning("Order creation rejected: product not found");
                throw new SystemException(
                        "    There is no product named \"" + requested.productName()
                                + "\". No order was created.");
            }

            if (requested.quantity() > product.getStock()) {
                logger.warning("Order creation rejected: insufficient stock");
                throw new SystemException(
                        "    Only " + product.getStock() + " of " + product.getName()
                                + " in stock, but the order asks for "
                                + requested.quantity() + ". No order was created.");
            }

            matchedProducts.add(product);
        }

        assert matchedProducts.size() == requestedItems.size()
                : "Every requested item must have a matched product";

        // Pass 2: all lines are valid, so deduct stock and record the order.
        List<OrderItem> items = new ArrayList<>();

        for (int i = 0; i < requestedItems.size(); i++) {
            Product product = matchedProducts.get(i);
            int quantity = requestedItems.get(i).quantity();

            product.setStock(product.getStock() - quantity);

            assert product.getStock() >= 0
                    : "Validated order must not produce negative stock";

            items.add(new OrderItem(product, quantity));
        }

        Order order = orders.create(customer, items);

        logger.info("Created order " + order.getId()
                + " with " + items.size() + " item lines");

        ui.showOrderCreated(order);
    }
}
