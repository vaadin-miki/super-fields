import {CustomField} from '@vaadin/custom-field';
import {CSS_LABEL_POSITIONS} from "./styles/css-label-positions";

class VariantField extends CustomField {

    static get is() {return 'variant-field'}

    static get styles() {return [CSS_LABEL_POSITIONS]}

}

customElements.define(VariantField.is, VariantField);
