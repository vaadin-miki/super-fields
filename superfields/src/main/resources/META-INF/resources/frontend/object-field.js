import {CustomField} from '@vaadin/custom-field';

class ObjectField extends CustomField {

    static get is() {return 'object-field'}

}

customElements.define(ObjectField.is, ObjectField);
