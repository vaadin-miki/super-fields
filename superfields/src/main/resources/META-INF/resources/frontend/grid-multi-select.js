import {CustomFieldElement} from '@vaadin/vaadin-custom-field';

class GridMultiSelect extends CustomFieldElement {

    static get is() {return 'grid-multi-select'}

}

customElements.define(GridMultiSelect.is, GridMultiSelect);
