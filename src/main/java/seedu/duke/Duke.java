package seedu.duke;

import java.util.ArrayList;
import java.util.Scanner;
import seedu.duke.command.Command;


/**
 * Entry point of the system.
 *
 * Runs the read-parse-execute loop: a line is read from the user, turned into a
 * {@link Command} by the {@link Parser}, and executed against the {@link ProductList}.
 */
public class Duke {
    private static ArrayList<Stock> stockList = new ArrayList<>();

    /**
     * Starts the system and runs until the user exits or input ends.
     *
     * @param args command line arguments, which the system does not use
     */
    public static void main(String[] args) {
        //@@author BA-reused
        //Reused from /ip/src/main/java/bryte/Bryte.java
        //with minor modifications
        Scanner scanner = new Scanner(System.in);
        boolean isRunning = true;

        Ui ui = new Ui();
        ui.printDivider();
        ui.showWelcome();
        ui.printDivider();
        Parser parser = new Parser();
        ProductList products = new ProductList();
        OrderList orders = new OrderList();

        while (isRunning && scanner.hasNextLine()) {
            String command = scanner.nextLine().trim();
            if (command.isEmpty()) {
                continue;
            }
            String[] commandParts = command.split(" ", 2);
            String keyword = commandParts[0].toLowerCase();
            //@@author

            if (keyword.equals("stock")) {
                if (commandParts.length < 2) {
                    ui.printDivider();
                    System.out.println("    Invalid stock command.");
                    ui.printDivider();
                    System.out.println();
                    continue;
                }
                String[] stockParts = commandParts[1].trim().split(" ", 2);
                String action = stockParts[0].toLowerCase();
                String details = stockParts.length > 1 ? stockParts[1].trim() : "";

                switch (action) {
                case "add":
                    ui.printDivider();
                    handleAddStock(details, products);
                    ui.printDivider();
                    System.out.println();
                    break;
                case "remove":
                    ui.printDivider();
                    handleRemoveStock(details, products);
                    ui.printDivider();
                    System.out.println();
                    break;
                case "list":
                    ui.printDivider();
                    handleListStock();
                    ui.printDivider();
                    System.out.println();
                    break;
                default:
                    ui.printDivider();
                    System.out.println("    Unknown stock action.");
                    ui.printDivider();
                    System.out.println();
                }
            } else {
                ui.printDivider();
                try {
                    Command parsedCommand = parser.parse(command);
                    parsedCommand.execute(products, orders, ui);
                    isRunning = !parsedCommand.isExit();
                } catch (SystemException e) {
                    // A mistake in one command should never end the session.
                    ui.showError(e.getMessage());
                }
                ui.printDivider();
                System.out.println();
            }
        }
        scanner.close();
    }

    private static String parseField(String input, String prefix) {
        String match = " " + prefix;
        String paddedInput = " " + input;
        int startIndex = paddedInput.indexOf(match);
        if (startIndex == -1) {
            return null;
        }
        startIndex += match.length();

        int endIndex = paddedInput.length();
        String[] otherPrefixes = {" p/", " a/", " q/"};
        for (String other : otherPrefixes) {
            if (other.equals(match)) {
                continue;
            }
            int nextIndex = paddedInput.indexOf(other, startIndex);
            if (nextIndex != -1 && nextIndex < endIndex) {
                endIndex = nextIndex;
            }
        }

        return paddedInput.substring(startIndex, endIndex).trim();
    }

    private static void handleAddStock(String details, ProductList products) {
        String product = parseField(details, "p/");
        String amountStr = parseField(details, "a/");
        String qtyStr = parseField(details, "q/");

        if (product == null || amountStr == null || qtyStr == null) {
            System.out.println("    Invalid format. Use: stock add p/PRODUCT a/AMOUNT q/QUANTITY");
            return;
        }

        try {
            double price = Double.parseDouble(amountStr);
            int quantity = Integer.parseInt(qtyStr);

            Stock existing = null;
            for (Stock s : stockList) {
                if (s.product.equalsIgnoreCase(product)) {
                    existing = s;
                    break;
                }
            }

            if (existing != null) {
                refreshFromProduct(products, existing);
                existing.quantity += quantity;
                existing.price = price;
                syncProductStock(products, existing);
                System.out.println("    Updated stock for " + product + ". New quantity: " + existing.quantity);
            } else {
                Stock created = new Stock(product, price, quantity);
                stockList.add(created);
                syncProductStock(products, created);
                String formattedPrice = (price == (long) price)
                        ? String.format("%d", (long) price)
                        : String.format("%.2f", price);
                System.out.println("    Creates stock for a new product " + product
                        + " with quantity " + quantity + " and cost $" + formattedPrice + " each.");
            }
        } catch (NumberFormatException e) {
            System.out.println("    Amount and quantity must be numbers.");
        }
    }

    private static void handleRemoveStock(String details, ProductList products) {
        String product = parseField(details, "p/");
        String qtyStr = parseField(details, "q/");

        if (product == null) {
            System.out.println("    Invalid format. Use: stock remove p/PRODUCT [q/QUANTITY]");
            return;
        }

        int quantityToRemove = 1;
        boolean defaultBehavior = true;
        
        if (qtyStr != null) {
            try {
                quantityToRemove = Integer.parseInt(qtyStr);
                defaultBehavior = false;
            } catch (NumberFormatException e) {
                System.out.println("    Quantity must be a number.");
                return;
            }
        }

        Stock existing = null;
        for (Stock s : stockList) {
            if (s.product.equalsIgnoreCase(product)) {
                existing = s;
                break;
            }
        }

        if (existing == null) {
            System.out.println("    Product not found in stock.");
            return;
        }

        refreshFromProduct(products, existing);
        existing.quantity -= quantityToRemove;
        if (existing.quantity < 0) {
            existing.quantity = 0;
        }
        syncProductStock(products, existing);
        
        if (defaultBehavior) {
            System.out.println("    Removes 1 unit (default behaviour) of " + product);
        } else {
            String unitLabel = quantityToRemove == 1 ? " unit of " : " units of ";
            System.out.println("    Removes " + quantityToRemove + unitLabel + product);
        }
    }

    /**
     * Copies a stock entry's quantity onto the catalogue product of the same name, if there is one.
     *
     * Stock quantities are kept in two places for now: the {@link Stock} list here, and
     * {@link Product#getStock()}, which the product and order commands read. This keeps the two in
     * step until stock handling moves into a proper command that updates {@link Product} directly.
     * Only the quantity is copied: the a/ amount in a stock command is the cost price, which is not
     * the product's selling price.
     *
     * @param products the catalogue to update
     * @param stock    the stock entry that just changed
     */
    private static void syncProductStock(ProductList products, Stock stock) {
        Product product = products.findByName(stock.product);
        if (product != null) {
            product.setStock(stock.quantity);
        }
    }

    /**
     * Copies the catalogue product's current quantity onto a stock entry before the entry is changed.
     *
     * Orders change {@link Product#getStock()} without touching the {@link Stock} list, so the entry
     * may be out of date. Refreshing it first means a later stock add or remove starts from the
     * real current quantity instead of overwriting the product with a stale total.
     *
     * @param products the catalogue to read from
     * @param stock    the stock entry about to be changed
     */
    private static void refreshFromProduct(ProductList products, Stock stock) {
        Product product = products.findByName(stock.product);
        if (product != null) {
            stock.quantity = product.getStock();
        }
    }

    private static void handleListStock() {
        System.out.println("    +----------------+----------+");
        System.out.println("    | Product        |      Qty |");
        System.out.println("    +----------------+----------+");
        for (Stock stock : stockList) {
            System.out.printf("    | %-14s | %8d |\n", stock.product, stock.quantity);
        }
        System.out.println("    +----------------+----------+");
    }
}
