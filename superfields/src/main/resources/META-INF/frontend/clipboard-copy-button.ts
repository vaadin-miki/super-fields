import {html, LitElement} from 'lit';
import '@vaadin/tooltip';

export class ClipboardCopyButton extends LitElement {

  static properties = {
    text: {type: String},
  };

  text = '';

  render() {
    return html`
        <button type="button" class="clipboard-copy-button" @click=${this.clicked}>
            <slot name="icon"></slot>
            ${this.text}
        </button>
        <slot name="tooltip"></slot>
    `;
  }

  async clicked(event: Event) {
    const selection = window.getSelection()?.toString() ?? '';
    const modified = await this.$server.processText(selection);
    try {
      await navigator.clipboard.writeText(modified);
      this.dispatchEvent(new CustomEvent('button-pressed', {
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
