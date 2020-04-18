# Demo app

Demo application of the latest release can be found at https://superfields.herokuapp.com/.

Source code for the app and for the components can be found at https://github.com/vaadin-miki/super-fields/.

# SuperFields

All SuperFields are Java-friendly. There is as little client-side code as possible ;)

## Number fields

None of the number fields support range checking, so if you allow too many digits, overflows will occur.

### `SuperDoubleField`and `SuperBigDecimalField`

An input field for entering localised `Double` and `BigDecimal` numbers. Supports thousands (grouping) separators for the integer part and optional decimal separator.

### `SuperIntegerField` and `SuperLongField`

An input field for entering localised `Integer` and `Long` numbers. Supports thousands (grouping) separators.

## Date fields

### `SuperDatePicker` and `SuperDateTimePicker`

Fully localised `DatePicker` and `DateTimePicker` that fetch month names and weekday names from Java `Locale`. Those settings can be overwritten by resource bundle named `superdatepickeri18n`.

Both components behave funky when changing locale at runtime if their calendars were already shown. That is mostly due to some weird caching on the client side and is also a Vaadin bug. 

## Select fields

### `ItemGrid`

A highly configurable grid that allows single selection. Each cell in the grid corresponds to one item (in contrast to Vaadin `Grid`, which displays one item per row).

### `SuperTabs`

A customisable tabbed pane (something like `TabSheet` in the Vaadin 8 era) that also serves as a value component (current value corresponds to the selected tab).