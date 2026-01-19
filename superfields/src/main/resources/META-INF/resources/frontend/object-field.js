import {CustomField} from '@vaadin/custom-field';
import {CSS_LABEL_POSITIONS} from "./styles/css-label-positions";

class ObjectField extends CustomField {

    static get is() {return 'object-field'}

    static get styles() {return [CSS_LABEL_POSITIONS]}

}

customElements.define(ObjectField.is, ObjectField);
