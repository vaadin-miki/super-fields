import {CustomField} from '@vaadin/custom-field';
import {CSS_LABEL_POSITIONS} from "./styles/css-label-positions";
import {CSS_LABEL_POSITIONS_GRIDS} from "./styles/css-label-positions-grid";

class GridMultiSelect extends CustomField {

    static get is() {return 'grid-multi-select'}

    static get styles() {return [CSS_LABEL_POSITIONS, CSS_LABEL_POSITIONS_GRIDS]}

}

customElements.define(GridMultiSelect.is, GridMultiSelect);
