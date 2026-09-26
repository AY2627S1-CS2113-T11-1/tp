package seedu.duke;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.duke.command.AddOrderCommand;
import seedu.duke.command.AddProductCommand;
import seedu.duke.command.CancelOrderCommand;
import seedu.duke.command.Command;
import seedu.duke.command.ExitCommand;
import seedu.duke.command.ListOrderCommand;
import seedu.duke.command.ListProductCommand;
import seedu.duke.command.ListSalesCommand;

/**
 * Turns a line of user input into the {@link Command} it describes.
 *
 * All of the string handling lives here, so commands only ever see validated values.
 */
public class Parser {
    /**
     * Every prefix the system understands in a product command. Used to find where one value ends
     * and the next begins, so that a value may itself contain spaces.
     */
    private static final String[] PRODUCT_PREFIXES = {"p/", "a/"};

    private static final String NAME_PREFIX = "p/";
    private static final String PRICE_PREFIX = "a/";
    private static final String ADD_FORMAT = "Format: product add p/PRODUCT a/AMOUNT";
    private static final int MAX_DECIMAL_PLACES = 2;

    private static final String CUSTOMER_PREFIX = "c/";
    private static final String QUANTITY_PREFIX = "q/";
    private static final String ORDER_ADD_FORMAT =
            "Format: order add c/CUSTOMER p/PRODUCT q/QUANTITY [p/PRODUCT q/QUANTITY]...";
    private static final String ORDER_CANCEL_FORMAT = "Format: order cancel ORDER_NUMBER";

    /**
     * Matches a c/, p/ or q/ prefix at the start of the arguments or after a space. Requiring the
     * space means a product name such as "Salt and Pepper Mix 1c/2" is not split in the middle.
     */
    private static final Pattern ORDER_PREFIX_PATTERN = Pattern.compile("(?<=^|\\s)([cpq]/)");

    /**
     * Returns the command described by a line of user input.
     *
     * @param fullCommand the raw line the user typed
     * @throws SystemException if the line is empty, names an unknown command, or has bad arguments
     */
    public Command parse(String fullCommand) throws SystemException {
        String input = fullCommand.trim();
        if (input.isEmpty()) {
            throw new SystemException("Please type a command. Try: product list");
        }

        // Split into noun, verb, and the rest, so that arguments keep their internal spacing.
        String[] words = input.split("\\s+", 3);
        String noun = words[0].toLowerCase();

        if (noun.equals("product")) {
            return parseProductCommand(words);
        }
        if (noun.equals("order")) {
            return parseOrderCommand(words);
        }
        if (noun.equals("sales")) {
            return parseSalesCommand(words);
        }
        if (noun.equals("bye")) {
            return new ExitCommand();
        }
        throw new SystemException("I don't recognise \"" + words[0] + "\". Known commands: product, order, bye");
    }

    /**
     * Returns the product command described by the already split input words.
     *
     * @param words the input split into noun, verb, and arguments
     * @throws SystemException if the verb is missing or unknown, or its arguments are invalid
     */
    private Command parseProductCommand(String[] words) throws SystemException {
        if (words.length < 2) {
            throw new SystemException("What should I do with products? Try: product add, or product list");
        }

        String verb = words[1].toLowerCase();
        String arguments = words.length < 3 ? "" : words[2].trim();

        if (verb.equals("add")) {
            return parseAddProduct(arguments);
        }
        if (verb.equals("list")) {
            if (!arguments.isEmpty()) {
                throw new SystemException("\"product list\" takes no extra input, but I found: " + arguments);
            }
            return new ListProductCommand();
        }
        throw new SystemException("I don't know how to \"product " + words[1] + "\". Try: add, or list");
    }

    /**
     * Returns the order command described by the already split input words.
     *
     * @param words the input split into noun, verb, and arguments
     * @throws SystemException if the verb is missing or unknown, or its arguments are invalid
     */
    private Command parseOrderCommand(String[] words) throws SystemException {
        if (words.length < 2) {
            throw new SystemException("What should I do with orders? Try: order add, order cancel, or order list");
        }

        String verb = words[1].toLowerCase();
        String arguments = words.length < 3 ? "" : words[2].trim();

        if (verb.equals("add")) {
            return parseAddOrder(arguments);
        }
        if (verb.equals("cancel")) {
            return parseCancelOrder(arguments);
        }
        if (verb.equals("list")) {
            if (!arguments.isEmpty()) {
                throw new SystemException("\"order list\" takes no extra input, but I found: " + arguments);
            }
            return new ListOrderCommand();
        }
        throw new SystemException("I don't know how to \"order " + words[1] + "\". Try: add, cancel, or list");
    }

    /**
     * Returns the sales command described by the already split input words.
     *
     * @param words the input split into noun, verb, and arguments
     * @throws SystemException if the verb is missing, unknown, or has extra arguments
     */
    private Command parseSalesCommand(String[] words) throws SystemException {
        if (words.length < 2) {
            throw new SystemException("What should I do with sales? Try: sales list");
        }

        String verb = words[1].toLowerCase();
        String arguments = words.length < 3 ? "" : words[2].trim();
        if (verb.equals("list")) {
            if (!arguments.isEmpty()) {
                throw new SystemException("\"sales list\" takes no extra input, but I found: " + arguments);
            }
            return new ListSalesCommand();
        }
        throw new SystemException("I don't know how to \"sales " + words[1] + "\". Try: list");
    }

    /**
     * Returns an add-order command built from the arguments of {@code order add}.
     *
     * Unlike {@code product add}, p/ and q/ may appear many times here, and each q/ belongs to the
     * p/ just before it. So instead of looking each prefix up once, the arguments are cut into
     * (prefix, value) pairs in the order they were typed, and the pairs are then checked in sequence:
     * one c/ first, followed by one or more p/ q/ pairs.
     *
     * @param arguments the text after "order add", e.g. "c/Jonas Low p/Apple q/3 p/Carrot q/1"
     * @throws SystemException if the arguments do not follow the format, or a value is invalid
     */
    private Command parseAddOrder(String arguments) throws SystemException {
        List<String[]> pairs = splitIntoPrefixValuePairs(arguments);

        if (pairs.isEmpty() || !pairs.get(0)[0].equals(CUSTOMER_PREFIX)) {
            throw new SystemException("Start the order with c/CUSTOMER. " + ORDER_ADD_FORMAT);
        }
        String customer = pairs.get(0)[1];
        if (customer.isEmpty()) {
            throw new SystemException("The customer name cannot be empty. " + ORDER_ADD_FORMAT);
        }

        List<String[]> itemPairs = pairs.subList(1, pairs.size());
        if (itemPairs.isEmpty()) {
            throw new SystemException("An order needs at least one p/PRODUCT q/QUANTITY pair. " + ORDER_ADD_FORMAT);
        }
        if (itemPairs.size() % 2 != 0) {
            throw new SystemException("Every p/PRODUCT needs a q/QUANTITY right after it. " + ORDER_ADD_FORMAT);
        }

        List<AddOrderCommand.RequestedItem> items = new ArrayList<>();
        Set<String> seenNames = new HashSet<>();
        for (int i = 0; i < itemPairs.size(); i += 2) {
            String[] productPair = itemPairs.get(i);
            String[] quantityPair = itemPairs.get(i + 1);
            if (!productPair[0].equals(NAME_PREFIX) || !quantityPair[0].equals(QUANTITY_PREFIX)) {
                throw new SystemException("After c/CUSTOMER, give p/PRODUCT q/QUANTITY pairs in that order. "
                        + ORDER_ADD_FORMAT);
            }

            String productName = productPair[1];
            if (productName.isEmpty()) {
                throw new SystemException("A product name cannot be empty. " + ORDER_ADD_FORMAT);
            }
            // Naming a product twice would let each line pass the stock check on its own while
            // together they ask for more than is in stock, so it is rejected outright.
            if (!seenNames.add(productName.toLowerCase())) {
                throw new SystemException("\"" + productName + "\" appears more than once in this order. "
                        + "Put the full quantity in a single q/ instead.");
            }
            items.add(new AddOrderCommand.RequestedItem(productName, parseQuantity(quantityPair[1])));
        }
        return new AddOrderCommand(customer, items);
    }

    /**
     * Cuts order arguments into (prefix, value) pairs, in the order they were typed.
     *
     * For example, "c/Jonas p/Apple q/3" becomes [c/, Jonas], [p/, Apple], [q/, 3].
     *
     * @param arguments the argument text to split
     * @throws SystemException if there is text before the first prefix
     */
    private List<String[]> splitIntoPrefixValuePairs(String arguments) throws SystemException {
        List<String[]> pairs = new ArrayList<>();
        Matcher matcher = ORDER_PREFIX_PATTERN.matcher(arguments);

        String currentPrefix = null;
        int valueStart = 0;
        while (matcher.find()) {
            if (currentPrefix == null) {
                String leadingText = arguments.substring(0, matcher.start()).trim();
                if (!leadingText.isEmpty()) {
                    throw new SystemException("I don't understand \"" + leadingText + "\". " + ORDER_ADD_FORMAT);
                }
            } else {
                pairs.add(new String[] {currentPrefix, arguments.substring(valueStart, matcher.start()).trim()});
            }
            currentPrefix = matcher.group(1);
            valueStart = matcher.end();
        }
        if (currentPrefix != null) {
            pairs.add(new String[] {currentPrefix, arguments.substring(valueStart).trim()});
        }
        return pairs;
    }

    /**
     * Returns the quantity described by the text after q/.
     *
     * @param quantityText the text after the q/ prefix, e.g. "3"
     * @throws SystemException if the text is not a whole number of at least 1
     */
    private int parseQuantity(String quantityText) throws SystemException {
        int quantity;
        try {
            quantity = Integer.parseInt(quantityText);
        } catch (NumberFormatException e) {
            throw new SystemException("A quantity must be a whole number such as 3, but I got: \""
                    + quantityText + "\"");
        }
        if (quantity < 1) {
            throw new SystemException("A quantity must be at least 1, but I got: " + quantityText);
        }
        return quantity;
    }

    /**
     * Returns a cancel-order command built from the arguments of {@code order cancel}.
     *
     * @param arguments the text after "order cancel", e.g. "1"
     * @throws SystemException if the arguments are not a single positive whole number
     */
    private Command parseCancelOrder(String arguments) throws SystemException {
        if (arguments.isEmpty()) {
            throw new SystemException("Which order should I cancel? " + ORDER_CANCEL_FORMAT);
        }
        int orderId;
        try {
            orderId = Integer.parseInt(arguments);
        } catch (NumberFormatException e) {
            throw new SystemException("The order number must be a whole number such as 1, but I got: \""
                    + arguments + "\". " + ORDER_CANCEL_FORMAT);
        }
        if (orderId < 1) {
            throw new SystemException("Order numbers start at 1, but I got: " + arguments);
        }
        return new CancelOrderCommand(orderId);
    }

    /**
     * Returns an add command built from the arguments of {@code product add}.
     *
     * @param arguments the text after "product add", e.g. "p/Oat Milk a/3.50"
     * @throws SystemException if a prefix is missing, the name is empty, or the price is invalid
     */
    private Command parseAddProduct(String arguments) throws SystemException {
        String name = extractValue(arguments, NAME_PREFIX);
        String priceText = extractValue(arguments, PRICE_PREFIX);

        if (name.isEmpty()) {
            throw new SystemException("The product name cannot be empty. " + ADD_FORMAT);
        }
        return new AddProductCommand(name, parsePrice(priceText));
    }

    /**
     * Returns the value that follows the given prefix, up to the next prefix or the end of input.
     *
     * Scanning for prefixes rather than splitting on spaces is what lets a product name such as
     * "Oat Milk" stay in one piece, and lets the user type p/ and a/ in either order.
     *
     * @param arguments the argument text to search
     * @param prefix    the prefix whose value is wanted, e.g. "p/"
     * @throws SystemException if the prefix does not appear in the arguments
     */
    private String extractValue(String arguments, String prefix) throws SystemException {
        int prefixIndex = arguments.indexOf(prefix);
        if (prefixIndex < 0) {
            throw new SystemException("Missing " + prefix + " in your command. " + ADD_FORMAT);
        }

        int valueStart = prefixIndex + prefix.length();
        int valueEnd = arguments.length();
        for (String otherPrefix : PRODUCT_PREFIXES) {
            if (otherPrefix.equals(prefix)) {
                continue;
            }
            int otherIndex = arguments.indexOf(otherPrefix, valueStart);
            if (otherIndex >= 0 && otherIndex < valueEnd) {
                valueEnd = otherIndex;
            }
        }
        return arguments.substring(valueStart, valueEnd).trim();
    }

    /**
     * Returns the price described by the text after a/, in dollars.
     *
     * @param priceText the text after the a/ prefix, e.g. "3.50"
     * @throws SystemException if the text is not a number, is negative, or has too many decimals
     */
    private double parsePrice(String priceText) throws SystemException {
        if (priceText.isEmpty()) {
            throw new SystemException("The price cannot be empty. " + ADD_FORMAT);
        }

        int pointIndex = priceText.indexOf('.');
        if (pointIndex >= 0 && priceText.length() - pointIndex - 1 > MAX_DECIMAL_PLACES) {
            throw new SystemException("A price can have at most 2 decimal places, but I got: " + priceText);
        }

        double price;
        try {
            price = Double.parseDouble(priceText);
        } catch (NumberFormatException e) {
            throw new SystemException("The price must be a number such as 3.50, but I got: " + priceText);
        }

        if (Double.isNaN(price) || Double.isInfinite(price)) {
            throw new SystemException("The price must be an ordinary number such as 3.50, but I got: " + priceText);
        }
        if (price < 0) {
            throw new SystemException("The price cannot be negative, but I got: " + priceText);
        }
        return price;
    }
}
