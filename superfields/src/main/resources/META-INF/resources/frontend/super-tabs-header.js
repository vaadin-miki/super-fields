import {Tabs} from '@vaadin/tabs';
import {tabsStyles} from '@vaadin/tabs/src/styles/vaadin-tabs-base-styles.js';
import {CSS_SUPER_TABS_MULTILINE, CSS_SUPER_TABS_STYLED_OVERFLOW_BUTTONS} from "./styles/css-super-tabs-multiline";

class SuperTabsHeader extends Tabs {

    static get is() {
        return 'super-tabs-header'
    }

    static get styles() {
        return [tabsStyles, CSS_SUPER_TABS_MULTILINE, CSS_SUPER_TABS_STYLED_OVERFLOW_BUTTONS]
    }

}

customElements.define(SuperTabsHeader.is, SuperTabsHeader);
