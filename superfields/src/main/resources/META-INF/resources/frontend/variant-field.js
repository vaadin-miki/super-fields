import {CustomField} from '@vaadin/custom-field';

class VariantField extends CustomField {

    static get is() {return 'variant-field'}

}

customElements.define(VariantField.is, VariantField);
