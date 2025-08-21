import {html, LitElement} from 'lit';
import {customElement} from 'lit/decorators.js';

@customElement('clipboard-paste-button')
export class ClipboardCopyButton extends LitElement {

    private targetComponent: HTMLElement;

    render() {
        return html`<vaadin-icon icon="vaadin:paste" role="img" aria-label="Paste""></vaadin-icon>`;
    }

    setTargetComponent(target: HTMLElement) {
        this.targetComponent = target;
        console.info("CPB: setting target to " + target);
    }

    clearTargetComponent() {
        this.targetComponent = null;
        console.info("CPB: clear target component");
    }

    pasteClipboardContents() {
        console.info("CPB: pasting contents from clipboard");
        const fromClipboard = navigator.clipboard.readText();
        if (this.targetComponent !== undefined) {
            console.log("CPB: setting value of target component");
            this.targetComponent.setValue(fromClipboard);
        }
        this.$server.clipboardPasted(fromClipboard);
    }

    getTargetComponent() {
        return this.targetComponent;
    }

}
