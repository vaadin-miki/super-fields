import {DatePicker} from '@vaadin/date-picker';
import {DatePatternMixin} from "./date-pattern-mixin";
import {TextSelectionMixin} from "./text-selection-mixin";

class SuperDatePicker extends TextSelectionMixin.to(DatePatternMixin.to(DatePicker)) {

    static get is() {return 'super-date-picker'}

    setCallingServer(callingServer) {
        console.log('SDP: configuring text selection listeners; callingServer flag is '+callingServer);
        const version = customElements.get('vaadin-date-picker').version;
        if (version && parseInt(version.split('.')[0]) >= 23) {
            // date picker has changed since v14
            this.listenToEvents(this.querySelector('input[slot="input"]'), this, callingServer);
        }
        else {
            // old Vaadin versions
            this.listenToEvents(this.shadowRoot.querySelector('vaadin-date-picker-text-field').inputElement, this, callingServer);
        }
    }

}

customElements.define(SuperDatePicker.is, SuperDatePicker);