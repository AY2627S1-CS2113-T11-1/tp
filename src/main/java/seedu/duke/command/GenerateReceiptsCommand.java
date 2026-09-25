package seedu.duke.command;

import seedu.duke.OrderList;
import seedu.duke.ProductList;
import seedu.duke.ReceiptGenerator;
import seedu.duke.SystemException;
import seedu.duke.Ui;

/**
 * Generates an individual receipt for each confirmed purchase belonging to a customer.
 * Corresponds to {@code generate receipts c/CUSTOMER}.
 */
public class GenerateReceiptsCommand extends Command {
    private final String customer;

    /**
     * Constructs a command with the full customer name validated by the receipt parser.
     *
     * @param customer the customer whose receipts should be shown
     */
    public GenerateReceiptsCommand(String customer) {
        this.customer = customer;
    }

    @Override
    public void execute(ProductList products, OrderList orders, Ui ui) throws SystemException {
        ui.showReceipts(customer, new ReceiptGenerator().generate(orders, customer));
    }
}
