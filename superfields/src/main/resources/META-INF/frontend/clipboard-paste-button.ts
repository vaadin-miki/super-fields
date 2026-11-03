import {AbstractClickableButton} from "./abstract-clickable-button";

export class ClipboardPasteButton extends AbstractClickableButton {

  protected async clicked(event: Event): Promise<void> {
    console.log('clicked the copy button');
    try {
      const content = await navigator.clipboard.readText();
      this.dispatchEvent(new CustomEvent('button-pressed', {
        detail: {value: content},
        bubbles: true,
        composed: true
      }));
    } catch (e) {
      console.error('cannot paste from clipboard', e);
    }
  }

}

if (!customElements.get('clipboard-paste-button')) {
  customElements.define('clipboard-paste-button', ClipboardPasteButton);
}
