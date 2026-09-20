package seedu.duke;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Stores the product catalogue in memory.
 *
 * Products are held in insertion order and sorted only when they are listed. With the small
 * catalogues the system deals with, sorting on demand is cheap and keeps this class easy to follow;
 * a sorted collection such as a {@code TreeMap} would only pay off at a much larger scale.
 */
public class ProductList {
    private final ArrayList<Product> products = new ArrayList<>();

    /**
     * Adds a product to the catalogue.
     *
     * Product names identify products, so duplicates are rejected here rather than in the
     * command, keeping the rule in one place.
     *
     * @param product the product to add
     * @throws SystemException if a product with the same name already exists, ignoring case
     */
    public void add(Product product) throws SystemException {
        if (findByName(product.getName()) != null) {
            throw new SystemException("A product named \"" + product.getName() + "\" is already in the catalogue.");
        }
        products.add(product);
    }

    /**
     * Returns the product with the given name ignoring case, or null if the catalogue has none.
     *
     * @param name the product name to look for
     */
    public Product findByName(String name) {
        for (Product product : products) {
            if (product.getName().equalsIgnoreCase(name)) {
                return product;
            }
        }
        return null;
    }

    public int size() {
        return products.size();
    }

    /**
     * Returns the products sorted alphabetically by name, ignoring case.
     *
     * The returned list is a copy, so sorting it does not disturb the stored order.
     */
    public List<Product> getSortedByName() {
        List<Product> sorted = new ArrayList<>(products);
        sorted.sort(Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER));
        return sorted;
    }
}
