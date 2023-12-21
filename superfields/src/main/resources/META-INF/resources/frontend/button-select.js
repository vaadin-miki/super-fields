import {CustomField} from '@vaadin/custom-field';

class ButtonSelect extends CustomField {

    static get is() {return 'button-select'}

}

customElements.define(ButtonSelect.is, ButtonSelect);
