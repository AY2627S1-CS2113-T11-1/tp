# Caleb Thow - Project Portfolio Page

## Overview

Business Management System (BMS) is a Java command-line application for small, growing businesses.
It keeps product catalogues, stock, customer orders, sales and operating expenses in one place.
My contributions focused on expense and financial management, payment accuracy, command usability,
and the documentation needed to explain these features to users and future developers.

### Summary of Contributions

* **Code contributed**:
  [tP Code Dashboard](https://nus-cs2113-ay2627-s1.github.io/tp-dashboard/?search=CalebThow&breakdown=true)

* **Feature: Expense management** (`expense add`, `expense list`)
    * What it does: records operating expenses against a product and lists recorded expenses in insertion order.
    * Justification: gives a business owner a simple way to record and inspect operating costs alongside sales.
    * Highlights: introduced the `Expense` and `ExpenseList` domain classes, parser support and dedicated commands;
      validates non-blank product names, non-negative amounts and at most two decimal places; preserves insertion
      order while returning a copy of the stored list so presentation code cannot mutate application state.
      Improved the confirmation and empty-list feedback so the CLI remains understandable for both successful and
      no-data cases (initial implementation and refinements in [#76](https://github.com/AY2627S1-CS2113-T11-1/tp/pull/76)
      and [#77](https://github.com/AY2627S1-CS2113-T11-1/tp/pull/77)).

* **Feature: Financial reporting** (`finances`)
    * What it does: reports total order value, payments received, total expenses and profit or loss.
    * Justification: provides a compact view of the business's financial position without requiring manual calculation.
    * Highlights: separates total order value from recorded sales, excludes cancelled orders from sales, includes
      partial payments only to the amount received, and computes profit or loss as sales minus expenses. Refactored
      monetary values to use `BigDecimal` throughout the relevant domain and UI paths, avoiding floating-point
      rounding errors in currency calculations. Added focused tests for profit, loss, equal totals and partial
      payments ([#76](https://github.com/AY2627S1-CS2113-T11-1/tp/pull/76)).

* **Enhancement: Payment and currency correctness**
    * Updated customer-payment handling so payments are applied across eligible unpaid orders in history order,
      support partial settlement, and return excess as change. Kept the User Guide, order/payment tests and
      financial report consistent with the resulting rules.
    * Replaced `double`-based currency handling with `BigDecimal` in the expense and financial-reporting paths,
      with validation at the domain boundary and consistent two-decimal display. This was a cross-cutting change
      because order, product, stock, payment, UI and test code share monetary values.

* **Enhancement: Command usability**
    * Renamed the placeholder `bye` command to the clearer `exit` command and updated parsing, help text, tests and
      the User Guide ([#78](https://github.com/AY2627S1-CS2113-T11-1/tp/pull/78)).
    * Aligned command ordering and the command summary so that the help output and User Guide present the same
      feature groups and syntax.

* **Testing and diagnostics**:
    * Added unit tests for `Expense`, `ExpenseList`, expense commands, parser behaviour and financial reports.
      Added Java assertions around expense-list operations and enabled assertions in Gradle test and run tasks
      ([#43](https://github.com/AY2627S1-CS2113-T11-1/tp/pull/43)).
    * The tests cover validation boundaries, insertion order, display behaviour, partial payments, profit, loss,
      and the zero-difference case, reducing the risk of silently incorrect financial output.

* **Contributions to the User Guide**:
    * Wrote and refined the sections for adding/listing expenses and viewing the financial report, including syntax,
      validation rules, partial-payment semantics, examples and currency-formatting behaviour.
    * Updated the command summary, help/exit documentation, and related command descriptions as the CLI evolved.

* **Contributions to the Developer Guide**:
    * Wrote and reorganised the Expense and financial management section, documenting the command flow, class
      responsibilities, financial calculations, `BigDecimal` design choice, data-protection boundary and testing.
    * Added the expense/finance architecture diagram and sequence diagrams for adding an expense, listing expenses,
      and generating the financial report. Converted the diagrams to portable SVG assets and added contents and
      attribution structure to the guide ([#79](https://github.com/AY2627S1-CS2113-T11-1/tp/pull/79),
      [#80](https://github.com/AY2627S1-CS2113-T11-1/tp/pull/80),
      [#81](https://github.com/AY2627S1-CS2113-T11-1/tp/pull/81)).

* **Contributions to team-based tasks**:
    * Updated the team profile and project documentation, removed obsolete placeholder command documentation,
      and fixed documentation/build compatibility issues when features were integrated.
    * Coordinated cross-feature changes so payment, order, product, stock, help and finance code continued to use
      consistent money types and command names.

* **Review/mentoring contributions**:
    * Reviewed the team's financial-reporting and expense changes, including checks for payment semantics,
      assertions and help-text consistency. Relevant integration work is recorded in [#76](https://github.com/AY2627S1-CS2113-T11-1/tp/pull/76),
      [#77](https://github.com/AY2627S1-CS2113-T11-1/tp/pull/77) and [#78](https://github.com/AY2627S1-CS2113-T11-1/tp/pull/78).

* **Contributions beyond the project team**:
    * To be updated with evidence from the Practical Exam dry run, forum contributions, or bug reports to other teams.

