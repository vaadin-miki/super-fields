import {DateTimePicker} from '@vaadin/vaadin-date-time-picker/src/vaadin-date-time-picker.js';
import {DatePatternMixin} from "./date-pattern-mixin";

class SuperDateTimePicker extends DatePatternMixin.to(DateTimePicker) {

    static get is() { return 'super-date-time-picker'; }

    initPatternSetting(datepicker) {
        super.initPatternSetting(datepicker.querySelector('vaadin-date-time-picker-date-picker'));
    }

    setDisplayPattern(datepicker, displayPattern) {
        return super.setDisplayPattern(datepicker.querySelector('vaadin-date-time-picker-date-picker'), displayPattern);
    }

}
customElements.define(SuperDateTimePicker.is, SuperDateTimePicker);