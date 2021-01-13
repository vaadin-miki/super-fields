# 0.9.4 - Month name support for DatePattern
## New features and enhancements
* \#242 - [Add "month name" to supported date patterns](https://github.com/vaadin-miki/super-fields/issues/242)
## Changes to API
* \#242 - [Add "month name" to supported date patterns](https://github.com/vaadin-miki/super-fields/issues/242)
## Bug fixes
* \#87 - [Calling SuperDateTimePicker.setDatePattern when it is on an invisible layout results in "$connector undefined"](https://github.com/vaadin-miki/super-fields/issues/87)
* \#260 - [TypeError (datepicker is null) in SuperDateTimePicker](https://github.com/vaadin-miki/super-fields/issues/260)
# 0.9.3 - GridMultiSelect and ItemGrid row padding
## New features and enhancements
* \#239 - [GridMultiSelect](https://github.com/vaadin-miki/super-fields/issues/239)
* \#250 - [ItemGrid should pad rows with empty cells](https://github.com/vaadin-miki/super-fields/issues/250)
## Changes to API
* \#256 - [ItemGrid should allow fetching cells information by row and by column](https://github.com/vaadin-miki/super-fields/issues/256)
## Bug fixes
# 0.9.2 - Bugfixes to number fields
## New features and enhancements
(nothing reported)
## Changes to API
(nothing reported)
## Bug fixes
* \#241 - [SuperBigDecimalField loses validation information](https://github.com/vaadin-miki/super-fields/issues/241)
* \#243 - [Text selection mixin throws JS errors when used in Grid](https://github.com/vaadin-miki/super-fields/issues/243)
# 0.9.1 - Vaadin 14.4
## New features and enhancements
* \#224 - [Changing locale should preserve number precision information](https://github.com/vaadin-miki/super-fields/issues/224)
## Changes to API
* \#227 - [Upgrade Vaadin to 14.4 and add field helpers where required](https://github.com/vaadin-miki/super-fields/issues/227)
## Bug fixes
* \#222 - [GridSelect should not allow changing selection mode of the underlying grid](https://github.com/vaadin-miki/super-fields/issues/222)
* \#226 - [super-fields addon should not ship with a logging library](https://github.com/vaadin-miki/super-fields/issues/226)
# 0.9.0 - GridSelect
## New features and enhancements
* \#211 - [GridSelect](https://github.com/vaadin-miki/super-fields/issues/211)
* \#212 - [Date pickers should have an option to always accept short year as input](https://github.com/vaadin-miki/super-fields/issues/212)
## Changes to API
(nothing reported)
## Bug fixes
* \#206 - [Client-side listener for UnloadObserver is not cleared when navigating away](https://github.com/vaadin-miki/super-fields/issues/206)
* \#214 - [Demo app events are triggered multiple times](https://github.com/vaadin-miki/super-fields/issues/214)
* \#216 - [Year calculation in date pickers for short year is incorrect](https://github.com/vaadin-miki/super-fields/issues/216)
# 0.8.0 - MultiClickButton
## New features and enhancements
* \#160 - [Confirm-on-click button](https://github.com/vaadin-miki/super-fields/issues/160)
* \#201 - [Allow date pattern with no separator character](https://github.com/vaadin-miki/super-fields/issues/201)
## Changes to API
(nothing reported)
## Bug fixes
* \#199 - [Number fields causes NPE when null value is used](https://github.com/vaadin-miki/super-fields/issues/199)
# 0.7.3 - Null as default value in number fields
## New features and enhancements
(nothing reported)
## Changes to API
* \#189 - [Number fields constructors should allow null as default value](https://github.com/vaadin-miki/super-fields/issues/189)
## Bug fixes
* \#189 - [Number fields constructors should allow null as default value](https://github.com/vaadin-miki/super-fields/issues/189)
# 0.7.2 - UI-scoped UnloadObserver
## New features and enhancements
(nothing reported)
## Changes to API
* \#183 - [Introduce UnloadObserver.get(Component)](https://www.github.com/vaadin-miki/super-fields/issues/183)
## Bug fixes
(nothing reported)
# 0.7.1 - Fixes to UnloadObserver
## New features and enhancements
* \#170 - [UnloadObserver should always send an event before unloading](https://www.github.com/vaadin-miki/super-fields/issues/170)
## Changes to API
(nothing reported)
## Bug fixes
* \#178 - [UnloadObserver should belong to the UI that will be unloaded.](https://www.github.com/vaadin-miki/super-fields/issues/178)
# 0.7.0 - SuperTextField
## New features and enhancements
* \#122 - [SuperTextField, SuperTextArea and text selection API](https://www.github.com/vaadin-miki/super-fields/issues/122)
* \#123 - [Add text selection API to existing components](https://www.github.com/vaadin-miki/super-fields/issues/123)
* \#135 - [SuperTabs should have an option to wrap tabs](https://www.github.com/vaadin-miki/super-fields/issues/135)
* \#141 - [Server-side date formatting for DatePattern](https://www.github.com/vaadin-miki/super-fields/issues/141)
* \#152 - [Allow overriding default value in number fields](https://www.github.com/vaadin-miki/super-fields/issues/152)
## Changes to API
* \#123 - [Add text selection API to existing components](https://www.github.com/vaadin-miki/super-fields/issues/123)
* \#147 - [HasId marker interface](https://www.github.com/vaadin-miki/super-fields/issues/147)
* \#171 - [DatePattern should be moved to a shared package](https://github.com/vaadin-miki/super-fields/issues/171)
## Bug fixes
* \#132 - [No deployment to Heroku](https://www.github.com/vaadin-miki/super-fields/issues/132)
* \#136 - [setReadOnly has no effect on number fields](https://www.github.com/vaadin-miki/super-fields/issues/136)
* \#137 - [UnloadObserver.onDetach causes TypeError](https://www.github.com/vaadin-miki/super-fields/issues/137)
* \#146 - [SuperTabs are difficult to style](https://www.github.com/vaadin-miki/super-fields/issues/146)
* \#154 - [Number fields do not trigger focus/blur events](https://www.github.com/vaadin-miki/super-fields/issues/154)
# 0.6.2 - Vaadin 14.2 compatibility
## New features and enhancements
* \#124 - [Update Vaadin dependencies to 14.2](https://www.github.com/vaadin-miki/super-fields/issues/124)
## Changes to API
* \#127 - [WithIdMixin is missing from number fields](https://www.github.com/vaadin-miki/super-fields/issues/127)
## Bug fixes
* \#126 - [Number fields cannot be easily styled](https://www.github.com/vaadin-miki/super-fields/issues/126)
* \#127 - [WithIdMixin is missing from number fields](https://www.github.com/vaadin-miki/super-fields/issues/127)
# 0.6.1 - Release process improvement
## New features and enhancements
* \#107 - [Generate release notes from a milestone](https://www.github.com/vaadin-miki/super-fields/issues/107)
## Changes to API
(nothing reported)
## Bug fixes
* \#105 - [Removing date (time) picker and adding it back in the dom resets the display pattern](https://www.github.com/vaadin-miki/super-fields/issues/105)
# 0.6 - ComponentObserver and UnloadObserver
## New features and enhancements
* \#66 - [A field that changes value on becoming shown and hidden](https://www.github.com/vaadin-miki/super-fields/issues/66)
* \#69 - [ComponentObserver - reusing client-side IntersectionObserver as a server-side component](https://www.github.com/vaadin-miki/super-fields/issues/69)
* \#75 - [Component for encapsulating beforeunload events](https://www.github.com/vaadin-miki/super-fields/issues/75)
* \#80 - [Expand SuperTabs to work also with removing tab contents](https://www.github.com/vaadin-miki/super-fields/issues/80)
* \#82 - [Allow overriding default container in SuperTabs](https://www.github.com/vaadin-miki/super-fields/issues/82)
## Changes to API
* \#76 - [Make Vaadin dependencies not transitive](https://www.github.com/vaadin-miki/super-fields/issues/76)
* \#85 - [Add WithItems also to ItemGrid](https://www.github.com/vaadin-miki/super-fields/issues/85)
* \#86 - [Create WithValue to allow chaining setValue calls](https://www.github.com/vaadin-miki/super-fields/issues/86)
* \#89 - [Expose DatePicker and TimePicker in SuperDateTimePicker](https://www.github.com/vaadin-miki/super-fields/issues/89)
* \#92 - [Add WithIdMixin to all components](https://www.github.com/vaadin-miki/super-fields/issues/92)
## Bug fixes
* \#81 - [Setting date display pattern does not work out-of-the-box](https://www.github.com/vaadin-miki/super-fields/issues/81)
* \#83 - [SuperTabs should implement HasItems](https://www.github.com/vaadin-miki/super-fields/issues/83)
* \#90 - [SuperTabs should be using removing handler by default](https://www.github.com/vaadin-miki/super-fields/issues/90)
* \#93 - ["this.observer is undefined" after ObservedField is removed from dom and added again](https://www.github.com/vaadin-miki/super-fields/issues/93)
* \#98 - [Vaadin production mode not included](https://www.github.com/vaadin-miki/super-fields/issues/98)
# 0.5.1 - Fix maven dependencies
## New features and enhancements
(nothing reported)
## Changes to API
(nothing reported)
## Bug fixes
* \#70 - [Parent pom unavailable for Directory downloads](https://www.github.com/vaadin-miki/super-fields/issues/70)
# 0.5 - LazyLoad
## New features and enhancements
* \#50 - [Investigate lazy loading for ItemGrid](https://www.github.com/vaadin-miki/super-fields/issues/50)
* \#51 - [Custom date formatting for date components](https://www.github.com/vaadin-miki/super-fields/issues/51)
## Changes to API
(nothing reported)
## Bug fixes
* \#56 - [Set width to full in number fields](https://www.github.com/vaadin-miki/super-fields/issues/56)
# 0.4 - ItemGrid
## New features and enhancements
* \#34 - [Basic version of `ItemGrid`](https://www.github.com/vaadin-miki/super-fields/issues/34)
## Changes to API
(nothing reported)
## Bug fixes
(nothing reported)
# 0.3 - SuperTabs
## New features and enhancements
* \#26 - [SuperTabs](https://www.github.com/vaadin-miki/super-fields/issues/26)
## Changes to API
* \#30 - [Add .withXYZ methods to all fields](https://www.github.com/vaadin-miki/super-fields/issues/30)
## Bug fixes
(nothing reported)
# 0.2 - Date components
## New features and enhancements
* \#12 - [SuperDatePicker](https://www.github.com/vaadin-miki/super-fields/issues/12)
* \#20 - [SuperDateTimePicker](https://www.github.com/vaadin-miki/super-fields/issues/20)
## Changes to API
(nothing reported)
## Bug fixes
(nothing reported)
# 0.1 - Number fields
## New features and enhancements
* \#2 - [SuperDoubleField](https://www.github.com/vaadin-miki/super-fields/issues/2)
* \#4 - [SuperIntegerField and SuperLongField](https://www.github.com/vaadin-miki/super-fields/issues/4)
* \#5 - [SuperBigDecimalField](https://www.github.com/vaadin-miki/super-fields/issues/5)
## Changes to API
(nothing reported)
## Bug fixes
* \#10 - [Max integer length does not work if it is a multiplication of grouping size](https://www.github.com/vaadin-miki/super-fields/issues/10)