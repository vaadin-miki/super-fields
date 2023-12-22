import {CustomFieldElement} from '@vaadin/vaadin-custom-field';

class LabelField extends CustomFieldElement {

    static get is() {return 'label-field'}

}

customElements.define(LabelField.is, LabelField);
