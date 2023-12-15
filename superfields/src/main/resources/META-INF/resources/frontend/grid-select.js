import {CustomField} from '@vaadin/custom-field';

class GridSelect extends CustomField {

    static get is() {return 'grid-select'}

}

customElements.define(GridSelect.is, GridSelect);
