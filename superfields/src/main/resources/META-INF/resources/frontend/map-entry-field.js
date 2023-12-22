import {CustomFieldElement} from '@vaadin/vaadin-custom-field';

class MapEntryField extends CustomFieldElement {

    static get is() {return 'map-entry-field'}

}

customElements.define(MapEntryField.is, MapEntryField);
