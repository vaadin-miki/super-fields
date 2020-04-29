package org.vaadin.miki.superfields.unload;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.templatemodel.TemplateModel;

/**
 * Server-side component that listens to {@code beforeunload} events.
 * Based on <a href="https://vaadin.com/forum/thread/17523194/unsaved-changes-detect-page-exit-or-reload">the code by Kaspar Scherrer and Stuart Robinson</a>.
 *
 * @author Kaspar Scherrer, Stuart Robinson; adapted to web-component by miki
 * @since 2020-04-29
 */
@JsModule("./unload-observer.js")
@Tag("unload-observer")
public class UnloadObserver extends PolymerTemplate<TemplateModel> {

    private boolean queryingOnUnload;

    private boolean clientInitialised;

    /**
     * Creates the unload observer and by default queries the user on unloading the page.
     */
    public UnloadObserver() {
        this(true);
    }

    /**
     * Creates the unload observer.
     * @param queryOnUnload Whether or not to query the user on unloading the page.
     */
    public UnloadObserver(boolean queryOnUnload) {
        super();
        this.setQueryingOnUnload(queryOnUnload);
    }

    /**
     * Controls whether or not there should be querying when the document is going to be unloaded.
     * @param queryingOnUnload When {@code true}, {@link UnloadListener}s registered through {@link #addUnloadListener(UnloadListener)} will be notified and document unloading can be prevented. When {@code false}, nothing will happen when the document gets unloaded.
     */
    public void setQueryingOnUnload(boolean queryingOnUnload) {
        if(queryingOnUnload != this.queryingOnUnload) {
            this.queryingOnUnload = queryingOnUnload;
            this.getElement().getNode().runWhenAttached(ui -> ui.beforeClientResponse(this, context ->
                    this.getElement().callJsFunction("queryOnUnload", this.queryingOnUnload)
            ));
        }
    }

    /**
     * Returns whether or not querying on document unload will happen.
     * @return {@code true} when unloading the document from browser window results in showing a browser-native confirmation dialog and notifying {@link UnloadListener}s; {@code false} otherwise.
     */
    public boolean isQueryingOnUnload() {
        return this.queryingOnUnload;
    }

    /**
     * Chains {@link #setQueryingOnUnload(boolean)} and returns itself.
     * @param value Whether or not to query on document unload.
     * @return This.
     * @see #setQueryingOnUnload(boolean)
     */
    public UnloadObserver withQueryingOnUnload(boolean value) {
        this.setQueryingOnUnload(value);
        return this;
    }

    /**
     * Shortcut for {@code withQueryingOnUnload(true)}.
     * @return This.
     * @see #withQueryingOnUnload(boolean)
     */
    public UnloadObserver withQueryingOnUnload() {
        return this.withQueryingOnUnload(true);
    }

    /**
     * Shortcut for {@code withQueryingOnUnload(false)}.
     * @return This.
     * @see #withQueryingOnUnload(boolean)
     */
    public UnloadObserver withoutQueryingOnUnload() {
        return this.withQueryingOnUnload(false);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        if(!this.clientInitialised) {
            this.getElement().callJsFunction("initObserver");
            this.clientInitialised = true;
        }
        super.onAttach(attachEvent);
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        if(this.clientInitialised) {
            this.getElement().callJsFunction("stopObserver");
            this.clientInitialised = false;
        }
        super.onDetach(detachEvent);
    }

    @ClientCallable
    private void unloadAttempted() {
        this.fireUnloadEvent(new UnloadEvent(this));
    }

    /**
     * Fires the {@link UnloadEvent}.
     * @param event Event to fire.
     */
    protected void fireUnloadEvent(UnloadEvent event) {
        this.getEventBus().fireEvent(event);
    }

    /**
     * Adds an {@link UnloadListener}.
     * @param listener Listener to add.
     * @return A {@link Registration} that can be used to stop listening to the event.
     */
    public Registration addUnloadListener(UnloadListener listener) {
        return this.getEventBus().addListener(UnloadEvent.class, listener);
    }

}
