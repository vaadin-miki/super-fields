import {CustomFieldElement} from '@vaadin/vaadin-custom-field';

class VariantField extends CustomFieldElement {

    static get is() {return 'variant-field'}

}

customElements.define(VariantField.is, VariantField);
