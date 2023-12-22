import {CustomFieldElement} from '@vaadin/vaadin-custom-field';

class GridSelect extends CustomFieldElement {

    static get is() {return 'grid-select'}

}

customElements.define(GridSelect.is, GridSelect);
