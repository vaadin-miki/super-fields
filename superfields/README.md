# Demo app

Demo application of the latest release can be found at https://demo.unforgiven.pl/superfields/.

Source code for the app and for the components can be found at https://github.com/vaadin-miki/super-fields/.

# SuperFields

All SuperFields are Java-friendly. There is as little client-side code as possible ;) In addition to that, any client-side code is considered **implementation detail** and may change at any time.

#### Fluid API

All SuperFields feature fluid API (or method chaining), at least to some extent. For example, instead of doing:

```
  SuperDatePicker datePicker = new SuperDatePicker();
  datePicker.setId("super-date-picker");
  datePicker.setValue(LocalDate.today());
```

you can do a magic one-liner: `new SuperDatePicker().withId("super-date-picker").withValue(LocalDate.today())`.

#### Text selection

Some components contain server-side methods to control text selection in their web component part. In addition, they can optionally listen to text-selection events happening in the browser and broadcast them to the server.

The web components listen to each key press and mouse click. If text selection changes as a result of that action, they send an event to the server-side component. This may happen quite often and increase server load, so the feature is turned off by default. To turn it on simply call `setReceivingSelectionEventsFromClient(true)` (or `withReceivingSelectionEventsFromClient(true)`).  

#### Label position

Most of the components that can have a label allow positioning said label through `setLabelPosition(...)` (or `withLabelPosition(...)`). This should work in most layouts, with `FormLayout` being an obvious exception. Not all components support all possible values of `LabelPosition` (`SuperCheckbox` ignores vertical alignment). In addition, things might work weird when using right-to-left languages - if that happens, please report the problem in GitHub.

#### Log messages

Quite a few components log their state using [SLF4J](https://www.slf4j.org). Critical information is logged as error or warning, debugging messages are, well, debug or trace. Information about [how to configure which log messages get displayed can be found e.g. on Stack Overflow](https://stackoverflow.com/questions/45997759/how-to-change-slf4j-logging-level).

## Number fields

None of the number fields support range checking, so if you allow too many digits, overflows will occur.

All number fields support text selection API. They also should show a numeric keyboard on mobile devices.

All number fields support alternative characters that serve as grouping (thousand) separator, negative sign and (if applicable) the decimal separator. By default, the alternatives may not overlap with the symbols provided by the current `Locale`. Furthermore, some of those alternative symbols can be prohibited from being typed with a keyboard.

All number fields implement `HasInvalidInputPrevention`. Turning input prevention on will disallow entering any text into the field that is not a properly formatted number (similar to `setPreventInvalidInput(true)` in Vaadin 14). Input prevention is turned off by default.

### `SuperDoubleField` and `SuperBigDecimalField`

An input field for entering localised `Double` and `BigDecimal` numbers. Supports thousands (grouping) separators for the integer part and optional decimal separator.

Both fields allow the integer part of the number to be optional (thus omitted and defaulted to zero - `.27` parsed as `0.27`). This feature is turned off by default (meaning the integer part is required).

In addition to that `SuperBigDecimalField` supports (optionally) entering numbers with scientific notation, e.g. `12.34e-1`.

### `SuperIntegerField` and `SuperLongField`

An input field for entering localised `Integer` and `Long` numbers. Supports thousands (grouping) separators.

## Text fields

### `SuperTextField` and `SuperTextArea`

These are the same components as their Vaadin counterparts, except they fully support text selection API. This means that the text contained in the components can be selected from server-side code and that changes to selection in the browser are sent to the server as events.

In addition to that, both components allow server-side initiated text change at caret position (or any selected range).

`SuperTextField` implements `HasTextInputMode` which allows customising its on-screen keyboard on devices that support it.

`SuperTextField` implements `HasInvalidInputPrevention` which, when enabled, disallows entering any value that does not match the specified pattern. Note that enabling this feature without specifying a valid pattern has no effect.  

## Date fields

### `SuperDatePicker` and `SuperDateTimePicker`

Fully localised `DatePicker` and `DateTimePicker` that fetch month names and weekday names from Java `Locale`. Those settings can be overwritten by resource bundle named `superdatepickeri18n`.

In addition to the above, both components allow setting custom date display pattern. This pattern should survive setting locale or i18n object, so keep that in mind.

**Please note:** Vaadin's `DatePicker` and `DateTimePicker` allow setting custom date display patterns since version 14.8. It is achieved through `DatePickerI18n.setDateFormat`. That method executes in `SuperDatePickerI18n`, but it logs a warning message. If you intend to set formatting through i18n, please use default Vaadin's components.

Both components behave funky when changing locale at runtime if their calendars were already shown. That is mostly due to some weird caching on the client side and is also a Vaadin bug.

**Please note:** only `SuperDatePicker` has support for text selection API.

## `CollectionField`, `MapField` and related helpers

### `CollectionField`

Almost out-of-the-box, fully configurable `CustomField` for data of type `List<X>` (or `Set<X>`). Supports custom layouts, fields, removing individual or all elements and adding at specified indices.

To help navigate around functional interfaces `CollectionComponentProviders` contains some common use cases, like buttons for removing, clearing or adding elements. Please check the demo app (`CollectionFieldProvider`) for an example.

### `HasIndex` and `IndexedButton`

While intended to work with `CollectionField`, here is a general interface for anything that needs an index (an integer number). `IndexedButton` does nothing by itself, but it is a `Button` that implements `HasIndex`. It is used e.g. to represent a button that removes an element from a collection.

### `MapField` and `MapEntryField`

`MapField` is a customisable `CustomField` for data of type `Map<K, V>`. It is based on `CollectionField` and works with any field capable of displaying value of type `Map.Entry<K, V>`. 

`MapEntryField` is a component that is a `CustomField` for `Map.Entry<K, V>`. It allows custom layout and dedicated components for keys and values of the map.

## `HasHeader`, `HasFooter` and layout helpers

A three-part layout, with header, content and footer, is quite common in UX design. Two helper classes are offered:
* `HeaderFooterFieldWrapper` - a fully functional `CustomField` that has a header and a footer, and the content is just the field;
* `HeaderFooterLayoutWrapper` - a fully functional layout (`HasComponents`) that has a header and a footer, and the content is a layout to which `HasComponents` method calls are delegated.

Both wrappers attempt to be API-transparent and can replace already existing layouts or fields (for example, replacing a `SuperTextField` with a `HeaderFooterFieldWrapper` should just result in a header and footer around the text field, and no other changes should be needed).

## Other fields

### `LabelField`

This is an always read-only field. Basically, it is a `CustomField` that uses a `Text` to display `String` representation of the value. It is possible to customise how that representation is created and what text to show when the value is `null`. Note that the *readonlyness* of this component applies only to the UI.

### `VariantField`

A `CustomField<Object>`. It checks the type of the value passed to it and attempts to display it using a known mapping to a field (e.g. by registering `SuperIntegerField` and `SuperDatePicker` it will be able to show integers and dates). No events or details - other than value change - are propagated or exposed from the inner field.

### `ObjectField`

A `CustomField<T>` capable of building components and matching them with object's properties. Once configured it is basically an automated form generator. This component is highly configurable and details on how to use it are present in [the project's wiki](https://github.com/vaadin-miki/super-fields/wiki).

In most common cases one would use `ObjectFieldFactory` to create and configure `ObjectField`, and then annotate data model class with the additional information. Please consult the demo application or the tests (`ObjectFieldTest`, `NestedObjectFieldTest` and `EnumObjectTest`) for details. Also please note that [`ObjectFieldFactory` will become a separate library](https://github.com/vaadin-miki/super-fields/issues/401) at some point in the future. 

### `SuperCheckbox`

It is known that [`Checkbox` does not support read-only mode](https://github.com/vaadin/web-components/issues/688). This component exists as a workaround and binds `enabled` and `readOnly` as one: setting the checkbox to read-only will disable it. 

## Select fields

### `ItemGrid`

A highly configurable grid that allows single selection. Each cell in the grid corresponds to one item (in contrast to Vaadin `Grid`, which displays one item per row).

### `GridSelect` and `GridMultiSelect`

A single- and multi-selection `Grid`s that are value components, meaning they broadcast value change events (rather than selection events). Both allow access to the underlying `Grid`, except changing selection mode.

`GridMultiSelect` operates on `Set` and has an option to limit the size of the selection.

### `Component(Multi)Select` (and `Button(Multi)Select`)

Single- and multi-selection components that show each option as an individual component that is a `ClickNotifier`, for example a button. `Button(Multi)Select` uses `Button`s and constructors that allow usage of styles or variants to show if a button is selected.

`ComponentMultiSelect` and `ButtonMultiSelect` operate on `Set` and have an option to limit the size of the selection.

`ComponentSelect` and `ButtonSelect` can optionally allow `null` value. Multi-selection versions do not accept `null` and use an empty set instead.

### `SuperTabs`

A customisable tabbed pane (something like `TabSheet` in the Vaadin 8 era) that also serves as a value component (current value corresponds to the selected tab).

## Components that rely on [Intersection Observer API](https://developer.mozilla.org/en-US/docs/Web/API/Intersection_Observer_API)

**Please note:** the underlying API is *experimental*. It means its support in browsers may vary. For example, it is **not** supported by Internet Explorer and mobile browsers. Please [check browser support](https://caniuse.com/#feat=mdn-api_intersectionobserver) before using these components.

**Please also note:** apparently the API does not handle style-controlled visibility (at least in Firefox), so if the component gets hidden through CSS `display: none`, related events may not trigger. For resizing browser windows, scrolling and adding components into DOM things seem to work pretty ok.

**Finally please note:** I did my best to keep the components Java-friendly, but it may happen that their client state gets out of sync. If that happens, please file a bug report.

### `ComponentObserver`

A wrapper for one instance of client-side `IntersectionObserver`. It allows observing changes that happen to other components.

### `LazyLoad`

A simple wrapper to lazy load contents when the component gets into view.

### `ObservedField`

A boolean field that changes its value (`true` or `false`) depending on whether it is currently shown on the screen or not. 

## Other components

### `MultiClickButton`

A button that needs to be clicked many times before it notifies its click listeners. Each transition is stored as a state with text, icon, class names and theme names. 

### `UnloadObserver`

A component that listens and reacts to browser's `beforeunload` events that happen for example when browser window/tab is closed. The support [varies between browsers](https://developer.mozilla.org/en-US/docs/Web/API/Window/beforeunload_event), but in general is quite good. This should work with at least the major browsers.

**Please note**: This component is basically a UI-scoped singleton - there can be only one instance of it per active UI. As such, please refrain from adding the same instance into multiple layouts.

The code is based on solution [posted by Kaspar Scherrer and Stuart Robinson](https://vaadin.com/forum/thread/17523194/unsaved-changes-detect-page-exit-or-reload) (with invaluable feedback from Jean-François Lamy). It does not work with `<a href>` or `Anchor` as download links, so please use [FileDownloadWrapper](https://vaadin.com/directory/component/file-download-wrapper/discussions) for that.

### `ContentAware`

This is an extension of a `Div` that is aware (through [mutation observers](https://developer.mozilla.org/en-US/docs/Web/API/MutationObserver)) of changes happening to any of its contents (including deeply nested elements).

**Please note:** Currently Vaadin does not allow passing client-side elements to the server-side code, so there is no way to figure out which component was added or removed. Until a reasonable workaround is designed, the current behaviour has to suffice.

**Please also note:** Changes to the attributes in the contained components are ignored, even though it would be possible to notify the server about such changes. If that ever becomes needed, please submit an improvement ticket.
