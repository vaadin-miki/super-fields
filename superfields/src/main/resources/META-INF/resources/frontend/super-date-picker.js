import {DatePickerElement} from '@vaadin/vaadin-date-picker/src/vaadin-date-picker.js';
import {DatePatternMixin} from "./date-pattern-mixin";
import {TextSelectionMixin} from "./text-selection-mixin";

class SuperDatePicker extends TextSelectionMixin.to(DatePatternMixin.to(DatePickerElement)) {

    static get is() {return 'super-date-picker'}

    setCallingServer(callingServer) {
        console.log('SDP: configuring text selection listeners; callingServer flag is '+callingServer);
        this.listenToEvents(this.shadowRoot.querySelector('vaadin-date-picker-text-field').inputElement, this, callingServer);
    }

}

customElements.define(SuperDatePicker.is, SuperDatePicker);