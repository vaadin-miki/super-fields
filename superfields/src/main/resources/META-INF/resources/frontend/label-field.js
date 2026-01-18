import {CustomField} from '@vaadin/custom-field';
import {CSS_LABEL_POSITIONS} from "./styles/css-label-positions";

class LabelField extends CustomField {

    static get is() {return 'label-field'}

    static get styles() {return [CSS_LABEL_POSITIONS]}

}

customElements.define(LabelField.is, LabelField);
