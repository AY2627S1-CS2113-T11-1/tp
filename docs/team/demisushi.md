# Tham You Jie - Project Portfolio Page

## Overview

Business Management System (BMS) is a Java command-line application for small, growing businesses.
It brings product catalogues, stock, customer orders, sales and expenses into one application.
My contributions focus on helping users inspect customer purchases, record payments and find the
commands they need, together with documenting these features for future developers.

### Summary of Contributions

* **Code contributed**:
  [tP Code Dashboard](https://nus-cs2113-ay2627-s1.github.io/tp-dashboard/?search=DemiSushi&breakdown=true)

* **Feature: Listing sales** (`sales list`)
    * What it does: displays non-cancelled orders and their combined sales value.
    * Justification: gives users a summary without manually adding individual order amounts.
    * Highlights: uses the shared order history so cancellations are reflected in subsequent listings.
      Fixed the display numbering to remain consecutive after cancellation while preserving the original
      order IDs, and updated the regression test
      ([#27](https://github.com/AY2627S1-CS2113-T11-1/tp/pull/27),
      [#67](https://github.com/AY2627S1-CS2113-T11-1/tp/pull/67)).

* **Feature: Generating receipts** (`generate receipts c/CUSTOMER`)
    * What it does: displays individual receipts for a customer's non-cancelled orders.
    * Justification: lets users retrieve purchase details without searching the complete order list.
    * Highlights: separates customer lookup and receipt selection from UI formatting, supports full names
      containing spaces and case-insensitive matching, and distinguishes an unknown customer from one whose
      orders are all cancelled. Reuses the order table layout and returns an unmodifiable result list
      ([#26](https://github.com/AY2627S1-CS2113-T11-1/tp/pull/26)).

* **Feature: Recording customer payments** (`order add pay c/CUSTOMER a/AMOUNT`)
    * What it does: introduced customer payment recording, change calculation and paid-order status.
    * Justification: separates recording an order from recording the customer's payment.
    * Highlights: implemented a dedicated parser requiring the customer before a valid monetary amount,
      with decimal arithmetic for payment and change. My original implementation checked the full balance
      before marking eligible orders paid, rejecting insufficient payment without changing their status
      ([#64](https://github.com/AY2627S1-CS2113-T11-1/tp/pull/64)). Partial-payment support was subsequently
      added by a teammate; I do not claim that extension as my implementation.

* **Feature: Viewing customer order details** (`order details c/CUSTOMER`)
    * What it does: displays each matching order with its products, quantities, costs and status.
    * Highlights: retains cancelled orders for reference, keeps each order separately identified, and
      reports when no orders match. Separates input validation, order selection and table presentation
      ([#65](https://github.com/AY2627S1-CS2113-T11-1/tp/pull/65)).

* **Feature: Command help** (`help [AREA]`)
    * What it does: displays command descriptions and syntax for a selected area.
    * Highlights: supports case-insensitive areas, an overview when no area is given, helpful errors for
      unknown areas and square brackets for optional arguments. Keeps help content in `HelpCommand` and
      formatting in `Ui` ([#66](https://github.com/AY2627S1-CS2113-T11-1/tp/pull/66)).

* **Testing and diagnostics**: added JUnit coverage for sales filtering and totals, receipt lookup and
  result-list behaviour, and the payment, order-details and help commands. Added Java assertions and
  logging to sales and receipts to check internal assumptions and report execution outcomes
  ([#39](https://github.com/AY2627S1-CS2113-T11-1/tp/pull/39),
  [#40](https://github.com/AY2627S1-CS2113-T11-1/tp/pull/40)).

* **Documentation**:
    * User Guide: contributed the sections for sales listing, receipt generation, customer payments,
      individual order details and help, including syntax, validation rules and examples.
    * Developer Guide: wrote the Sales and customer commands section, describing all five features,
      responsibilities, execution flows, alternatives and integration limitations. Added five sequence
      diagrams, one per feature ([#75](https://github.com/AY2627S1-CS2113-T11-1/tp/pull/75)).

* **Contributions to team-based tasks**:
    * Helped resolve UI output/test indentation mismatches and command-signature integration problems
      during merges ([#23](https://github.com/AY2627S1-CS2113-T11-1/tp/pull/23),
      [#26](https://github.com/AY2627S1-CS2113-T11-1/tp/pull/26)).
    * Contributed test-coverage improvements
      ([#72](https://github.com/AY2627S1-CS2113-T11-1/tp/pull/72)) and updated the About Us page
      ([#29](https://github.com/AY2627S1-CS2113-T11-1/tp/pull/29)).

* **Review/mentoring contributions**:
    * Requested an excess-payment regression test in the financial-reporting PR
      ([review](https://github.com/AY2627S1-CS2113-T11-1/tp/pull/76#discussion_r4389623744)).
    * Checked whether renaming the exit command was reflected in help text
      ([review](https://github.com/AY2627S1-CS2113-T11-1/tp/pull/78#discussion_r4389685233)).
    * Reviewed assertion readability in expense tests
      ([review](https://github.com/AY2627S1-CS2113-T11-1/tp/pull/77#discussion_r4389660046)).
