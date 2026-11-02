import {html, LitElement} from 'lit';
import '@vaadin/tooltip';

export class AbstractClickableButton extends LitElement {

  static properties = {
    text: {type: String},
  };

  text = '';

  render() {
    return html`
        <button type="button" @click=${this.clicked}>
            <slot name="icon"></slot>
            ${this.text}
        </button>
        <slot name="tooltip"></slot>
    `;
  }

  protected abstract clicked(event: Event): void | Promise<void>;

}
