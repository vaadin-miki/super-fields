import {ComponentObserver} from "./component-observer";

export class ObservedField extends ComponentObserver {
    static get is() {
        return 'observed-field';
    }
}

customElements.define(ObservedField.is, ObservedField);
