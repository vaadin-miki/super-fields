import {TextField} from '@vaadin/text-field';
import {TextSelectionMixin} from "./text-selection-mixin";
import {CSS_LABEL_POSITIONS} from "./styles/css-label-positions";

class SuperTextField extends TextSelectionMixin.to(TextField) {

    static get is() {return 'super-text-field'}

    static get styles() {return [CSS_LABEL_POSITIONS]}

    setCallingServer(callingServer) {
        console.log('STF: configuring event listeners; callingServer flag is '+callingServer);
        console.log('STF: this now refers to '+this);
        this.listenToEvents(this.inputElement, this, callingServer);
    }

    ensureValidText(e, webComponent, textInput) {
        if (webComponent.preventInvalidMixin === undefined) {
            // simply do nothing
        } else if (webComponent.pattern === undefined || (new RegExp(webComponent.pattern).test(textInput.value))) {
            webComponent.preventInvalidMixin.lastValue = textInput.value;
        } else {
            // this triggers when neither of the above is true (so preventing is on, pattern is defined and the check failed
            webComponent.value = webComponent.preventInvalidMixin.lastValue;
            webComponent._markInputPrevented();
        }
    }

    preventInvalidInput(prevent) {
        console.log('STF: preventing invalid input set to ' + prevent);
        const listener = (e) => this.ensureValidText(e, this,  this.inputElement);
        let lastKnownValue = this.inputElement.value;

        // no data present (i.e. was not preventing) and now will be preventing
        if (this.preventInvalidMixin === undefined && prevent) {
            if (!new RegExp(this.inputElement.pattern).test(lastKnownValue)) {
                this.inputElement.value = '';
                this.preventInvalidMixin = {
                    lastValue: '',
                    eventListener: listener
                };
            } else {
                this.preventInvalidMixin = {
                    lastValue: lastKnownValue,
                    eventListener: listener
                };
            }
            this.inputElement.addEventListener('input', listener);
        }
        // data is present (i.e. was preventing) and now will not be preventing
        else if (this.preventInvalidMixin !== undefined && !prevent) {
            this.inputElement.removeEventListener('input', this.preventInvalidMixin.eventListener);
            delete this.preventInvalidMixin;
        }
    }

}

customElements.define(SuperTextField.is, SuperTextField);