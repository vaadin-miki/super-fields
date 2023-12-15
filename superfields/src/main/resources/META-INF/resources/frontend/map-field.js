import {CustomField} from '@vaadin/custom-field';

class MapField extends CustomField {

    static get is() {return 'map-field'}

}

customElements.define(MapField.is, MapField);
