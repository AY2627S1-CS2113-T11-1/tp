# Brian Yang - Project Portfolio Page

## Overview

Business Management System (BMS) is a Java command-line application for small, growing businesses.
It keeps product catalogues, stock, customer orders, sales and operating expenses in one place.
My contributions centred on the product catalogue, which every other feature refers to, and on the first
version of the command structure that the rest of the team built their features on.

### Summary of Contributions

* **Code contributed**:
  [tP Code Dashboard](https://nus-cs2113-ay2627-s1.github.io/tp-dashboard/?search=zechby&breakdown=true)

* **Feature: Adding and listing products** (`product add`, `product list`)
    * What it does: adds a product with a name and unit price to the catalogue, and lists the catalogue
      sorted by name.
    * Justification: orders, stock records and expenses all refer to products by name, so the catalogue is
      the first thing a user sets up.
    * Highlights: the parser looks for each prefix instead of splitting on spaces, so a name such as
      `Oat Milk 1L` stays whole and `p/` and `a/` can come in either order. Duplicate names are rejected,
      ignoring case, inside `ProductList#add`, so the rule holds whichever command adds a product
      ([#12](https://github.com/AY2627S1-CS2113-T11-1/tp/pull/12)).

* **Feature: Deleting a product** (`product delete`)
    * What it does: removes a product from the catalogue together with its stock record.
    * Justification: lets a business retire items it no longer sells.
    * Highlights: spans three features, adding `OrderList#findActiveOrderWithProduct` and
      `StockList#removeByProduct`. A product in an active order cannot be deleted, because cancelling or
      editing that order would return stock to a product no longer in the catalogue. All checks run before
      anything is removed, so a failed delete changes nothing
      ([#95](https://github.com/AY2627S1-CS2113-T11-1/tp/pull/95)).

* **Feature: Finding products** (`product find`)
    * What it does: lists the products whose names contain a keyword, ignoring case.
    * Highlights: keywords may contain spaces, and a keyword containing `p/` or `a/` is rejected because no
      product name can match it. Shares its output format with `product list`
      ([#96](https://github.com/AY2627S1-CS2113-T11-1/tp/pull/96)).

* **Enhancement: Initial command structure**: wrote the first version of the abstract `Command` class,
  `Parser`, `Ui`, `SystemException` and the read-parse-execute loop in `Duke`, which every later feature extends
  ([#12](https://github.com/AY2627S1-CS2113-T11-1/tp/pull/12)).

* **Bug fixes**: `expense add` with an empty amount showed the `product add` format, because both commands
  share `Parser#parsePrice`; it now takes the caller's format hint
  ([#93](https://github.com/AY2627S1-CS2113-T11-1/tp/pull/93)). Prices written with an exponent, such as
  `1e-3`, crashed the application; they are now rejected with an error message
  ([#PR](https://github.com/AY2627S1-CS2113-T11-1/tp/pull/PR)).

* **Testing**: wrote 59 JUnit tests for the parser, catalogue and product commands, covering duplicate names in
  different cases, sorting without changing the stored order, and deleting a product with or without stock and
  in an active or a cancelled order. Added assertions and logging to the product catalogue
  ([#47](https://github.com/AY2627S1-CS2113-T11-1/tp/pull/47)).

* **Documentation**:
    * User Guide: wrote section 1 (Product catalogue), covering `product add`, `product list`,
      `product delete` and `product find`, with examples
      ([#48](https://github.com/AY2627S1-CS2113-T11-1/tp/pull/48),
      [#95](https://github.com/AY2627S1-CS2113-T11-1/tp/pull/95),
      [#96](https://github.com/AY2627S1-CS2113-T11-1/tp/pull/96)).
    * Developer Guide: wrote section 2.1 (Product catalogue), covering design, implementation and design
      alternatives, and the product catalogue manual-testing steps
      ([#94](https://github.com/AY2627S1-CS2113-T11-1/tp/pull/94),
      [#97](https://github.com/AY2627S1-CS2113-T11-1/tp/pull/97)). Added the product catalogue class diagram
      and the `product delete` sequence diagram, and redrew both after tutor feedback to follow the course UML
      reference sheet ([#119](https://github.com/AY2627S1-CS2113-T11-1/tp/pull/119)).

* **Contributions to team-based tasks**:
    * Merged teammates' PRs [#37](https://github.com/AY2627S1-CS2113-T11-1/tp/pull/37),
      [#38](https://github.com/AY2627S1-CS2113-T11-1/tp/pull/38) and
      [#46](https://github.com/AY2627S1-CS2113-T11-1/tp/pull/46), and updated the About Us page
      ([#35](https://github.com/AY2627S1-CS2113-T11-1/tp/pull/35)).
    * Introduced the DG's figure caption format (`*Figure N: Title*`) with its first two figures, for the
      other sections to continue ([#119](https://github.com/AY2627S1-CS2113-T11-1/tp/pull/119)).

* **Review/mentoring contributions**:
    * Reviewed a teammate's PPP, pointing out a broken About Us link and an overview claim that the summary
      did not support ([#87](https://github.com/AY2627S1-CS2113-T11-1/tp/pull/87#discussion_r4404460262)).

* **Contributions beyond the project team**:
    * Reviewed team CS2113-W14-2's DG against the UML reference sheet, flagging static calls drawn as instance
      calls, missing `{abstract}` labels, and a sequence diagram that contradicted the text
      ([#13](https://github.com/NUS-CS2113-AY2627-S1/tp/pull/13#discussion_r4408420442)), and reviewed a
      classmate's iP for Javadoc and naming quality
      ([#17](https://github.com/NUS-CS2113-AY2627-S1/ip/pull/17#discussion_r3920840119)).
