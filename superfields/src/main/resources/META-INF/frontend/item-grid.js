import {CustomField} from '@vaadin/custom-field';

export class ItemGrid extends CustomField {

    static get is() {
        return 'item-grid';
    }

}

customElements.define(ItemGrid.is, ItemGrid);
