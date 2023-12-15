import {CustomField} from '@vaadin/custom-field';

class GridMultiSelect extends CustomField {

    static get is() {return 'grid-multi-select'}

}

customElements.define(GridMultiSelect.is, GridMultiSelect);
