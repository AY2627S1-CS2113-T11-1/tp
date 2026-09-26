# Generate receipts (v1.0)

Format: `generate receipts c/CUSTOMER`

Example: `generate receipts c/Jonas Low`

Shows a separate receipt for each confirmed, non-cancelled order belonging to the customer.
Each receipt contains the order number, customer name, products, quantities, prices paid,
subtotals and total, using the same table layout as existing order output.
The full customer name is matched without case sensitivity; names may contain spaces.

This version follows the existing sales convention: creating an order records a confirmed
purchase at the price paid. There is no separate unpaid status or payment command.
If payment tracking is added later, receipt eligibility must also check that status.

Customers are looked up in the order history because there is no separate customer database.
An unknown customer produces `Sorry! Customer not in database.` A customer whose orders
are all cancelled receives `No paid, non-cancelled orders found for this customer.`
Generating receipts does not change orders, stock or sales, and can be repeated.
