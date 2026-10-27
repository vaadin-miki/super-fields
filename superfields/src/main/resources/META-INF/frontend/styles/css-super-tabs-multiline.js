import {css} from 'lit';

export const CSS_SUPER_TABS_MULTILINE = css`
    :host([theme="multi-line-tabs"]) [part="tabs"] {
        flex-wrap: wrap;
        --_lumo-tabs-overflow-mask-image: none !important;
    }

    :host([theme="multi-line-tabs"]) [part="forward-button"],
    :host([theme="multi-line-tabs"]) [part="back-button"] {
        display: none;
    }

`;

export const CSS_SUPER_TABS_STYLED_OVERFLOW_BUTTONS = css`
    super-tabs-header::part(back-button),
    super-tabs-header::part(forward-button) {
        color: var(--lumo-tertiary-text-color);
        background: var(--aura-background-color) padding-box;
        padding: var(--vaadin-padding-xs);
        height: auto;
        align-self: center;
        box-shadow: var(--aura-shadow-s);
        transition: opacity 120ms;
    }

    super-tabs-header::part(back-button):hover,
    super-tabs-header::part(forward-button):hover {
        color: inherit;
    }
`;