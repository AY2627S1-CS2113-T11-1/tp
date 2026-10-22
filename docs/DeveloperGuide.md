# Developer Guide

## Acknowledgements

{list here sources of all reused/adapted ideas, code, documentation, and third-party libraries -- include links to the original source as well}

* **AI assistance (Brian Yang, @zechby):** Claude (Anthropic) was used to add the assertions and logging in
  `Product`, `ProductList` and `AddProductCommand`, the related `ProductTest` cases, and the first draft of the
  product catalogue sections of the User Guide.

## Design & implementation

{Describe the design and implementation of the product. Use UML diagrams and short code snippets where applicable.}

### Orders

The order feature records what customers buy and keeps stock in step with it. It supports five commands:
`order add`, `order cancel`, `order list`, `order edit` and `order discount`.

* `OrderList` stores every order and hands out order numbers. Numbers start at 1 and only go up, so a number
  always refers to the same order, even after it is cancelled.
* `Order` holds a customer, one or more `OrderItem`s, a cancelled flag and a discount percentage.
* `OrderItem` is one line of an order: a `Product`, a quantity and the unit price paid. The unit price is copied
  from the product when the line is created, so a later price change does not alter what a past order says the
  customer paid.
* Each command is its own `Command` subclass. `Parser` validates the input, so commands only ever receive
  well-formed values. A command checks the rules that depend on stored data, such as whether an order exists or
  stock is sufficient.

#### Adding an order

`order add c/CUSTOMER p/PRODUCT q/QUANTITY [p/PRODUCT q/QUANTITY]...` is handled by `Parser#parseAddOrder` and
`AddOrderCommand`.

**Parsing.** Unlike `product add`, `p/` and `q/` may appear many times here, and each `q/` belongs to the `p/`
just before it. So the parser cuts the arguments into (prefix, value) pairs in the order they were typed, then
checks them in sequence: one `c/` first, followed by one or more `p/ q/` pairs. A prefix only counts when it is
at the start or after a space, so a product named `Salt and Pepper Mix 1c/2` is not split in the middle. A
product named twice in one order is rejected. On their own, two lines for the same product could each pass the
stock check while together asking for more than is in stock.

**Execution.** `AddOrderCommand` works in two passes:

1. *Check.* Look up every product and check its stock. If any line fails, a `SystemException` is thrown before
   anything has changed.
2. *Apply.* Deduct each quantity from stock, build the `OrderItem`s, and call `OrderList#create`, which assigns
   the next order number.

**Design considerations.**

* *All-or-nothing vs. partial orders.* Accepting the valid lines and dropping the rest would leave the user
  unsure what was recorded. Rejecting the whole order keeps stock and the order list predictable.
* *Copying the unit price vs. reading it from the product.* Reading the price from `Product` each time would be
  simpler, but would change the totals of past orders whenever a price changes.

#### Cancelling an order

`order cancel ORDER_NUMBER` is handled by `Parser#parseCancelOrder` and `CancelOrderCommand`.

The command finds the order, calls `Order#cancel()`, then returns each line's quantity to stock. `cancel()` runs
first because it throws if the order is already cancelled, so stock can never be restored twice. The order stays
in `OrderList`, marked as cancelled. `sales list` uses `OrderList#getConfirmedSales()`, which leaves cancelled
orders out, and `ReceiptGenerator` skips them too.

**Design considerations.**

* *Marking as cancelled vs. deleting.* Deleting the order would either leave an unexplained gap in the numbering
  or let a number be reused for a different order. Keeping it, marked `Cancelled`, preserves the history and
  keeps every number unique.

#### Listing orders

`order list` is handled by `ListOrderCommand`, which passes `OrderList#getAll()` to `Ui#showOrders`. The list is
returned as a read-only view, so a caller cannot add or remove orders except through `OrderList`. `Ui` works out
each order's units and status, and sizes each table column to its longest value, so long names widen the table
instead of breaking its alignment.

**Design considerations.**

* *Showing vs. hiding cancelled orders.* Cancelled orders are shown with their status, so the numbers in the list
  always run 1, 2, 3... For sales figures that exclude them, `sales list` uses `getConfirmedSales()`.

#### Editing an order

`order edit ORDER_NUMBER [c/CUSTOMER] [p/PRODUCT] [q/QUANTITY]` is handled by `Parser#parseEditOrder` and
`EditOrderCommand`.

**Parsing.** The parser reads the order number, then cuts the rest into (prefix, value) pairs. It recognises
every prefix the system uses (`a/`, `c/`, `d/`, `p/`, `q/`), not just the three `order edit` supports, so that
`order edit 2 d/10` is rejected rather than read as part of a name. Each prefix may appear at most once. Fields
that are left out are passed to the command as `null`, meaning "keep the current value".

**Execution.** The command runs in two passes, like `AddOrderCommand`:

1. *Check.* Find the order, reject it if it is missing or cancelled, then work out the new order line in
   `planLineChange`: look up the replacement product (if any), and check that stock can cover the change.
   For the same product, only the extra units (new quantity minus old quantity) must be in stock. For a
   different product, the full new quantity must be in stock.
2. *Apply.* Return the old line's units to stock, take the new line's units out, and call `Order#update`.

Because nothing changes until every check has passed, a failed edit needs no rollback. An assertion after the
stock movement checks that units were only moved, never created or lost.

The unit price follows the product. An unchanged product keeps the price stored on the order line (through the
`OrderItem(Product, int, double)` constructor), so a customer is not re-priced by an edit. A replacement product
uses its current catalogue price.

**Design considerations.**

* *Multi-product orders.* `p/` and `q/` cannot say which line of a multi-product order they mean, so they are
  only accepted for single-product orders. Only `c/` can be edited on other orders. An alternative is a line
  selector such as `order edit 2 l/1 q/5`. It was not added because the requirements describe orders with a
  single product.
* *Validate-then-apply vs. apply-then-undo.* Applying changes and undoing them on failure would also work, but
  an error during the undo could leave stock wrong. Checking first keeps every failure free of side effects.

#### Discounting an order

`order discount ORDER_NUMBER d/PERCENTAGE` is handled by `Parser#parseDiscountOrder` and `DiscountOrderCommand`,
which calls `Order#setDiscountPercent`.

**Only the percentage is stored.** `Order` keeps `discountPercent` and works out the subtotal, the discount
amount and the total from its current lines whenever they are asked for. So when an order is edited, the
same percentage applies to the new subtotal with no extra code in `EditOrderCommand`. Setting a new percentage
replaces the old one, and `0` removes it.

**Money uses `BigDecimal`.** The discount amount must be rounded to the cent with half a cent rounded up.
A `double` cannot hold values such as $1.175 (12.5% of $9.40) exactly: it stores 1.17499..., which would round
down to $1.17. `Order` therefore converts each stored unit price with `BigDecimal.valueOf`, calculates
`subtotal × percent ÷ 100`, and rounds with `RoundingMode.HALF_UP`. The public getters still return `double`,
so existing callers (order list, sales, receipts) did not need to change. The parser also keeps the percentage
as a `BigDecimal`, so 12.5 is never approximated.

**Display.** Order tables (receipts, cancellations) sum the line subtotals in their last row. When a discount
applies, that row is labelled `Subtotal`, followed by a `Discount:` line and a `Total:` line, so the column
still adds up. Orders without a discount print exactly as before.

**Design considerations.**

* *Storing the percentage vs. the amount.* Storing a fixed dollar amount would need recalculating after every
  edit and could exceed a reduced subtotal. Storing the percentage avoids both problems.
* *`BigDecimal` everywhere vs. only for discounts.* Converting every price in the system to `BigDecimal` would be
  cleaner but would touch every feature. Prices are limited to two decimal places, so converting at the point of
  calculation gives exact results with a much smaller change.


## Product scope
### Target user profile

{Describe the target user profile}

This product is for growing business owners who currently lack a central enterprise software to maintain the overhead of logistics and sales for their business.

### Value proposition

{Describe the value proposition: what problem does it solve?}

It provides an easy to use platform to maintain and oversee the status of a business. It allows control over the system by recording the status of supply chain operations like procurement, as well as sales and inventory of the branch. Users include managers, logisticians and sales employees.

## User Stories

|Version| As a ... | I want to ... | So that I can ...|
|--------|----------|---------------|------------------|
|v1.0|manager|view expenses|oversee the business with better clarity|

|Version| As a ... | I want to ... | So that I can ...|
|--------|----------|---------------|------------------|
|v1.0|new user|see usage instructions|refer to them when I forget how to use the application|
|v2.0|user|find a to-do item by name|locate a to-do without having to go through the entire list|

## Non-Functional Requirements

{Give non-functional requirements}

## Glossary

* *glossary item* - Definition

## Instructions for manual testing

{Give instructions on how to do a manual product testing e.g., how to load sample data to be used for testing}
