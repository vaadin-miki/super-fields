import {css} from 'lit';

// fixes #589; in v25 the styling got reworked and apparently full size is tied to actual tag name
// naturally, super-text-area is different that vaadin-text-area and thus full size stopped working
// the css below should fix the problem
export const CSS_SUPER_TEXT_AREA_SIZEFULL = css`
    :host([data-width-full]) vaadin-input-container ::slotted(textarea) {
        width: 100%;
    }

    :host([data-height-full]) vaadin-input-container ::slotted(textarea) {
        height: 100%;
    }
`;
