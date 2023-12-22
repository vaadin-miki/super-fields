import {CustomFieldElement} from '@vaadin/vaadin-custom-field';

class ButtonMultiSelect extends CustomFieldElement {

    static get is() {return 'button-multi-select'}

}

customElements.define(ButtonMultiSelect.is, ButtonMultiSelect);
