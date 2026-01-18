import {CustomField} from '@vaadin/custom-field';
import {CSS_LABEL_POSITIONS} from "./styles/css-label-positions";
import {CSS_LABEL_POSITIONS_GRIDS} from "./styles/css-label-positions-grid";

class GridSelect extends CustomField {

    static get is() {return 'grid-select'}

    static get styles() {return [CSS_LABEL_POSITIONS, CSS_LABEL_POSITIONS_GRIDS]}

}

customElements.define(GridSelect.is, GridSelect);
