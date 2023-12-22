import {CustomFieldElement} from '@vaadin/vaadin-custom-field';

class HeaderFooterFieldWrapper extends CustomFieldElement {

    static get is() {return 'header-footer-field-wrapper'}

}

customElements.define(HeaderFooterFieldWrapper.is, HeaderFooterFieldWrapper);
