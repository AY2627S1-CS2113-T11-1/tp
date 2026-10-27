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

### 2. Stock inventory

Stock records track the quantity of each product physically on hand. When you add or remove stock,
the system updates both the stock records and the product catalogue, ensuring available quantities
remain in sync.

### 2.1 Adding stock: `stock add`

Adds stock to an existing product in the catalogue and updates or creates a corresponding stock entry.

Format: `stock add p/PRODUCT q/QUANTITY`

* `PRODUCT` is the product name. It must match an existing product in the catalogue. Product names are
  case-insensitive.
* `QUANTITY` is the number of units to add. It must be a positive whole number (e.g. 1, 10, 50).
* `p/` and `q/` can be given in either order.
* Spaces at the start and end of a value are ignored.
* If the product does not exist in the catalogue, the addition is rejected. Add the product with `product add` first.
* Unit price is automatically retrieved from the product in the catalogue.
* If a stock record already exists for the product, its quantity is increased. Otherwise, a new stock record is created.

Examples of usage:

`stock add p/Oat Milk 1L q/20`

Adds 20 units of stock for Oat Milk 1L.

`stock add q/10 p/Coffee Beans 250g`

Adds 10 units of stock for Coffee Beans 250g.

Example output:

```text
Creates stock for a new product Oat Milk 1L with quantity 20 and cost $3.50 each.
```

Or when updating existing stock:

```text
Updated stock for Oat Milk 1L. New quantity: 35
```

### 2.2 Removing stock: `stock remove`

Removes a specified quantity of stock from a product.

Format: `stock remove p/PRODUCT [q/QUANTITY]`

* `PRODUCT` is the product name. Product names are case-insensitive.
* `QUANTITY` is optional. If specified, it must be a positive whole number.
* If `q/QUANTITY` is omitted, the command defaults to removing 1 unit.
* If the product is not found in the catalogue or stock list, the application reports `Product not found in stock.`
* If the quantity to remove is greater than the available stock, the stock level is reduced to 0 (stock never drops below 0).

Examples of usage:

`stock remove p/Oat Milk 1L`

Removes 1 unit of Oat Milk 1L (default behavior).

`stock remove p/Coffee Beans 250g q/5`

Removes 5 units of Coffee Beans 250g.

Example output (default behavior):

```text
Removes 1 unit (default behaviour) of Oat Milk 1L
```

Example output (custom quantity):

```text
Removes 5 units of Coffee Beans 250g
```

### 2.3 Listing stock: `stock list`

Lists all current stock records in a table showing the product name and quantity on hand.

Format: `stock list`

* No additional arguments are allowed. Any extra text produces an error.
* Displays each product and its current quantity in an aligned table.

Example of usage:

`stock list`

Example output:

```text
+-------------------+-----+
| Product           | Qty |
+-------------------+-----+
| Coffee Beans 250g |  10 |
| Oat Milk 1L       |  34 |
+-------------------+-----+
```

### 2.4 Viewing low stock: `stock low`

Displays products whose current stock level is at or below a specified threshold, alerting you when items need replenishing.

Format: `stock low [q/THRESHOLD]`

* If `THRESHOLD` is omitted, the default threshold of 5 is used.
* `THRESHOLD` must be a non-negative whole number (0, 1, 2, ...).
* You may specify the threshold with or without the `q/` prefix (e.g. `stock low q/10` or `stock low 10`).
* If no products have low stock, a message confirms that all items have sufficient stock.

Example of usage:

`stock low`

Example output (items at or below default threshold 5):

```text
Low stock items (quantity <= 5):
+---------+-----+
| Product | Qty |
+---------+-----+
| Apples  |   3 |
| Oranges |   5 |
+---------+-----+
```

Example output (when all items have sufficient stock):

```text
All products have sufficient stock (more than 5).
```

Example of usage with custom threshold:

`stock low q/10`

### 3. Orders

An order records what a customer buys. Creating an order takes the ordered units out of stock, and
cancelling it puts them back, so stock levels always reflect your orders.

Each order has:

* **Order number**: assigned automatically, starting from 1. Numbers are never reused, so a number always
  refers to the same order, even after it is cancelled.
* **Customer**: the name of the customer who placed the order.
* **Items**: one or more products, each with a quantity and the unit price at the time of the order.
* **Status**: `Active`, or `Cancelled` once the order is cancelled.

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

Expected output (Bread costs $1.88 and Lettuce costs $2.50):

```text
Order 3 created for Jonas Low.
+---------+-----+------------+----------+
| Product | Qty | Unit price | Subtotal |
+---------+-----+------------+----------+
| Bread   |   2 |      $1.88 |    $3.76 |
| Lettuce |   3 |      $2.50 |    $7.50 |
+---------+-----+------------+----------+
| Total   |   5 |            |   $11.26 |
+---------+-----+------------+----------+
```

Expected output (the same product appears twice):

```text
Sorry! "bread" appears more than once in this order. Put the full quantity in a single q/ instead.
```

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

Expected output (order 1 contained 2 units of Bread at $1.88 each):

```text
Order 1 for Jonas Low cancelled.
Stock restored:
+---------+----------+------------+----------+
| Product | Restored | Unit price | Subtotal |
+---------+----------+------------+----------+
| Bread   |        2 |      $1.88 |    $3.76 |
+---------+----------+------------+----------+
| Total   |        2 |            |    $3.76 |
+---------+----------+------------+----------+
Amount excluded from recorded sales: $3.76
```

Expected output (order 1 was already cancelled):

```text
Sorry! Order 1 is already cancelled.
```

### 3.3 Listing orders: `order list`

Lists all recorded orders in the order they were created, including cancelled orders.

Format: `order list`

* No additional arguments are allowed. Any extra text produces an error.
* Each order shows its order number, customer name, total number of units, total cost and status.
* The total includes any discount applied with `order discount`.
* The status is either `Active` or `Cancelled`.
* If there are no orders, the application displays a message explaining how to create one.

Example of usage:

`order list`

Expected output:

```text
Orders (3):
+-----+-----------+-------+--------+-----------+
| No. | Customer  | Units |  Total | Status    |
+-----+-----------+-------+--------+-----------+
|   1 | Jonas Low |     2 |  $3.76 | Cancelled |
|   2 | Mei Tan   |     5 |  $8.46 | Active    |
|   3 | Jonas Low |     5 | $11.26 | Active    |
+-----+-----------+-------+--------+-----------+
```

### 3.4 Editing an order: `order edit`

Updates an active order's customer, product or quantity. The order keeps its original number, while its total and the affected stock quantities are updated accordingly.

Format: `order edit ORDER_NUMBER [c/CUSTOMER] [p/PRODUCT] [q/QUANTITY]`

* `ORDER_NUMBER` must refer to an existing, active order. Cancelled orders cannot be edited.
* At least one field must be provided. Fields that are not provided remain unchanged. Fields may be given in any order.
* Square brackets indicate optional fields and should not be typed.
* Customer and product names cannot be blank. The product must exist in the catalogue. Product names are case-insensitive.
* `QUANTITY` must be a positive whole number.
* Increasing the quantity deducts the additional units from stock. Decreasing it returns the difference to stock.
* Changing the product restores the original product's stock and deducts the required quantity of the replacement product.
* If the product is unchanged, its stored purchase price is retained. A replacement product uses its current price.
* Any existing discount percentage is retained and applied to the updated subtotal.
* `p/` and `q/` can only be used on orders containing a single product. For an order with several products, only `c/` can be edited.
* Missing values, repeated prefixes and prefixes other than `c/`, `p/` and `q/` are rejected.
* All checks must pass before changes are applied. If an edit fails, the order and stock remain unchanged.

Example of usage (order 2 contains 4 units at $1.88 each, and enough additional stock is available):

`order edit 2 c/Mei Tan q/5`

Expected output:

```text
Order 2 updated.
Customer: Mei Tan
Units: 5
Total: $9.40
Status: Active
```

Expected output (insufficient stock):

```text
Sorry! Unable to edit order: insufficient stock.
No changes were made to the order or stock.
```

### 3.5 Discounting an order: `order discount`

Applies a percentage discount to an active order and updates its total. An existing discount can be replaced or removed.

Format: `order discount ORDER_NUMBER d/PERCENTAGE`

* `ORDER_NUMBER` must refer to an existing, active order. Cancelled orders cannot receive discounts.
* `PERCENTAGE` must be a number between 0 and 100 inclusive, with up to two decimal places.
* Enter the percentage as a number without the `%` symbol.
* Setting the percentage to `0` removes the existing discount.
* A new discount replaces the previous discount. Discounts are not added together.
* The discount is calculated using the undiscounted subtotal, based on the order's quantities and stored unit prices.
* The discount amount is rounded to the nearest cent, with half-cent values rounded up. The final total is the subtotal minus this amount.
* Applying a discount does not change stock quantities or stored unit prices.
* If the order is edited later, the same discount percentage applies to its updated subtotal.
* Order lists, sales, receipts and cancellations use the discounted total.
* Missing values, repeated prefixes and unexpected arguments are rejected. Invalid input leaves the order unchanged.

Example of usage (order 2 is active and has an undiscounted subtotal of $9.40):

`order discount 2 d/10`

Expected output:

```text
Discount updated for order 2.
Subtotal: $9.40
Discount: 10% ($0.94)
Total: $8.46
```

`order discount 2 d/0`

Expected output (remove discount):

```text
Discount removed from order 2.
Subtotal: $9.40
Discount: 0% ($0.00)
Total: $9.40
```

Expected output (percentage outside the allowed range):

```text
Sorry! Discount must be between 0 and 100.
No changes were made to the order.
```

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

### 4.3 Recording a customer's payment: `order add pay`

Records a payment for the customer's unpaid, non-cancelled orders. Payments are applied in
order-history order and may be partial; a later payment can settle the remaining balance.
Orders start unpaid. The customer must already appear in the order history; there is no
separate customer registration command. Full names are matched without case sensitivity.

Format: `order add pay c/CUSTOMER a/AMOUNT`

Enter the customer before the amount. Names may contain spaces. The amount must be a
non-negative integer or decimal with at most two decimal places, such as `20` or `20.50`.

Example: `order add pay c/BigChungus a/20.50`

For an outstanding total of $20.40:

```text
    BigChungus has paid $20.50! The change to return is: $0.10.
```

If the customer has no order history:

```text
    Sorry! Customer BigChungus is not a registered customer.
```

If the payment is smaller than the outstanding balance, it is applied across eligible orders
until it is exhausted. Already-paid and cancelled orders are excluded from subsequent payments.
Any excess is returned as change. If no eligible orders remain, no payment is accepted.
Payment does not change stock, record an expense, or implement refunds. Payment status is held
in memory for the current session, like the existing order history.

### 4.4 Viewing a customer's order details: `order details`

Displays each order for a customer, oldest first, including cancelled orders.

Format: `order details c/CUSTOMER`

Example: `order details c/You Jie`

Use the customer's full name. Names may contain spaces and are matched without case sensitivity.
Each order has its own order number, Active or Cancelled status, and a table of products,
quantities and line costs. The final row shows the total item count and cost for that order.
Cancelled orders show their original contents and total for reference.

If no orders match, the app displays `No orders found for You Jie.`
Viewing details does not change orders, stock or sales. Missing customer names and extra fields
are rejected with the command format.

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

### 5.3 View financial report: `finances`

Calculates and displays the total value of all recorded orders, total payments received, total
recorded expenses and the overall profit or loss. Total Orders includes unpaid orders. Cancelled
orders remain in order history and are included in Total Orders, but are excluded from Total Sales.
Partial payments are included in Total Sales, so an order's full value can be larger than its
reported sales amount until the remaining balance is paid.

Format: `finances`

Example output:

```text
Total Orders (including unpaid): $500.00
Total Sales: $500.00
Total Expenses: $1000.00
______________________________
Overall Loss: $500.00
```

The report uses the same two-decimal currency format as the rest of the application. A zero
difference is displayed as `Profit`.


### 6.1 Getting command help: `help`

Format: `help {AREA}`

Example: `help stock`

Shows all commands for the selected area, with a numbered description and command format.
Optional parameters are shown in square brackets. Area names are case-insensitive.

Available areas: `product`, `stock`, `order`, `sales`, `finances`, `expense`, `generate`, `general`.
Use `help generate` for receipt commands and `help general` for help and exit commands.
Enter `help` without an area to see the available areas and usage. Unknown areas and multiple
areas produce a helpful error with the usage format.

Stock help uses `stock add p/PRODUCT q/QUANTITY`: the product must already exist in the
catalogue, and its selling price is used. No separate price parameter is required.


### 6.2 Exiting the program: `bye`

Exits the BMS program.

Format: `bye`


## FAQ

**Q**: How do I transfer my data to another computer?

**A**: {your answer here}

## Command Summary

| Description | Command |
| --- | --- |
| **<ins>Product<ins>** | |
| Add product | `product add p/PRODUCT a/AMOUNT` |
| List products | `product list` |
| | |
| **<ins>Stock<ins>** | |
| Add stock | `stock add p/PRODUCT q/QUANTITY` |
| Remove stock | `stock remove p/PRODUCT [q/QUANTITY]` |
| List stock | `stock list` |
| | |
| **<ins>Order<ins>** | |
| Add order | `order add c/CUSTOMER p/PRODUCT q/QUANTITY [p/PRODUCT q/QUANTITY]` |
| Cancel order | `order cancel ORDER_NUMBER` |
| List orders | `order list` |
| Edit order | `order edit ORDER_NUMBER [c/CUSTOMER] [p/PRODUCT] [q/QUANTITY]` |
| Discount order | `order discount ORDER_NUMBER d/PERCENTAGE` |
| Pay outstanding orders | `order add pay c/CUSTOMER a/AMOUNT` |
| View customer order details | `order details c/CUSTOMER` |
| | |
| **<ins>Sales<ins>** | |
| View sales | `sales list` |
| | |
| **<ins>Finances<ins>** | |
| View financial report | `finances` |
| | |
| **<ins>Generate<ins>** | |
| Generate receipts | `generate receipts c/CUSTOMER` |
| | |
| **<ins>Expense<ins>** | |
| Add expense | `expense add p/PRODUCT a/AMOUNT` |
| List expenses | `expense list` |
| | |
| **<ins>General<ins>** | |
| View help | `help [AREA]` |
| Exit the program | `bye` |
