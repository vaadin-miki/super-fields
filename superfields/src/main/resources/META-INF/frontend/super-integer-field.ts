import {html, LitElement} from 'lit';

export class SuperIntegerField extends LitElement {

    render() {
        return html`
            <slot></slot>`;
    }

    /**
     * Fix a Javascript error when the long field is used inside a CustomField
     * (during the validation of the customfield)
     */
    checkValidity() {
        return true;
    }

}

if (!customElements.get('super-integer-field')) {
    customElements.define('super-integer-field', SuperIntegerField);
}
