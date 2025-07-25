// @ts-nocheck
window.Vaadin = window.Vaadin || {};
window.Vaadin.featureFlags = window.Vaadin.featureFlags || {};
if (Object.keys(window.Vaadin.featureFlags).length === 0) {
window.Vaadin.featureFlags.exampleFeatureFlag = false;
window.Vaadin.featureFlags.collaborationEngineBackend = false;
window.Vaadin.featureFlags.formFillerAddon = false;
window.Vaadin.featureFlags.fullstackSignals = false;
window.Vaadin.featureFlags.flowFullstackSignals = false;
window.Vaadin.featureFlags.copilotExperimentalFeatures = false;
window.Vaadin.featureFlags.masterDetailLayoutComponent = false;
window.Vaadin.featureFlags.react19 = false;
window.Vaadin.featureFlags.accessibleDisabledButtons = false;
window.Vaadin.featureFlags.layoutComponentImprovements = false;
window.Vaadin.featureFlags.defaultAutoResponsiveFormLayout = false;
};
if (window.Vaadin.featureFlagsUpdaters) { 
const activator = (id) => window.Vaadin.featureFlags[id] = true;
window.Vaadin.featureFlagsUpdaters.forEach(updater => updater(activator));
delete window.Vaadin.featureFlagsUpdaters;
} 
export {};