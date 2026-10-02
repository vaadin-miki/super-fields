import {css} from 'lit';

export const CSS_TEXT_FIELD_WOBBLE = css`
    @keyframes wobble {
        0%, 100% {
            transform: translateX(0);
            color: inherit;
        }
        10% {
            transform: translateX(-6px);
            color: red;
        }
        60% {
            transform: translateX(12px);
            color: red;
        }
    }

    /* Apply to the input field part when input-prevented is present */

    :host([input-prevented]) vaadin-input-container ::slotted(input) {
        animation: wobble 1000ms ease-in-out;
        /* optional: make it more visible */
        will-change: transform;
    }
`;
