import {CustomFieldElement} from '@vaadin/vaadin-custom-field';

class CollectionField extends CustomFieldElement {

    static get is() {return 'collection-field'}

}

customElements.define(CollectionField.is, CollectionField);
