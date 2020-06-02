import {html, PolymerElement} from '@polymer/polymer/polymer-element.js';

/**
 * A web component that listens to {@code beforeunload} events and sends them to server-side.
 * This requires Flow and a corresponding server-side Java component to work properly.
 * Alternatively, make sure that this.$server.unloadAttempted() is available.
 * Based on the code written by Kaspar Scherrer and Stuart Robinson: https://vaadin.com/forum/thread/17523194/unsaved-changes-detect-page-exit-or-reload
 */
export class UnloadObserver extends PolymerElement {

    static get template() {
        return html``;
    }

    static get is() {
        return 'unload-observer';
    }

    /**
     * Initialises the observer and registers a beforeunload listener.
     */
    initObserver() {
        const src = this;
        if (window.Vaadin.unloadObserver === undefined) {
            window.Vaadin.unloadObserver = {
                handler: undefined
            }
        }
        if (window.Vaadin.unloadObserver.handler !== undefined) {
            window.removeEventListener('beforeunload', window.Vaadin.unloadObserver.handler);
        }
        window.Vaadin.unloadObserver.handler = event => src.unloadHappened(src, event);
        window.addEventListener('beforeunload', window.Vaadin.unloadObserver.handler);
    }

    /**
     * Invoked in response to beforeunload browser event.
     * @param source An unload observer.
     * @param event Event that happened.
     */
    unloadHappened(source, event) {
        if (window.Vaadin.unloadObserver.query) {
            event.preventDefault();
            event.returnValue = '';
            if (source.$server) {
                source.$server.unloadAttempted();
            }
        }
    }

    /**
     * Controls whether or not prevent unload events.
     * @param value When {@code truthy} (recommended String "true"), unload event will be prevented.
     */
    queryOnUnload(value) {
        if (value) {
            window.Vaadin.unloadObserver.query = 'true';
        }
        else {
            delete window.Vaadin.unloadObserver.query;
        }
    }

    static get properties() {
        return {
        };
    }
}

customElements.define(UnloadObserver.is, UnloadObserver);
