import {html, LitElement} from 'lit';

export class SuperLongField extends LitElement {

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

if (!customElements.get('super-long-field')) {
    customElements.define('super-long-field', SuperLongField);
}
