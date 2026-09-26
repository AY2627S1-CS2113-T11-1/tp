package seedu.duke.command;

import java.util.ArrayList;
import java.util.List;

import seedu.duke.Order;
import seedu.duke.OrderItem;
import seedu.duke.OrderList;
import seedu.duke.Product;
import seedu.duke.ProductList;
import seedu.duke.SystemException;
import seedu.duke.Ui;

/**
 * Records a customer's purchase and deducts the bought quantities from stock.
 *
 * Corresponds to {@code order add c/CUSTOMER p/PRODUCT q/QUANTITY [p/PRODUCT q/QUANTITY]...}.
 * The order is all-or-nothing: every line is checked before any stock is touched, so a single
 * bad line leaves both the stock and the order list exactly as they were.
 */
public class AddOrderCommand extends Command {
    /**
     * One product-quantity pair as typed by the user, before it is matched to a catalogue product.
     *
     * @param productName the name after p/, already validated as non-empty by the parser
     * @param quantity    the number after q/, already validated as positive by the parser
     */
    public record RequestedItem(String productName, int quantity) {
    }

    private final String customer;
    private final List<RequestedItem> requestedItems;

    /**
     * Constructs a command that creates an order for the given customer.
     *
     * @param customer       the customer's name, already validated as non-empty by the parser
     * @param requestedItems at least one item, with no product named twice (checked by the parser)
     */
    public AddOrderCommand(String customer, List<RequestedItem> requestedItems) {
        this.customer = customer;
        this.requestedItems = List.copyOf(requestedItems);
    }

    public String getCustomer() {
        return customer;
    }

    public List<RequestedItem> getRequestedItems() {
        return requestedItems;
    }

    @Override
    public void execute(ProductList products, OrderList orders, Ui ui) throws SystemException {
        // Pass 1: check every line. Nothing is changed yet, so throwing here leaves no trace.
        List<Product> matchedProducts = new ArrayList<>();
        for (RequestedItem requested : requestedItems) {
            Product product = products.findByName(requested.productName());
            if (product == null) {
                throw new SystemException("    There is no product named \"" + requested.productName()
                        + "\". No order was created.");
            }
            if (requested.quantity() > product.getStock()) {
                throw new SystemException("    Only " + product.getStock() + " of " + product.getName()
                        + " in stock, but the order asks for " + requested.quantity() + ". No order was created.");
            }
            matchedProducts.add(product);
        }

        // Pass 2: every line is valid, so deduct stock and record the order.
        List<OrderItem> items = new ArrayList<>();
        for (int i = 0; i < requestedItems.size(); i++) {
            Product product = matchedProducts.get(i);
            int quantity = requestedItems.get(i).quantity();
            product.setStock(product.getStock() - quantity);
            items.add(new OrderItem(product, quantity));
        }
        Order order = orders.create(customer, items);
        ui.showOrderCreated(order);
    }
}
