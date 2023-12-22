import {CustomFieldElement} from '@vaadin/vaadin-custom-field';

class ComponentMultiSelect extends CustomFieldElement {

    static get is() {return 'component-multi-select'}

}

customElements.define(ComponentMultiSelect.is, ComponentMultiSelect);
