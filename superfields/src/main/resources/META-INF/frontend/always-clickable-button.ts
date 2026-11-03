import {AbstractClickableButton} from "./abstract-clickable-button";

export class AlwaysClickableButton extends AbstractClickableButton {

  protected async clicked(event: Event): Promise<void> {
    this.dispatchEvent(new CustomEvent('button-pressed', {
      bubbles: true,
      composed: true
    }));
  }
}

if (!customElements.get('always-clickable-button')) {
  customElements.define('always-clickable-button', AlwaysClickableButton);
}
