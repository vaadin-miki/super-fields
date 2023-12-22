import {CustomFieldElement} from '@vaadin/vaadin-custom-field';

export class ItemGrid extends CustomFieldElement {

    static get is() {
        return 'item-grid';
    }

}

customElements.define(ItemGrid.is, ItemGrid);
