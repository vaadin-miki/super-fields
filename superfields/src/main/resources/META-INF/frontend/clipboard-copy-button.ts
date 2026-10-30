import {html, LitElement} from 'lit';

export class ClipboardCopyButton extends LitElement {

    render() {
        return html`
            <vaadin-icon icon="vaadin:copy" role="img" aria-label="Copy" @click=${this.clicked}></vaadin-icon>`;
    }

    async clicked(event: Event) {
        const selection = window.getSelection()?.toString() ?? '';
        if (!selection) {
            console.log('CCB: nothing selected, so nothing to copy');
        } else {
            try {
                await navigator.clipboard.writeText(selection);
                this.$server.clipboardCopied(selection);
            } catch (e) {
                console.error("cannot write to clipboard", e);
            }
        }
    }
}

if (!customElements.get('clipboard-copy-button')) {
    customElements.define('clipboard-copy-button', ClipboardCopyButton);
}
