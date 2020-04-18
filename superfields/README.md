# SuperFields

## Demo app

Demo application can be found in `demo-v14` module. Please consult it to see how to use the components (until this documentation is updated).

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