import {TextArea} from '@vaadin/text-area';
import {TextSelectionMixin} from "./text-selection-mixin";
import {CSS_LABEL_POSITIONS} from "./styles/css-label-positions";

class SuperTextArea extends TextSelectionMixin.to(TextArea) {

    static get is() {return 'super-text-area'}

    static get styles() { return [CSS_LABEL_POSITIONS]; }

    setCallingServer(callingServer) {
        console.log('STA: configuring event listeners; callingServer flag is '+callingServer);
        this.listenToEvents(this.inputElement, this, callingServer);
    }

}

customElements.define(SuperTextArea.is, SuperTextArea);