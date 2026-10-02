import {CustomField} from '@vaadin/custom-field';

class CollectionField extends CustomField {

    static get is() {return 'collection-field'}

}

customElements.define(CollectionField.is, CollectionField);
