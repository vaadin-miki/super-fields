import {CustomField} from '@vaadin/custom-field';

class ButtonMultiSelect extends CustomField {

    static get is() {return 'button-multi-select'}

}

customElements.define(ButtonMultiSelect.is, ButtonMultiSelect);
