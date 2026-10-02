import {CustomField} from '@vaadin/custom-field';

class ComponentMultiSelect extends CustomField {

    static get is() {return 'component-multi-select'}

}

customElements.define(ComponentMultiSelect.is, ComponentMultiSelect);
