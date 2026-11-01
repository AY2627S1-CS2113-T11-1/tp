package seedu.duke.command;

import java.util.List;

import seedu.duke.ExpenseList;
import seedu.duke.OrderList;
import seedu.duke.Product;
import seedu.duke.ProductList;
import seedu.duke.StockList;
import seedu.duke.Ui;

/**
 * Lists the products whose name contains a keyword, ignoring case.
 *
 * Corresponds to {@code product find KEYWORD}. It only reads the catalogue, so it never fails:
 * a keyword that matches nothing is reported as an ordinary result.
 */
public class FindProductCommand extends Command {
    private final String keyword;

    /**
     * Constructs a command that searches the catalogue for the given keyword.
     *
     * @param keyword the text to look for, already validated as non-empty by the parser
     */
    public FindProductCommand(String keyword) {
        this.keyword = keyword;
    }

    public String getKeyword() {
        return keyword;
    }

    @Override
    public void execute(ProductList products, OrderList orders, ExpenseList expenses, StockList stocks, Ui ui) {
        List<Product> matches = products.findByKeyword(keyword);
        ui.showMatchingProducts(keyword, matches);
    }
}
