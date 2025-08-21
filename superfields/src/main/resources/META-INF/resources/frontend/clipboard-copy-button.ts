import {html, LitElement} from 'lit';
import {customElement} from 'lit/decorators.js';

@customElement('clipboard-copy-button')
export class ClipboardCopyButton extends LitElement {

    render() {
        return html`<vaadin-icon icon="vaadin:copy" role="img" aria-label="Copy"></vaadin-icon>`;
    }

    setSourceComponent(source: Element) {
        console.info("CCB: setting source to " + source);
    }

    clearSourceComponent() {
        console.info("CCB: clear source component");
    }

}
