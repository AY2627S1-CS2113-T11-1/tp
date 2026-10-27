package seedu.duke;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.duke.command.AddOrderCommand;
import seedu.duke.command.AddExpenseCommand;
import seedu.duke.command.AddProductCommand;
import seedu.duke.command.CancelOrderCommand;
import seedu.duke.command.Command;
import seedu.duke.command.ExitCommand;
import seedu.duke.command.HelpCommand;
import seedu.duke.command.ListOrderCommand;
import seedu.duke.command.ListExpenseCommand;
import seedu.duke.command.ListProductCommand;
import seedu.duke.command.ListSalesCommand;
import seedu.duke.command.ListStockCommand;
import seedu.duke.command.RemoveStockCommand;
import seedu.duke.command.AddStockCommand;
import seedu.duke.command.EditOrderCommand;
import seedu.duke.command.DiscountOrderCommand;
import seedu.duke.command.LowStockCommand;
import seedu.duke.command.FinancesCommand;

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
    private static final String ADD_FORMAT =
            "Format: product add p/PRODUCT a/AMOUNT";
    private static final String EXPENSE_ADD_FORMAT =
            "Format: expense add p/PRODUCT a/AMOUNT";
    private static final int MAX_DECIMAL_PLACES = 2;

    private static final String CUSTOMER_PREFIX = "c/";
    private static final String QUANTITY_PREFIX = "q/";
    private static final String ORDER_ADD_FORMAT =
            "Format: order add c/CUSTOMER p/PRODUCT q/QUANTITY [p/PRODUCT q/QUANTITY]...";
    private static final String ORDER_CANCEL_FORMAT =
            "Format: order cancel ORDER_NUMBER";
    private static final String ORDER_EDIT_FORMAT =
            "Format: order edit ORDER_NUMBER [c/CUSTOMER] [p/PRODUCT] [q/QUANTITY]";
    private static final String DISCOUNT_PREFIX = "d/";
    private static final String ORDER_DISCOUNT_FORMAT =
            "Format: order discount ORDER_NUMBER d/PERCENTAGE";
    private static final BigDecimal MAX_DISCOUNT_PERCENT = BigDecimal.valueOf(100);

    /**
     * A plain decimal number, optionally negative,
     * e.g. "10", "12.5" or "-3". Rejects "1e2", "+5" and "10%".
     */
    private static final Pattern DECIMAL_NUMBER_PATTERN =
            Pattern.compile("-?\\d+(\\.\\d+)?");
    private static final String STOCK_ADD_FORMAT =
            "Format: stock add p/PRODUCT q/QUANTITY";
    private static final String STOCK_REMOVE_FORMAT =
            "Format: stock remove p/PRODUCT [q/QUANTITY]";
    private static final String STOCK_LOW_FORMAT =
            "Format: stock low [q/THRESHOLD]";
    private static final String[] STOCK_PREFIXES = {"p/", "q/"};

    /**
     * Matches a c/, p/ or q/ prefix at the start of the arguments or after a space. Requiring the
     * space means a product name such as "Salt and Pepper Mix 1c/2" is not split in the middle.
     */
    private static final Pattern ORDER_PREFIX_PATTERN =
            Pattern.compile("(?<=^|\\s)([acdpq]/)");

    /**
     * Matches any prefix used anywhere in the system (a/, c/, d/, p/, q/), so that a command can reject
     * prefixes that belong to other commands instead of silently treating them as part of a name.
     */
    private static final Pattern KNOWN_PREFIX_PATTERN =
            Pattern.compile("(?<=^|\\s)([acdpq]/)");

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

        if (noun.equals("help")) {
            if (words.length > 2) {
                throw new SystemException("Choose one help area. " + HelpCommand.USAGE);
            }
            return new HelpCommand(words.length == 2 ? words[1] : "");
        }
        if (noun.equals("generate")) {
            return new ReceiptParser().parse(input);
        }
        if (noun.equals("product")) {
            return parseProductCommand(words);
        }
        if (noun.equals("expense")) {
            return parseExpenseCommand(words);
        }
        if (noun.equals("order")) {
            return parseOrderCommand(words);
        }
        if (noun.equals("stock")) {
            return parseStockCommand(words);
        }
        if (noun.equals("sales")) {
            return parseSalesCommand(words);
        }
        if (noun.equals("exit")) {
            return new ExitCommand();
        }
        throw new SystemException("I don't recognise \"" + words[0]
                + "\". Known commands: product, order, expense, stock, sales, generate, help, exit");
        if (noun.equals("finances")) {
            if (words.length > 1) {
                throw new SystemException("\"finances\" takes no extra input, but I found: "
                        + input.substring("finances".length()).trim());
            }
            return new FinancesCommand();
        }
        throw new SystemException("I don't recognise \"" + words[0]
                + "\". Known commands: product, order, expense, stock, sales, finances, generate, help, bye");
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
     * Returns the expense command described by the already split input words.
     *
     * @param words the input split into noun, verb, and arguments
     * @throws SystemException if the verb is missing or its arguments are invalid
     */
    private Command parseExpenseCommand(String[] words) throws SystemException {
        if (words.length < 2) {
            throw new SystemException("What should I do with expenses? Try: expense add, or expense list");
        }

        String verb = words[1].toLowerCase();
        String arguments = words.length < 3 ? "" : words[2].trim();

        if (verb.equals("add")) {
            return parseAddExpense(arguments);
        }
        if (verb.equals("list")) {
            if (!arguments.isEmpty()) {
                throw new SystemException("\"expense list\" takes no extra input, but I found: " + arguments);
            }
            return new ListExpenseCommand();
        }
        throw new SystemException("I don't know how to \"expense " + words[1] + "\". Try: add, or list");
    }

    /**
     * Returns the order command described by the already split input words.
     *
     * @param words the input split into noun, verb, and arguments
     * @throws SystemException if the verb is missing or unknown, or its arguments are invalid
     */
    private Command parseOrderCommand(String[] words) throws SystemException {
        if (words.length < 2) {
            throw new SystemException("What should I do with orders? "
                    + "Try: order add, order edit, order discount, order cancel, or order list");
        }

        String verb = words[1].toLowerCase();
        String arguments = words.length < 3 ? "" : words[2].trim();

        if (verb.equals("details")) {
            return new OrderDetailsParser().parse(arguments);
        }
        if (verb.equals("add")) {
            return parseAddOrder(arguments);
        }
        if (verb.equals("cancel")) {
            return parseCancelOrder(arguments);
        }
        if (verb.equals("edit")) {
            return parseEditOrder(arguments);
        }
        if (verb.equals("discount")) {
            return parseDiscountOrder(arguments);
        }
        if (verb.equals("list")) {
            if (!arguments.isEmpty()) {
                throw new SystemException("\"order list\" takes no extra input, " +
                        "but I found: " + arguments);
            }
            return new ListOrderCommand();
        }
        throw new SystemException("I don't know how to \"order " + words[1]
                + "\". Try: add, edit, discount, cancel, or list");
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

        String[] paymentParts = arguments.split("\\s+", 2);
        if (paymentParts[0].equalsIgnoreCase("pay")) {
            return new PaymentParser().parse(paymentParts.length == 2 ? paymentParts[1] : "");
        }

        List<String[]> pairs = splitToPrefix(arguments);

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

    private List<String[]> splitToPrefix(String arguments) throws SystemException {
        return splitToPrefix(arguments, ORDER_PREFIX_PATTERN, ORDER_ADD_FORMAT);
    }
    /**
     * Cuts order arguments into (prefix, value) pairs, in the order they were typed.
     *
     * For example, "c/Jonas p/Apple q/3" becomes [c/, Jonas], [p/, Apple], [q/, 3].
     *
     * @param arguments the argument text to split
     * @throws SystemException if there is text before the first prefix
     */
    private List<String[]> splitToPrefix(String arguments, Pattern prefixPattern, String formatHint)
            throws SystemException {
        List<String[]> pairs = new ArrayList<>();
        Matcher matcher = ORDER_PREFIX_PATTERN.matcher(arguments);

        String currentPrefix = null;
        int valueStart = 0;
        while (matcher.find()) {
            if (currentPrefix == null) {
                String leadingText = arguments.substring(0, matcher.start()).trim();
                if (!leadingText.isEmpty()) {
                    throw new SystemException("I don't understand \"" + leadingText + "\". " + formatHint);
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

    private Command parseCancelOrder(String arguments) throws SystemException {
        if (arguments.isEmpty()) {
            throw new SystemException("Which order should I cancel? " + ORDER_CANCEL_FORMAT);
        }
        return new CancelOrderCommand(parseOrderNumber(arguments, ORDER_CANCEL_FORMAT));
    }

    /**
     * Returns the order number described by the given text.
     *
     * @param orderNumberText the text that should hold the order number, e.g. "1"
     * @param formatHint      the usage hint to include if the text is not a valid order number
     * @throws SystemException if the text is not a whole number of at least 1
     */
    private int parseOrderNumber(String orderNumberText, String formatHint) throws SystemException {
        int orderId;
        try {
            orderId = Integer.parseInt(orderNumberText);
        } catch (NumberFormatException e) {
            throw new SystemException("The order number must be a whole number such as 1, but I got: \""
                    + orderNumberText + "\". " + formatHint);
        }
        if (orderId < 1) {
            throw new SystemException("Order numbers start at 1, but I got: " + orderNumberText);
        }
        return orderId;
    }

    /**
     * Returns an edit-order command built from the arguments of {@code order edit}.
     *
     * The order number comes first, followed by any of c/, p/ and q/ in any order, each at most once.
     * Fields that are left out stay unchanged, so they are passed to the command as null.
     *
     * @param arguments the text after "order edit", e.g. "2 c/Mei Tan q/5"
     * @throws SystemException if the order number is invalid, no field is given, a prefix is repeated
     *                         or not supported, or a value is blank or invalid
     */
    private Command parseEditOrder(String arguments) throws SystemException {
        if (arguments.isEmpty()) {
            throw new SystemException("Which order should I edit? " + ORDER_EDIT_FORMAT);
        }
        String[] parts = arguments.split("\\s+", 2);
        int orderId = parseOrderNumber(parts[0], ORDER_EDIT_FORMAT);
        String fieldText = parts.length < 2 ? "" : parts[1];
        if (fieldText.isBlank()) {
            throw new SystemException("Give at least one of c/CUSTOMER, p/PRODUCT or q/QUANTITY to change. "
                    + ORDER_EDIT_FORMAT);
        }

        String customer = null;
        String productName = null;
        Integer quantity = null;
        List<String[]> pairs = splitToPrefix(fieldText, KNOWN_PREFIX_PATTERN, ORDER_EDIT_FORMAT);
        if (pairs.isEmpty()) {
            // No recognised prefix at all, e.g. "order edit 2 x/5" or "order edit 2 Mei".
            throw new SystemException("I don't understand \"" + fieldText.trim() + "\". " + ORDER_EDIT_FORMAT);
        }

        Set<String> seenPrefixes = new HashSet<>();
        for (String[] pair : pairs) {
            String prefix = pair[0];
            String value = pair[1];
            if (!seenPrefixes.add(prefix)) {
                throw new SystemException(prefix + " appears more than once. Give each field at most once. "
                        + ORDER_EDIT_FORMAT);
            }
            switch (prefix) {
            case CUSTOMER_PREFIX:
                if (value.isEmpty()) {
                    throw new SystemException("The customer name cannot be blank. " + ORDER_EDIT_FORMAT);
                }
                customer = value;
                break;
            case NAME_PREFIX:
                if (value.isEmpty()) {
                    throw new SystemException("The product name cannot be blank. " + ORDER_EDIT_FORMAT);
                }
                productName = value;
                break;
            case QUANTITY_PREFIX:
                quantity = parseQuantity(value);
                break;
            default:
                throw new SystemException("\"order edit\" does not accept " + prefix + ". "
                        + "Use c/, p/ or q/. " + ORDER_EDIT_FORMAT);
            }
        }
        return new EditOrderCommand(orderId, customer, productName, quantity);
    }

    /**
     * Returns a discount command built from the arguments of {@code order discount}.
     *
     * Every error message ends by confirming that the order was not changed, as the User Guide promises.
     *
     * @param arguments the text after "order discount", e.g. "2 d/10"
     * @throws SystemException if the order number or percentage is missing or invalid, d/ is repeated,
     *                         or there is any other input
     */
    private Command parseDiscountOrder(String arguments) throws SystemException {
        if (arguments.isEmpty()) {
            throw discountError("Which order should I discount? " + ORDER_DISCOUNT_FORMAT);
        }
        String[] parts = arguments.split("\\s+", 2);
        int orderId;
        try {
            orderId = parseOrderNumber(parts[0], ORDER_DISCOUNT_FORMAT);
        } catch (SystemException e) {
            throw discountError(e.getMessage());
        }

        String fieldText = parts.length < 2 ? "" : parts[1];
        List<String[]> pairs;
        try {
            pairs = splitToPrefix(fieldText, KNOWN_PREFIX_PATTERN, ORDER_DISCOUNT_FORMAT);
        } catch (SystemException e) {
            throw discountError(e.getMessage());
        }
        if (pairs.isEmpty()) {
            String problem = fieldText.isBlank() ? "Missing d/PERCENTAGE. " : "I don't understand \""
                    + fieldText.trim() + "\". ";
            throw discountError(problem + ORDER_DISCOUNT_FORMAT);
        }
        for (String[] pair : pairs) {
            if (!pair[0].equals(DISCOUNT_PREFIX)) {
                throw discountError("\"order discount\" only accepts d/, but I found " + pair[0] + ". "
                        + ORDER_DISCOUNT_FORMAT);
            }
        }
        if (pairs.size() > 1) {
            throw discountError("d/ appears more than once. Give a single percentage. " + ORDER_DISCOUNT_FORMAT);
        }
        return new DiscountOrderCommand(orderId, parseDiscountPercent(pairs.get(0)[1]));
    }

    /**
     * Returns the discount percentage described by the text after d/.
     *
     * The percentage is kept as a {@link BigDecimal} so that 12.5 stays exactly 12.5 for the
     * half-cent rounding done by {@link Order}.
     *
     * @param percentText the text after the d/ prefix, e.g. "12.5"
     * @throws SystemException if the text is empty, not a plain number, outside 0 to 100,
     *                         or has more than two decimal places
     */
    private BigDecimal parseDiscountPercent(String percentText) throws SystemException {
        if (percentText.isEmpty()) {
            throw discountError("The discount percentage cannot be empty. " + ORDER_DISCOUNT_FORMAT);
        }
        if (percentText.matches("\\S+\\s+.*")) {
            throw discountError("I found extra input after the percentage: \"" + percentText + "\". "
                    + ORDER_DISCOUNT_FORMAT);
        }
        if (percentText.endsWith("%")) {
            throw discountError("Enter the percentage without the % symbol, e.g. d/10.");
        }
        if (!DECIMAL_NUMBER_PATTERN.matcher(percentText).matches()) {
            throw discountError("The discount must be a number such as 10 or 12.5, but I got: \""
                    + percentText + "\".");
        }

        BigDecimal percent = new BigDecimal(percentText);
        if (percent.signum() < 0 || percent.compareTo(MAX_DISCOUNT_PERCENT) > 0) {
            throw discountError("Discount must be between 0 and 100.");
        }
        // scale() counts the decimals as typed, so "10.500" is rejected just as "a/3.500" is for prices.
        if (percent.scale() > MAX_DECIMAL_PLACES) {
            throw discountError("A discount can have at most 2 decimal places, but I got: " + percentText);
        }
        return percent;
    }

    /**
     * Returns a discount error whose message ends by confirming that the order was not changed.
     */
    private static SystemException discountError(String reason) {
        return new SystemException(reason + "\n" + DiscountOrderCommand.NO_CHANGES_MESSAGE);
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
     * Returns an add-expense command built from the arguments of {@code expense add}.
     *
     * @param arguments the text after "expense add"
     * @throws SystemException if the product name or amount is invalid
     */
    private Command parseAddExpense(String arguments) throws SystemException {
        String productName = extractValue(arguments, NAME_PREFIX, PRODUCT_PREFIXES, EXPENSE_ADD_FORMAT);
        String amountText = extractValue(arguments, PRICE_PREFIX, PRODUCT_PREFIXES, EXPENSE_ADD_FORMAT);

        if (productName.isEmpty()) {
            throw new SystemException("The product name cannot be empty. " + EXPENSE_ADD_FORMAT);
        }
        return new AddExpenseCommand(productName, parsePrice(amountText));
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
        return extractValue(arguments, prefix, PRODUCT_PREFIXES, ADD_FORMAT);
    }

    /**
     * Returns the value that follows the given prefix, stopping at any prefix in {@code otherPrefixes}
     * or the end of input.
     *
     * @param arguments     the argument text to search
     * @param prefix        the prefix whose value is wanted, e.g. "p/"
     * @param otherPrefixes array of all valid prefixes for this command
     * @param formatHint    the usage hint to include if the prefix is missing
     * @throws SystemException if the prefix does not appear in the arguments
     */
    private String extractValue(String arguments, String prefix, String[] otherPrefixes, String formatHint)
            throws SystemException {
        int prefixIndex = arguments.indexOf(prefix);
        if (prefixIndex < 0) {
            throw new SystemException("Missing " + prefix + " in your command. " + formatHint);
        }

        int valueStart = prefixIndex + prefix.length();
        int valueEnd = arguments.length();
        for (String otherPrefix : otherPrefixes) {
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
    private BigDecimal parsePrice(String priceText) throws SystemException {
        if (priceText.isEmpty()) {
            throw new SystemException("The price cannot be empty. " + ADD_FORMAT);
        }

        int pointIndex = priceText.indexOf('.');
        if (pointIndex >= 0 && priceText.length() - pointIndex - 1 > MAX_DECIMAL_PLACES) {
            throw new SystemException("A price can have at most 2 decimal places, but I got: " + priceText);
        }

        BigDecimal price;
        try {
            price = new BigDecimal(priceText);
        } catch (NumberFormatException e) {
            throw new SystemException("The price must be a number such as 3.50, but I got: " + priceText);
        }

        if (price.signum() < 0) {
            throw new SystemException("The price cannot be negative, but I got: " + priceText);
        }
        return price.setScale(MAX_DECIMAL_PLACES);
    }

    private Command parseStockCommand(String[] words) throws SystemException {
        if (words.length < 2) {
            throw new SystemException("What should I do with stock? Try: stock add, stock remove, stock list,"
                    + " or stock low");
        }

        String verb = words[1].toLowerCase();
        String arguments = words.length < 3 ? "" : words[2].trim();

        if (verb.equals("add")) {
            return parseAddStock(arguments);
        }
        if (verb.equals("remove")) {
            return parseRemoveStock(arguments);
        }
        if (verb.equals("list")) {
            if (!arguments.isEmpty()) {
                throw new SystemException("\"stock list\" takes no extra input, but I found: " + arguments);
            }
            return new ListStockCommand();
        }
        if (verb.equals("low")) {
            return parseLowStock(arguments);
        }
        throw new SystemException("I don't know how to \"stock " + words[1] + "\". Try: add, remove, list, or low");
    }

    private Command parseLowStock(String arguments) throws SystemException {
        arguments = arguments.trim();
        if (arguments.isEmpty()) {
            return new LowStockCommand();
        }

        String thresholdText;
        if (arguments.startsWith(QUANTITY_PREFIX)) {
            thresholdText = arguments.substring(QUANTITY_PREFIX.length()).trim();
        } else {
            thresholdText = arguments;
        }

        int threshold = parseThreshold(thresholdText);
        return new LowStockCommand(threshold);
    }

    private int parseThreshold(String thresholdText) throws SystemException {
        if (thresholdText.isEmpty()) {
            throw new SystemException("The threshold cannot be empty. " + STOCK_LOW_FORMAT);
        }
        int threshold;
        try {
            threshold = Integer.parseInt(thresholdText);
        } catch (NumberFormatException e) {
            throw new SystemException("A threshold must be a whole number such as 5, but I got: \""
                    + thresholdText + "\"");
        }
        if (threshold < 0) {
            throw new SystemException("A threshold must be at least 0, but I got: " + thresholdText);
        }
        return threshold;
    }

    private Command parseAddStock(String arguments) throws SystemException {
        String productName = extractValue(arguments, NAME_PREFIX, STOCK_PREFIXES, STOCK_ADD_FORMAT);
        String quantityText = extractValue(arguments, QUANTITY_PREFIX, STOCK_PREFIXES, STOCK_ADD_FORMAT);

        if (productName.isEmpty()) {
            throw new SystemException("The product name cannot be empty. "
                    + "Format: stock add p/PRODUCT q/QUANTITY");
        }

        int quantity = parseQuantity(quantityText);
        return new AddStockCommand(productName, quantity);
    }

    private Command parseRemoveStock(String arguments) throws SystemException {
        String productName = extractValue(arguments, NAME_PREFIX, STOCK_PREFIXES, STOCK_REMOVE_FORMAT);
        if (productName.isEmpty()) {
            throw new SystemException("The product name cannot be empty. "
                    + "Format: stock remove p/PRODUCT [q/QUANTITY]");
        }

        int qtyPrefixIndex = arguments.indexOf(QUANTITY_PREFIX);
        if (qtyPrefixIndex < 0) {
            return new RemoveStockCommand(productName, 1, true);
        }

        String quantityText = extractValue(arguments, QUANTITY_PREFIX, STOCK_PREFIXES, STOCK_REMOVE_FORMAT);
        int quantity = parseQuantity(quantityText);
        return new RemoveStockCommand(productName, quantity, false);
    }
}
