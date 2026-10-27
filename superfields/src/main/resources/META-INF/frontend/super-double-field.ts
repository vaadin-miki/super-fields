import {html, LitElement} from 'lit';

export class SuperDoubleField extends LitElement {

    render() {
        return html`<slot></slot>`;
    }

    /**
     * Fix a Javascript error when the long field is used inside a CustomField
     * (during the validation of the customfield)
     */
    checkValidity() {
        return true;
    }

}

if (!customElements.get('super-double-field')) {
    customElements.define('super-double-field', SuperDoubleField);
}
