import {CustomFieldElement} from '@vaadin/vaadin-custom-field';

class ObjectField extends CustomFieldElement {

    static get is() {return 'object-field'}

}

customElements.define(ObjectField.is, ObjectField);
