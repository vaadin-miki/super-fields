import {html} from '@polymer/polymer/polymer-element.js';
import {ComponentObserver} from "./component-observer";

export class ObservedField extends ComponentObserver {
    static get template() {
        return html``;
    }

    static get is() {
        return 'observed-field';
    }
}

customElements.define(ObservedField.is, ObservedField);
