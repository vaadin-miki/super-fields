import {CustomField} from '@vaadin/custom-field';

class HeaderFooterFieldWrapper extends CustomField {

    static get is() {return 'header-footer-field-wrapper'}

}

customElements.define(HeaderFooterFieldWrapper.is, HeaderFooterFieldWrapper);
