import {CustomField} from '@vaadin/custom-field';

class SuperTabs extends CustomField {
    static get is() {return 'super-tabs'}
}

customElements.define(SuperTabs.is, SuperTabs);
