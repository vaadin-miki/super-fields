import {html, css, LitElement} from 'lit';
import '@vaadin/tooltip';

export abstract class AbstractClickableButton extends LitElement {

  static properties = {
    text: {type: String},
  };

  static styles = css`
      :host {
          display: block;
      }

      button {
          box-sizing: border-box;
          width: 100%;
          height: 100%;
      }
  `;

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
