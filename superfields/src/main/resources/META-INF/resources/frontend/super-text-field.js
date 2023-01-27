import {TextField} from '@vaadin/text-field';
import {TextSelectionMixin} from "./text-selection-mixin";

class SuperTextField extends TextSelectionMixin.to(TextField) {

    static get is() {return 'super-text-field'}

    setCallingServer(callingServer) {
        console.log('STF: configuring event listeners; callingServer flag is '+callingServer);
        console.log('STF: this now refers to '+this);
        this.listenToEvents(this.inputElement, this, callingServer);
    }

}

customElements.define(SuperTextField.is, SuperTextField);