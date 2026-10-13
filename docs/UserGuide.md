# User Guide

## Introduction

{Give a product intro}

## Quick Start

{Give steps to get started quickly}

1. Ensure that you have Java 25 or above installed.
2. Download the latest version of `Duke` from [here](http://link.to/duke).

## Features

{Give detailed description of each feature}

### View confirmed sales: `sales list`
Shows the complete list of confirmed sales. Cancelled orders are not included because their amounts
are excluded from recorded sales. The final row and summary show the total amount recorded from the
confirmed sales.

Format: `sales list`

### Adding a todo: `todo`
Adds a new item to the list of todo items.

Format: `todo n/TODO_NAME d/DEADLINE`

* The `DEADLINE` can be in a natural language format.
* The `TODO_NAME` cannot contain punctuation.

Examples of usage:
`todo n/Write the rest of the User Guide d/next week`

`todo n/Refactor the User Guide to remove passive voice d/13/04/2020`

### Adding an order: `order add`

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

### Cancelling an order: `order cancel`

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

### Listing orders: `order list`

Lists all recorded orders in the order they were created, including cancelled orders.

Format: `order list`

* No additional arguments are required.
* Each order shows its order number, customer name, total number of units, total cost and status.
* The status is either `Active` or `Cancelled`.
* If there are no orders, the application displays a message explaining how to create one.

Example of usage:

`order list`

### Generating receipts: `generate receipts`

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

### Adding an expense: `expense add`

Records an operating expense.

Format: `expense add p/PRODUCT a/AMOUNT`

The amount must be an integer or floating-point number in dollars.

Example of usage:

`expense add p/Bread a/100.50`

### Listing expenses: `expense list`

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

* Add todo `todo n/TODO_NAME d/DEADLINE`
* Add order `order add c/CUSTOMER p/PRODUCT q/QUANTITY [p/PRODUCT q/QUANTITY]...`
* Cancel order `order cancel ORDER_NUMBER`
* List orders `order list`
* Generate receipts `generate receipts c/CUSTOMER`
* Add expense `expense add p/PRODUCT a/AMOUNT`
* List expenses `expense list`
