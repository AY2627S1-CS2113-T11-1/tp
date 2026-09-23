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
        String banner = " ____        _        \n"
                + "|  _ \\ _   _| | _____ \n"
                + "| | | | | | | |/ / _ \\\n"
                + "| |_| | |_| |   <  __/\n"
                + "|____/ \\__,_|_|\\_\\___|\n";
        System.out.println(banner);

        //@@author BA-reused
        //Reused from /ip/src/main/java/bryte/Bryte.java
        //with minor modifications
        Scanner scanner = new Scanner(System.in);
        boolean isRunning = true;
        
        System.out.println("Hello from Duke!");
        System.out.println("What can I do for you?");
      
        Ui ui = new Ui();
        Parser parser = new Parser();
        ProductList products = new ProductList();

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
                    System.out.println("Invalid stock command.");
                    continue;
                }
                String[] stockParts = commandParts[1].trim().split(" ", 2);
                String action = stockParts[0].toLowerCase();
                String details = stockParts.length > 1 ? stockParts[1].trim() : "";

                switch (action) {
                case "add":
                    handleAddStock(details);
                    break;
                case "remove":
                    handleRemoveStock(details);
                    break;
                case "list":
                    handleListStock();
                    break;
                default:
                    System.out.println("Unknown stock action.");
                }
            } else if (keyword.equals("bye")) {
                isRunning = false;
            } else {
                try {
                Command command = parser.parse(input);
                command.execute(products, ui);
                isExit = command.isExit();
              } catch (SystemException e) {
                // A mistake in one command should never end the session.
                ui.showError(e.getMessage());
              }
            }
        }
        scanner.close();
        System.out.println("Bye. Hope to see you again soon!");
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

    private static void handleAddStock(String details) {
        String product = parseField(details, "p/");
        String amountStr = parseField(details, "a/");
        String qtyStr = parseField(details, "q/");

        if (product == null || amountStr == null || qtyStr == null) {
            System.out.println("Invalid format. Use: stock add p/PRODUCT a/AMOUNT q/QUANTITY");
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
                existing.quantity += quantity;
                existing.price = price;
                System.out.println("Updated stock for " + product + ". New quantity: " + existing.quantity);
            } else {
                stockList.add(new Stock(product, price, quantity));
                String formattedPrice = (price == (long) price)
                        ? String.format("%d", (long) price)
                        : String.format("%.2f", price);
                System.out.println("Creates stock for a new product " + product
                        + " with quantity " + quantity + " and cost $" + formattedPrice + " each.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Amount and quantity must be numbers.");
        }
    }

    private static void handleRemoveStock(String details) {
        String product = parseField(details, "p/");
        String qtyStr = parseField(details, "q/");

        if (product == null) {
            System.out.println("Invalid format. Use: stock remove p/PRODUCT [q/QUANTITY]");
            return;
        }

        int quantityToRemove = 1;
        boolean defaultBehavior = true;
        
        if (qtyStr != null) {
            try {
                quantityToRemove = Integer.parseInt(qtyStr);
                defaultBehavior = false;
            } catch (NumberFormatException e) {
                System.out.println("Quantity must be a number.");
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
            System.out.println("Product not found in stock.");
            return;
        }

        existing.quantity -= quantityToRemove;
        if (existing.quantity < 0) {
            existing.quantity = 0;
        }
        
        if (defaultBehavior) {
            System.out.println("Removes 1 unit (default behaviour) of " + product);
        } else {
            String unitLabel = quantityToRemove == 1 ? " unit of " : " units of ";
            System.out.println("Removes " + quantityToRemove + unitLabel + product);
        }
    }

    private static void handleListStock() {
        System.out.println("+----------------+----------+");
        System.out.println("| Product        |      Qty |");
        System.out.println("+----------------+----------+");
        for (Stock stock : stockList) {
            System.out.printf("| %-14s | %8d |\n", stock.product, stock.quantity);
        }
        System.out.println("+----------------+----------+");
    }
}
