import {CustomFieldElement} from '@vaadin/vaadin-custom-field';

class ButtonSelect extends CustomFieldElement {

    static get is() {return 'button-select'}

}

customElements.define(ButtonSelect.is, ButtonSelect);
