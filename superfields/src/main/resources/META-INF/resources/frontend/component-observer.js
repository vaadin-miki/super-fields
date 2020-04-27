import {html, PolymerElement} from '@polymer/polymer/polymer-element.js';

export class ComponentObserver extends PolymerElement {

    static get template() {
        return html``;
    }

    static get is() {
        return 'component-observer';
    }

    static get properties() {
        return {
            // Declare your properties here.

        };
    }
}

customElements.define(ComponentObserver.is, ComponentObserver);
