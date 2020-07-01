import {TextAreaElement} from '@vaadin/vaadin-text-field/src/vaadin-text-area';
import {TextSelectionMixin} from "./text-selection-mixin";

class SuperTextArea extends TextSelectionMixin.to(TextAreaElement) {

    static get is() {return 'super-text-area'}

    setCallingServer(callingServer) {
        console.log('STA: configuring event listeners; callingServer flag is '+callingServer);
        this.listenToEvents(this.inputElement, this, callingServer);
    }

}

customElements.define(SuperTextArea.is, SuperTextArea);