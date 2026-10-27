import {html, LitElement} from 'lit';

export class SuperBigDecimalField extends LitElement {

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

if (!customElements.get('super-big-decimal-field')) {
    customElements.define('super-big-decimal-field', SuperBigDecimalField);
}
