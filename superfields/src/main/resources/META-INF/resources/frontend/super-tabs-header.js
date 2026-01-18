import {Tabs} from "@vaadin/tabs"
import { tabsStyles } from '@vaadin/tabs/src/styles/vaadin-tabs-base-styles.js';
import {CSS_SUPER_TABS_MULTILINE} from "./styles/css-super-tabs-multiline";

class SuperTabsHeader extends Tabs {

    static get is() {return 'super-tabs-header'}

    static get styles() {return [tabsStyles, CSS_SUPER_TABS_MULTILINE]}

}

customElements.define(SuperTabsHeader.is, SuperTabsHeader);
