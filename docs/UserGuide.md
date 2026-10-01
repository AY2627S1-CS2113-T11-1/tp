# User Guide

## Introduction

{Give a product intro}

## Quick Start

{Give steps to get started quickly}

1. Ensure that you have Java 25 or above installed.
1. Down the latest version of `Duke` from [here](http://link.to/duke).

## Features

{Give detailed description of each feature}

### Adding a todo: `todo`
Adds a new item to the list of todo items.

Format: `todo n/TODO_NAME d/DEADLINE`

* The `DEADLINE` can be in a natural language format.
* The `TODO_NAME` cannot contain punctuation.  

Example of usage: 

`todo n/Write the rest of the User Guide d/next week`

`todo n/Refactor the User Guide to remove passive voice d/13/04/2020`

### Adding an expense: `expense add`

Records an operating expense.

Format: `expense add p/PRODUCT a/AMOUNT`

The amount must be an integer or floating-point number in dollars.

Example:

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

{Give a 'cheat sheet' of commands here}

* Add todo `todo n/TODO_NAME d/DEADLINE`
* Add expense `expense add p/PRODUCT a/AMOUNT`
* List expenses `expense list`
