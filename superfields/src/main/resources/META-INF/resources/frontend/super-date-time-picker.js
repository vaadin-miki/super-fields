import {DateTimePicker} from '@vaadin/date-time-picker';
import {DatePatternMixin} from "./date-pattern-mixin";

class SuperDateTimePicker extends DatePatternMixin.to(DateTimePicker) {

    static get is() { return 'super-date-time-picker'; }

    initPatternSetting(datepicker) {
        super.initPatternSetting(datepicker.querySelector('vaadin-date-time-picker-date-picker'));
    }

    setDisplayPattern(datepicker, displayPattern) {
        // this method may be called by inner method that changes locale when the pattern is present
        // in such case, the datepicker object is the actual date picker, not the date-time picker container
        // see https://github.com/vaadin-miki/super-fields/issues/260
        return super.setDisplayPattern(datepicker.tagName === 'VAADIN-DATE-TIME-PICKER-DATE-PICKER' ? datepicker : datepicker.querySelector('vaadin-date-time-picker-date-picker'), displayPattern);
    }

}
customElements.define(SuperDateTimePicker.is, SuperDateTimePicker);