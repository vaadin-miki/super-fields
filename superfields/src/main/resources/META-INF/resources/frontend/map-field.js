import {CustomFieldElement} from '@vaadin/vaadin-custom-field';

class MapField extends CustomFieldElement {

    static get is() {return 'map-field'}

}

customElements.define(MapField.is, MapField);
