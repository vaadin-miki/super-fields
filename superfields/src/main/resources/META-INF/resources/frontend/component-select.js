import {CustomField} from '@vaadin/custom-field';

class ComponentSelect extends CustomField {

    static get is() {return 'component-select'}

}

customElements.define(ComponentSelect.is, ComponentSelect);
