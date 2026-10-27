import {css} from 'lit';
export const CSS_LABEL_POSITIONS_GRIDS = css`
    :host([data-label-position-details~="side"]) [class|="vaadin"] .inputs-wrapper {
        flex: auto;
    }

    :host([data-label-position-details~="after"]) [class|="vaadin"] .inputs-wrapper {
        order: -1;
    }
`;
