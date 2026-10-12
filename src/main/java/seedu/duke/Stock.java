package seedu.duke;

public class Stock {
    public String product;
    public double price;
    public int quantity;

    public Stock(String product, double price, int quantity) {
        this.product = product;
        this.price = price;
        this.quantity = quantity;
    }
}
