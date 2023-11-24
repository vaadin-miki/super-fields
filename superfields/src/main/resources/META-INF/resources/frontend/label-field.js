import {CustomField} from '@vaadin/custom-field';

class LabelField extends CustomField {

    static get is() {return 'label-field'}

}

customElements.define(LabelField.is, LabelField);
