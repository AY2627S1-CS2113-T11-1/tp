package seedu.duke;

import seedu.duke.command.AddProductCommand;
import seedu.duke.command.Command;
import seedu.duke.command.ExitCommand;
import seedu.duke.command.ListProductCommand;

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
        if (noun.equals("bye")) {
            return new ExitCommand();
        }
        throw new SystemException("I don't recognise \"" + words[0] + "\". Known commands: product, bye");
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
