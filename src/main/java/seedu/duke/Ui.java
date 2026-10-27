package seedu.duke;

import java.io.InputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Handles all reading from and writing to the user.
 *
 * Keeping every {@code System.out.println} in this one class means the rest of the system can be
 * unit-tested without capturing console output, and the wording of a message only ever
 * changes in one place.
 */
public class Ui {
    /**
     * Column layout of one catalogue line: name padded to 18 characters, then price padded to 8.
     */
    private static final String PRODUCT_LINE_FORMAT = "   %-18s%-8sstock %d";
    private static final String INDENT = "    ";
    private static final String DIVIDER = INDENT + "_".repeat(60);
    private static final String banner = "    +--------------------------------------+\n"
            + "    |       BUSINESS MANAGEMENT SYSTEM     |\n"
            + "    |                 (BMS)                |\n"
            + "    +--------------------------------------+\n";

    private final Scanner scanner;
    private final PrintStream out;

    /**
     * Constructs a Ui that reads from standard input and writes to standard output.
     */
    public Ui() {
        this(System.in, System.out);
    }

    /**
     * Constructs a Ui that reads from and writes to the given streams.
     *
     * Tests use this constructor to feed in scripted input and inspect the exact output.
     *
     * @param in  the stream commands are read from
     * @param out the stream messages are written to
     */
    public Ui(InputStream in, PrintStream out) {
        this.scanner = new Scanner(in);
        this.out = out;
    }

    /**
     * Returns the next line typed by the user, or null if there is no more input.
     *
     * Null signals end of input (for example, the user pressed Ctrl-D or input was piped in),
     * which lets the main loop stop instead of throwing.
     */
    public String readCommand() {
        if (!scanner.hasNextLine()) {
            return null;
        }
        return scanner.nextLine();
    }

    public void showWelcome() {
        printBanner();
        printIndent("Welcome to BMS.");
        printIndent("Type a command, or \"exit\" to close BMS.");
    }

    private void printBanner() {
        out.println(banner);
    }

    public void printDivider() {
        out.println(DIVIDER);
    }

    private void printIndent(String line) {
        out.println(INDENT + line);
    }

    /**
     * Shows the confirmation for updated stock.
     *
     * @param stock the stock entry that was updated
     */
    public void showStockUpdated(Stock stock) {
        printIndent("Updated stock for " + stock.product + ". New quantity: " + stock.quantity);
    }

    /**
     * Shows the confirmation for newly created stock.
     *
     * @param stock the stock entry that was created
     */
    public void showStockCreated(Stock stock) {
        String formattedPrice = stock.price.stripTrailingZeros().toPlainString();
        printIndent("Creates stock for a new product " + stock.product
                + " with quantity " + stock.quantity + " and cost $" + formattedPrice + " each.");
    }

    /**
     * Shows a message indicating that the requested product was not found in stock.
     */
    public void showStockNotFound() {
        printIndent("Product not found in stock.");
    }

    /**
     * Shows confirmation for removed stock.
     *
     * @param productName the name of the product
     * @param quantityRemoved the quantity removed
     * @param defaultBehavior true if 1 unit was removed by default
     */
    public void showStockRemoved(String productName, int quantityRemoved, boolean defaultBehavior) {
        if (defaultBehavior) {
            printIndent("Removes 1 unit (default behaviour) of " + productName);
        } else {
            String unitLabel = quantityRemoved == 1 ? " unit of " : " units of ";
            printIndent("Removes " + quantityRemoved + unitLabel + productName);
        }
    }

    /**
     * Shows every stock entry as a table with Product and Qty columns.
     *
     * @param stocks the stock entries to display
     */
    public void showStocks(List<Stock> stocks) {
        String[] headers = {"Product", "Qty"};
        List<String[]> rows = new ArrayList<>();
        for (Stock stock : stocks) {
            rows.add(new String[] {stock.product, String.valueOf(stock.quantity)});
        }
        boolean[] isLeftAligned = {true, false};
        printTable(headers, rows, null, isLeftAligned);
    }

    /**
     * Displays products with stock levels at or below the given threshold.
     *
     * @param lowStocks the list of low stock items to display
     * @param threshold the threshold used for filtering
     * @param isStockListEmpty whether the entire stock list is empty
     */
    public void showLowStock(List<Stock> lowStocks, int threshold, boolean isStockListEmpty) {
        if (isStockListEmpty) {
            printIndent("There are currently no stocks in the list.");
            return;
        }
        if (lowStocks.isEmpty()) {
            printIndent("All products have sufficient stock (more than " + threshold + ").");
            return;
        }
        printIndent("Low stock items (quantity <= " + threshold + "):");
        String[] headers = {"Product", "Qty"};
        List<String[]> rows = new ArrayList<>();
        for (Stock stock : lowStocks) {
            rows.add(new String[] {stock.product, String.valueOf(stock.quantity)});
        }
        boolean[] isLeftAligned = {true, false};
        printTable(headers, rows, null, isLeftAligned);
    }

    public void showGoodbye() {
        printDivider();
        printIndent("Goodbye!");
    }

    /**
     * Shows the confirmation for a newly added product.
     *
     * @param product the product that was just added
     */
    public void showProductAdded(Product product) {
        printIndent("Added product " + product.getName());
        printIndent("Price: " + product.getPriceString());
        printIndent("Stock: " + product.getStock());
    }

    /**
     * Shows the confirmation for a newly recorded expense.
     *
     * @param expense the expense that was just recorded
     */
    public void showExpenseAdded(Expense expense) {
        printIndent("Expense added:");
        printIndent(String.format("%s: $%.2f", expense.getProductName(), expense.getAmount()));
    }

    /**
     * Shows the catalogue, one product per line, in the order given.
     *
     * @param products the products to display, already sorted by the caller
     */
    public void showProducts(List<Product> products) {
        printIndent("Products (" + products.size() + "):");
        if (products.isEmpty()) {
            printIndent("Nothing here yet. Add one with: product add p/PRODUCT a/AMOUNT");
        } else {
            for (Product product : products) {
                printIndent(String.format(PRODUCT_LINE_FORMAT,
                        product.getName(), product.getPriceString(), product.getStock()));
            }
        }
    }

    /**
     * Shows every recorded expense in insertion order.
     *
     * @param expenses the expenses to display
     */
    public void showExpenses(List<Expense> expenses) {
        printIndent("Expenses:");
        if (expenses.isEmpty()) {
            printIndent("No expenses have been added");
            return;
        }

        for (int i = 0; i < expenses.size(); i++) {
            Expense expense = expenses.get(i);
            printIndent((i + 1) + ". " + expense.getProductName() + ": " + formatMoney(expense.getAmount()));
        }
    }

    /**
     * Shows the confirmation for a newly created order as a table: one row per product, then a
     * total row.
     *
     * @param order the order that was just created
     */
    public void showOrderCreated(Order order) {
        printIndent("Order " + order.getId() + " created for " + order.getCustomer() + ".");
        printOrderTable(order, "Qty");
    }

    /**
     * Shows the confirmation for a cancelled order, including the stock restored
     * and the amount excluded from recorded sales.
     *
     * @param order the order that was cancelled
     */
    public void showOrderCancelled(Order order) {
        printIndent("Order " + order.getId() + " for " + order.getCustomer() + " cancelled.");
        printIndent("Stock restored:");
        printOrderTable(order, "Restored");
        printIndent("Amount excluded from recorded sales: " + formatMoney(order.getTotal()));
    }

    /**
     * Shows the confirmation for an edited order: its customer, units, total and status.
     *
     * @param order the order that was just edited
     */
    public void showOrderUpdated(Order order) {
        printIndent("Order " + order.getId() + " updated.");
        printIndent("Customer: " + order.getCustomer());
        printIndent("Units: " + order.getTotalUnits());
        printIndent("Total: " + formatMoney(order.getTotal()));
        printIndent("Status: " + (order.isCancelled() ? "Cancelled" : "Active"));
    }

    /**
     * Shows the confirmation for a new, replaced or removed discount, with the amounts it produces.
     *
     * @param order the order whose discount was just set
     */
    public void showDiscountUpdated(Order order) {
        if (order.hasDiscount()) {
            printIndent("Discount updated for order " + order.getId() + ".");
        } else {
            printIndent("Discount removed from order " + order.getId() + ".");
        }
        printIndent("Subtotal: " + formatMoney(order.getSubtotal()));
        printIndent(discountLine(order));
        printIndent("Total: " + formatMoney(order.getTotal()));
    }

    /**
     * Shows every order as a table: number, customer, units bought, total and status.
     *
     * @param orders the orders to display, oldest first
     */
    public void showOrders(List<Order> orders) {
        printIndent("Orders (" + orders.size() + "):");
        if (orders.isEmpty()) {
            printIndent("Nothing here yet. Create one with: order add c/CUSTOMER p/PRODUCT q/QUANTITY");
            return;
        }

        List<String[]> rows = new ArrayList<>();
        for (Order order : orders) {
            int units = 0;
            for (OrderItem item : order.getItems()) {
                units += item.getQuantity();
            }
            rows.add(new String[] {String.valueOf(order.getId()), order.getCustomer(), String.valueOf(units),
                formatMoney(order.getTotal()), order.isCancelled() ? "Cancelled" : "Active"});
        }
        String[] headers = {"No.", "Customer", "Units", "Total", "Status"};
        boolean[] isLeftAligned = {false, true, false, false, true};
        printTable(headers, rows, null, isLeftAligned);
    }

    /**
     * Shows confirmed sales and the total amount recorded from them.
     *
     * Cancelled orders are filtered by {@link OrderList} before this method is called, so every
     * row shown contributes to the total. Row numbers reflect display positions, not order IDs.
     *
     * @param sales the confirmed sales to display, oldest first
     */
    public void showSales(List<Order> sales) {
        printIndent("Sales (" + sales.size() + "):");
        if (sales.isEmpty()) {
            printIndent("Nothing here yet. Confirm an order with: order add c/CUSTOMER p/PRODUCT q/QUANTITY");
            printIndent("Total sales: $0.00");
            return;
        }

        List<String[]> rows = new ArrayList<>();
        BigDecimal totalSales = BigDecimal.ZERO;
        for (Order sale : sales) {
            int units = 0;
            for (OrderItem item : sale.getItems()) {
                units += item.getQuantity();
            }
            totalSales = totalSales.add(sale.getTotal());
            rows.add(new String[] {String.valueOf(rows.size() + 1), sale.getCustomer(), String.valueOf(units),
                formatMoney(sale.getTotal())});
        }
        String[] headers = {"No.", "Customer", "Units", "Subtotal"};
        String[] totalRow = {"Total", "", "", formatMoney(totalSales)};
        boolean[] isLeftAligned = {false, true, false, false};
        printTable(headers, rows, totalRow, isLeftAligned);
        printIndent("Total sales: " + formatMoney(totalSales));
    }

    /**
     * Shows the total order value, payments received, total expenses and overall profit or loss.
     *
     * @param orders   all recorded orders, including unpaid and cancelled orders
     * @param sales    orders with recorded payments, excluding cancelled orders
     * @param expenses recorded operating expenses
     */
    public void showFinances(List<Order> orders, List<Order> sales, List<Expense> expenses) {
        BigDecimal totalOrders = BigDecimal.ZERO;
        for (Order order : orders) {
            totalOrders = totalOrders.add(order.getTotal());
        }

        BigDecimal totalSales = BigDecimal.ZERO;
        for (Order sale : sales) {
            totalSales = totalSales.add(sale.getPaidAmount());
        }

        BigDecimal totalExpenses = BigDecimal.ZERO;
        for (Expense expense : expenses) {
            totalExpenses = totalExpenses.add(expense.getAmount());
        }

        BigDecimal difference = totalSales.subtract(totalExpenses);
        String result = difference.signum() >= 0 ? "Profit" : "Loss";
        printIndent("Total Orders (including unpaid): " + formatMoney(totalOrders));
        printIndent("Total Sales: " + formatMoney(totalSales));
        printIndent("Total Expenses: " + formatMoney(totalExpenses));
        printIndent("______________________________");
        printIndent("Overall " + result + ": " + formatMoney(difference.abs()));
    }

    /**
     * Prints an order's lines as a table with Product, quantity, Unit price and Subtotal columns,
     * followed by a total row.
     *
     * @param order          the order to print
     * @param quantityHeader the heading of the quantity column, e.g. "Qty" or "Restored"
     */
    private void printOrderTable(Order order, String quantityHeader) {
        List<String[]> rows = new ArrayList<>();
        int totalUnits = 0;
        for (OrderItem item : order.getItems()) {
            rows.add(new String[] {item.getProduct().getName(), String.valueOf(item.getQuantity()),
                formatMoney(item.getUnitPrice()), formatMoney(item.getSubtotal())});
            totalUnits += item.getQuantity();
        }
        String[] headers = {"Product", quantityHeader, "Unit price", "Subtotal"};
        String footerLabel = order.hasDiscount() ? "Subtotal" : "Total";
        String[] totalRow = {footerLabel, String.valueOf(totalUnits), "", formatMoney(order.getSubtotal())};
        boolean[] isLeftAligned = {true, false, false, false};
        printTable(headers, rows, totalRow, isLeftAligned);
        if (order.hasDiscount()) {
            printIndent(discountLine(order));
            printIndent("Total: " + formatMoney(order.getTotal()));
        }
    }

    /**
     * Returns the discount summary line, e.g. "Discount: 12.5% ($1.18)".
     */
    private static String discountLine(Order order) {
        // stripTrailingZeros turns 10.00 into 1E+1; toPlainString then prints it as "10".
        String percent = order.getDiscountPercent().stripTrailingZeros().toPlainString();
        return "Discount: " + percent + "% (" + formatMoney(order.getDiscountAmount()) + ")";
    }

    /**
     * Prints a bordered table.
     *
     * Column widths are worked out from the longest value in each column, so long names or large
     * amounts widen the table instead of pushing columns out of line. Numbers are right-aligned so
     * that the decimal points of amounts line up.
     *
     * @param headers       the column headings
     * @param rows          the body rows, each with one cell per column
     * @param footer        a final row set off by a border, such as a total, or null for none
     * @param isLeftAligned for each column, true to left-align it (text), false to right-align (numbers)
     */
    private void printTable(String[] headers, List<String[]> rows, String[] footer, boolean[] isLeftAligned) {
        int[] widths = new int[headers.length];
        for (int column = 0; column < headers.length; column++) {
            widths[column] = headers[column].length();
            for (String[] row : rows) {
                widths[column] = Math.max(widths[column], row[column].length());
            }
            if (footer != null) {
                widths[column] = Math.max(widths[column], footer[column].length());
            }
        }

        String border = tableBorder(widths);
        printIndent(border);
        printIndent(tableRow(headers, widths, isLeftAligned));
        printIndent(border);
        for (String[] row : rows) {
            printIndent(tableRow(row, widths, isLeftAligned));
        }
        printIndent(border);
        if (footer != null) {
            printIndent(tableRow(footer, widths, isLeftAligned));
            printIndent(border);
        }
    }

    /**
     * Returns a border line such as "+---------+-----+", sized to the given column widths.
     */
    private static String tableBorder(int[] widths) {
        StringBuilder border = new StringBuilder("+");
        for (int width : widths) {
            border.append("-".repeat(width + 2)).append("+");
        }
        return border.toString();
    }

    /**
     * Returns one table row, padding each cell to its column width on the side given by its alignment.
     */
    private static String tableRow(String[] cells, int[] widths, boolean[] isLeftAligned) {
        StringBuilder row = new StringBuilder("|");
        for (int column = 0; column < cells.length; column++) {
            String alignment = isLeftAligned[column] ? "-" : "";
            row.append(" ").append(String.format("%" + alignment + widths[column] + "s", cells[column])).append(" |");
        }
        return row.toString();
    }

    /**
     * Shows a separate receipt for each eligible order using the existing order table layout.
     *
     * @param customer the requested customer name
     * @param receipts confirmed, non-cancelled orders for this customer, oldest first
     */
    public void showReceipts(String customer, List<Order> receipts) {
        printIndent("Receipts for " + customer + " (" + receipts.size() + "):");
        if (receipts.isEmpty()) {
            printIndent("No paid, non-cancelled orders found for this customer.");
            return;
        }
        for (Order order : receipts) {
            printIndent("Receipt for order " + order.getId() + " - " + order.getCustomer());
            printOrderTable(order, "Qty");
        }
    }

    /**
     * Displays the payment amount and change to return to the customer.
     *
     * @param customer customer who paid
     * @param amount amount received, in dollars
     * @param change change to return, in dollars
     */
    public void showPayment(String customer, BigDecimal amount, BigDecimal change) {
        printIndent(customer + " has paid $" + amount.setScale(2).toPlainString()
                + "! The change to return is: $" + change.setScale(2).toPlainString() + ".");
    }

    /**
     * Displays the customer's orders with individual totals and cancellation status.
     *
     * @param customer requested customer name
     * @param orders matching orders in creation order
     */
    public void showOrderDetails(String customer, List<Order> orders) {
        if (orders.isEmpty()) {
            printIndent("No orders found for " + customer + ".");
            return;
        }
        printIndent("Here are " + customer + "'s order details:");
        for (Order order : orders) {
            String status = order.isCancelled() ? "Cancelled" : "Active";
            printIndent("Order " + order.getId() + " (" + status + "):");
            List<String[]> rows = new ArrayList<>();
            long totalUnits = 0;
            for (OrderItem item : order.getItems()) {
                rows.add(new String[] {item.getProduct().getName(), String.valueOf(item.getQuantity()),
                    formatMoney(item.getSubtotal())});
                totalUnits += item.getQuantity();
            }
            String[] headers = {"Product", "Qty", "Cost"};
            String unitLabel = totalUnits == 1 ? " item" : " items";
            String[] totalRow = {"Total", totalUnits + unitLabel, formatMoney(order.getTotal())};
            boolean[] isLeftAligned = {true, false, false};
            printTable(headers, rows, totalRow, isLeftAligned);
        }
    }

    /**
     * Displays the available help areas and command format.
     *
     * @param usage format and list of supported areas
     */
    public void showHelpOverview(String usage) {
        printIndent(usage);
        printIndent("Example: help stock");
        printIndent("Use help general for help and exit commands.");
    }

    /**
     * Displays numbered descriptions followed by each command's format.
     *
     * @param area display name of the selected area
     * @param commands description and syntax pairs
     */
    public void showHelp(String area, List<String[]> commands) {
        printIndent(area + " commands:");
        for (int i = 0; i < commands.size(); i++) {
            String[] command = commands.get(i);
            printIndent((i + 1) + ". " + command[0]);
            printIndent("   " + command[1]);
        }
    }

    /**
     * Shows an error message. A message may span several lines; each line is indented like other output,
     * and only the first carries the "Sorry!" lead-in.
     *
     * @param message the message to show, with lines separated by {@code \n}
     */
    public void showError(String message) {
        String[] lines = message.split("\n");
        printIndent("Sorry! " + lines[0]);
        for (int i = 1; i < lines.length; i++) {
            printIndent(lines[i]);
        }
    }

    /**
     * Returns an amount in dollars with two decimals, e.g. "$6.38".
     */
    private static String formatMoney(BigDecimal amount) {
        return "$" + amount.setScale(2).toPlainString();
    }
}
