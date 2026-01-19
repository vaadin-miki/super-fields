import {CustomField} from '@vaadin/custom-field';
import {CSS_LABEL_POSITIONS} from "./styles/css-label-positions";

class MapField extends CustomField {

    static get is() {return 'map-field'}

    static get styles() {return [CSS_LABEL_POSITIONS]}

}

customElements.define(MapField.is, MapField);
