import {CustomField} from '@vaadin/custom-field';

class MapEntryField extends CustomField {

    static get is() {return 'map-entry-field'}

}

customElements.define(MapEntryField.is, MapEntryField);
