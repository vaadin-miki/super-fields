import {html, LitElement} from 'lit';

export class ClipboardCopyButton extends LitElement {

  render() {
    return html`
        <vaadin-icon icon="vaadin:copy" role="img" aria-label="Copy" @click=${this.clicked}></vaadin-icon>`;
  }

  async clicked(event: Event) {
    const selection = window.getSelection()?.toString() ?? '';
    const modified = await this.$server.processText(selection);
    try {
      await navigator.clipboard.writeText(modified);
      this.dispatchEvent(new CustomEvent('copy-pressed', {
        detail: {value: modified, raw: selection},
        bubbles: true,
        composed: true
      }));
    } catch (e) {
      console.error("cannot write to clipboard", e);
    }
  }
}

if (!customElements.get('clipboard-copy-button')) {
  customElements.define('clipboard-copy-button', ClipboardCopyButton);
}
