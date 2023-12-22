import {html, LitElement} from 'lit-element';
import {customElement} from 'lit-element/lib/decorators.js';

@customElement('super-big-decimal-field')
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