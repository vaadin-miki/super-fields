import {TextField} from '@vaadin/text-field';
import {TextSelectionMixin} from "./text-selection-mixin";

class SuperTextField extends TextSelectionMixin.to(TextField) {

    static get is() {return 'super-text-field'}

    setCallingServer(callingServer) {
        console.log('STF: configuring event listeners; callingServer flag is '+callingServer);
        console.log('STF: this now refers to '+this);
        this.listenToEvents(this.inputElement, this, callingServer);
    }

    ensureValidText(e, webComponent, textInput) {
        if (webComponent.pattern !== undefined && webComponent.preventInvalidMixin !== undefined && webComponent.preventInvalidMixin.preventInvalidInput) {
            if (new RegExp(webComponent.pattern).test(textInput.value)) {
                webComponent.preventInvalidMixin.lastValue = textInput.value;
            }
            else {
                textInput.value = webComponent.preventInvalidMixin.lastValue;
                webComponent._markInputPrevented();
            }
        }
        else if (webComponent.preventInvalidMixin !== undefined) {
            webComponent.preventInvalidMixin.lastValue = textInput.value;
        }
    }

    preventInvalidInput(prevent) {
        console.log('STF: preventing invalid input set to ' + prevent);
        const listener = (e) => this.ensureValidText(e, this,  this.inputElement);
        let lastKnownValue = this.inputElement.value;
        if (this.preventInvalidMixin === undefined) {
            this.preventInvalidMixin = {
                lastValue: lastKnownValue,
                preventInvalidInput: prevent
            };
        }
        else if (!new RegExp(this.inputElement.pattern).test(lastKnownValue) && prevent) {
            this.preventInvalidMixin.lastValue = '';
            this.preventInvalidMixin.preventInvalidInput = true;
            this.inputElement.value = '';
        }
        else {
            this.preventInvalidMixin.lastValue = lastKnownValue;
            this.preventInvalidMixin.preventInvalidInput = prevent;
        }
        this.inputElement.addEventListener('input', listener);
    }

}

customElements.define(SuperTextField.is, SuperTextField);