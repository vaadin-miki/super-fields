import {TextFieldElement} from '@vaadin/vaadin-text-field/src/vaadin-text-field';
import {TextSelectionMixin} from "./text-selection-mixin";
import {TextGrowMixin} from "./text-grow-mixin";

class SuperTextField extends TextGrowMixin.to(TextSelectionMixin.to(TextFieldElement)) {

    static get is() {return 'super-text-field'}

    setCallingServer(callingServer) {
        console.log('STF: configuring event listeners; callingServer flag is '+callingServer);
        this.listenToEvents(this.inputElement, this, callingServer);
    }

    configureAutoGrow() {
        this.configureGrowingWithInput(this);
        this.setGrowWithInput(this, true);
    }

}

customElements.define(SuperTextField.is, SuperTextField);