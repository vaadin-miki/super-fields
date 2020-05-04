import {DatePickerElement} from '@vaadin/vaadin-date-picker/src/vaadin-date-picker.js';
import {DatePatternMixin} from "./date-pattern-mixin";

class SuperDatePicker extends DatePatternMixin.to(DatePickerElement) {

    static get is() {return 'super-date-picker'}

}

customElements.define(SuperDatePicker.is, SuperDatePicker);