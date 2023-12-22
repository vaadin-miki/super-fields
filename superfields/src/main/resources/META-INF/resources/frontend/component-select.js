import {CustomFieldElement} from '@vaadin/vaadin-custom-field';

class ComponentSelect extends CustomFieldElement {

    static get is() {return 'component-select'}

}

customElements.define(ComponentSelect.is, ComponentSelect);
