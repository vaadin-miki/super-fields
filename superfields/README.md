# SuperFields

## Demo app

Demo application can be found in `demo-v14` module. Please consult it to see how to use the components (until this documentation is updated).

## Number fields

None of the number fields support range checking, so if you allow too many digits, overflows will occur.

### `SuperDoubleField`and `SuperBigDecimalField`

An input field for entering localised `Double` and `BigDecimal` numbers. Supports thousands (grouping) separators for the integer part and optional decimal separator.

### `SuperIntegerField` and `SuperLongField`

An input field for entering localised `Integer` and `Long` numbers. Supports thousands (grouping) separators.
