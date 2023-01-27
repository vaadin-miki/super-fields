import {html, LitElement} from 'lit';
import {customElement} from 'lit/decorators.js';

@customElement('super-double-field')
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