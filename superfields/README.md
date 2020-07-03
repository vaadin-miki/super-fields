# Demo app

Demo application of the latest release can be found at https://superfields.herokuapp.com/.

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

## Number fields

None of the number fields support range checking, so if you allow too many digits, overflows will occur.

All number fields support text selection API.

### `SuperDoubleField` and `SuperBigDecimalField`

An input field for entering localised `Double` and `BigDecimal` numbers. Supports thousands (grouping) separators for the integer part and optional decimal separator.

### `SuperIntegerField` and `SuperLongField`

An input field for entering localised `Integer` and `Long` numbers. Supports thousands (grouping) separators.

## Text fields

### `SuperTextField` and `SuperTextArea`

These are the same components as their Vaadin counterparts, except they fully support text selection API. This means that the text contained in the components can be selected from server-side code and that changes to selection in the browser are sent to the server as events.

## Date fields

### `SuperDatePicker` and `SuperDateTimePicker`

Fully localised `DatePicker` and `DateTimePicker` that fetch month names and weekday names from Java `Locale`. Those settings can be overwritten by resource bundle named `superdatepickeri18n`.

In addition to the above, both components allow setting custom date display pattern. This pattern should survive setting locale or i18n object, so keep that in mind.

Both components behave funky when changing locale at runtime if their calendars were already shown. That is mostly due to some weird caching on the client side and is also a Vaadin bug.

**Please note:** only `SuperDatePicker` has support for text selection API. 

## Select fields

### `ItemGrid`

A highly configurable grid that allows single selection. Each cell in the grid corresponds to one item (in contrast to Vaadin `Grid`, which displays one item per row).

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

### `UnloadObserver`

A component that listens and reacts to browser's `beforeunload` events that happen for example when browser window/tab is closed. The support [varies between browsers](https://developer.mozilla.org/en-US/docs/Web/API/Window/beforeunload_event), but in general is quite good. This should work with at least the major browsers.

**Please note**: This component is basically a UI-scoped singleton - there can be only one instance of it per active UI. As such, please refrain from adding the same instance into multiple layouts.

The code is based on solution [posted by Kaspar Scherrer and Stuart Robinson](https://vaadin.com/forum/thread/17523194/unsaved-changes-detect-page-exit-or-reload) (with invaluable feedback from Jean-François Lamy). It does not work with `<a href>` or `Anchor` as download links, so please use [FileDownloadWrapper](https://vaadin.com/directory/component/file-download-wrapper/discussions) for that.
