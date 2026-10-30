import {html, LitElement} from 'lit';

export class ClipboardPasteButton extends LitElement {

    constructor() {
        super();
    }

    render() {
        return html`
            <vaadin-icon icon="vaadin:paste" role="img" aria-label="Paste" @click=${this.clicked}></vaadin-icon>`;
    }

    async clicked(event: Event) {
        try {
            const content = await navigator.clipboard.readText();
            console.log('CPB ' + content);
            this.$server.clipboardPasted(content);
        } catch (e) {
            console.error('cannot paste from clipboard', e);
        }
    }

}

if (!customElements.get('clipboard-paste-button')) {
    customElements.define('clipboard-paste-button', ClipboardPasteButton);
}
