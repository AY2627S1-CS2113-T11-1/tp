# User Guide

## Introduction

{Give a product intro}

BMS can be used to manage products, stocks, orders, sales, and expenses.

## Quick Start

{Give steps to get started quickly}

1. Ensure that you have Java 25 or above installed.
2. Download the latest version of `Duke` from [here](http://link.to/duke).

## Features

{Give detailed description of each feature}

### 1. Product catalogue

Products are the items your business sells. Orders and stock records refer to products by name, so add
your products to the catalogue first.

Each product has:

* **Name**: identifies the product. Two products cannot share a name. Names are compared ignoring
  upper and lower case, so `Oat Milk` and `oat milk` count as the same product.
* **Price**: the selling price of one unit, in dollars.
* **Stock**: the number of units on hand. A new product starts with 0 stock. Use the stock commands
  to change it.

### 1.1 Adding a product: `product add`

Adds a new product to the catalogue with 0 stock.

Format: `product add p/PRODUCT a/AMOUNT`

* `PRODUCT` is the product name. It cannot be empty, and it cannot contain `p/` or `a/`.
* `AMOUNT` is the unit price. It must be a number that is 0 or more, with at most 2 decimal places.
  Type the number only, without a `$` sign.
* `p/` and `a/` can be given in either order.
* Spaces at the start and end of a value are ignored.
* A product whose name is already in the catalogue is rejected, and the catalogue stays unchanged.

Examples of usage:

`product add p/Oat Milk 1L a/3.50`

Adds Oat Milk 1L at $3.50 per unit.

`product add a/12 p/Coffee Beans 250g`

Adds Coffee Beans 250g at $12.00 per unit.

Example output:

```text
Added product Oat Milk 1L
Price: $3.50
Stock: 0
```

### 1.2 Listing products: `product list`

Lists every product in the catalogue in alphabetical order, ignoring upper and lower case.

Format: `product list`

* No additional arguments are allowed.
* Each line shows the product name, unit price and current stock.
* If the catalogue is empty, the application explains how to add a product.

Example output:

```text
Products (2):
   Coffee Beans 250g $12.00  stock 0
   Oat Milk 1L       $3.50   stock 0
```

### 3.1 Adding an order: `order add`

Records a customer's order and deducts the ordered quantities from stock.

Format: `order add c/CUSTOMER p/PRODUCT q/QUANTITY [p/PRODUCT q/QUANTITY]...`

* The `CUSTOMER` cannot be empty.
* The `PRODUCT` must match an existing product name. Product names are case-insensitive.
* The `QUANTITY` must be a positive whole number and cannot exceed the available stock.
* The customer must come first, followed by at least one product-quantity pair.
* Multiple products can be added by repeating `p/PRODUCT q/QUANTITY`. Do not type the square brackets or ellipsis.
* Each product can appear only once per order. Combine repeated quantities into a single entry.
* If any item is invalid or has insufficient stock, the entire order is rejected and stock remains unchanged.

The application assigns an order number and displays the customer, products, quantities, unit prices, subtotals and total cost. Later changes to product prices do not affect existing order totals.

Examples of usage:

`order add c/Jonas Low p/Bread q/2`

Creates an order for two units of Bread, provided sufficient stock is available.

`order add c/Jonas Low p/Bread q/2 p/Lettuce q/3`

Creates an order for two units of Bread and three units of Lettuce, provided both products have sufficient stock.

### 3.2 Cancelling an order: `order cancel`

Cancels an active order and restores its ordered quantities to stock.

Format: `order cancel ORDER_NUMBER`

* The `ORDER_NUMBER` must be a positive whole number matching an existing order.
* Use `order list` to find the order number.
* An order can only be cancelled once.
* Cancelled orders remain in the list with the status `Cancelled`. Their order numbers are not reused.
* An invalid order number or an already-cancelled order produces an error without changing stock.

Example of usage:

`order cancel 1`

Cancels order 1. The application displays a confirmation, the quantities restored to stock and the amount excluded from recorded sales.

### 3.3 Listing orders: `order list`

Lists all recorded orders in the order they were created, including cancelled orders.

Format: `order list`

* No additional arguments are required.
* Each order shows its order number, customer name, total number of units, total cost and status.
* The status is either `Active` or `Cancelled`.
* If there are no orders, the application displays a message explaining how to create one.

Example of usage:

`order list`

### 4.1 View confirmed sales: `sales list`
Shows the complete list of confirmed sales. Cancelled orders are not included because their amounts
are excluded from recorded sales. The final row and summary show the total amount recorded from the
confirmed sales.

Format: `sales list`

### 4.2 Generating receipts: `generate receipts`

Shows a separate receipt for each confirmed, non-cancelled order belonging to a customer.

Format: `generate receipts c/CUSTOMER`

* The `CUSTOMER` cannot be empty. Specify exactly one customer and no other fields.
* Use the full customer name. Names are case-insensitive and may contain spaces.
* Each receipt shows the order number, customer name, products, quantities, prices paid, subtotals and total.
* Receipts use the same table layout as the output of `order add`.
* Creating an order records a confirmed purchase. There is no separate payment command.
* A customer with no order history receives `Sorry! Customer not in database.`
* A customer whose orders are all cancelled receives `No paid, non-cancelled orders found for this customer.`
* Generating receipts does not change orders, stock or recorded sales. The command can be repeated.

Example of usage:

`generate receipts c/Jonas Low`

Shows one receipt for each confirmed, non-cancelled order placed by Jonas Low, in the order the purchases were recorded.

### 5.1 Adding an expense: `expense add`

Records an operating expense.

Format: `expense add p/PRODUCT a/AMOUNT`

The amount must be an integer or floating-point number in dollars.

Example of usage:

`expense add p/Bread a/100.50`

### 5.2 Listing expenses: `expense list`

Lists all recorded expenses in the order they were added.

Format: `expense list`

Example output:

```text
1. Bread: $100.50
2. Lettuce: $42.30
```

## FAQ

**Q**: How do I transfer my data to another computer?

**A**: {your answer here}

## Command Summary

* Add product `product add p/PRODUCT a/AMOUNT`
* List products `product list`
* Add order `order add c/CUSTOMER p/PRODUCT q/QUANTITY [p/PRODUCT q/QUANTITY]...`
* Cancel order `order cancel ORDER_NUMBER`
* List orders `order list`
* View sales: `sales list`
* Generate receipts `generate receipts c/CUSTOMER`
* Add expense `expense add p/PRODUCT a/AMOUNT`
* List expenses `expense list`
