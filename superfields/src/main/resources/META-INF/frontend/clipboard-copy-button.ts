import {AbstractClickableButton} from "./abstract-clickable-button";

export class ClipboardCopyButton extends AbstractClickableButton {

  protected async clicked(event: Event): Promise<void> {
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
