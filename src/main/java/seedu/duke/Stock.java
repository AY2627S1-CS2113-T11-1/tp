package seedu.duke;

public class Stock {
    String product;
    double price;
    int quantity;

    public Stock(String product, double price, int quantity) {
        this.product = product;
        this.price = price;
        this.quantity = quantity;
    }
}
