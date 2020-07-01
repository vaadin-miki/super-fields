package org.vaadin.miki.superfields.unload;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.templatemodel.TemplateModel;
import org.vaadin.miki.markers.WithIdMixin;

/**
 * Server-side component that listens to {@code beforeunload} events.
 * Based on <a href="https://vaadin.com/forum/thread/17523194/unsaved-changes-detect-page-exit-or-reload">the code by Kaspar Scherrer and Stuart Robinson</a>.
 * This component will broadcast events on {@code beforeunload} event in the browser. If {@link #isQueryingOnUnload()}
 * is {@code true}, before the event the user will be prompted about leaving the page. However, there is no way to find out what the user selected.
 * If {@link #isQueryingOnUnload()} is {@code false}, the event on the server will be called just before the page is unloaded.
 * Note that the component must be present in the DOM structure in the browser for the event to be received on the server.
 *
 * Warning: this class is pretty much a {@link UI}-scoped singleton; the class is final, the constructors are private and there is at most one global instance per UI.
 *
 * @author Kaspar Scherrer, Stuart Robinson; adapted to web-component by miki; bugfixing and enhancements by Jean-François Lamy
 * @since 2020-04-29
 */
@JsModule("./unload-observer.js")
@Tag("unload-observer")
public final class UnloadObserver extends PolymerTemplate<TemplateModel> implements WithIdMixin<UnloadObserver> {

    /**
     * Returns or creates an instance for current UI.
     * The result is associated with the UI, but not added to any of its components.
     * @return An instance of {@link UnloadObserver}.
     * @throws IllegalStateException if there is no current {@link UI}.
     */
    public static UnloadObserver get() {
        UI ui = UI.getCurrent();
        if(ui == null)
            throw new IllegalStateException("there is no UI available to create UnloadObserver for");
        return get(ui);
    }

    /**
     * Returns or creates an instance for a given UI.
     * The result is associated with the UI, but not added to any of its components.
     * @param ui A {@link UI} to register the instance of {@link UnloadObserver} in. Must not be {@code null}.
     * @return An instance of {@link UnloadObserver}.
     */
    public static UnloadObserver get(UI ui) {
        UnloadObserver instance = ComponentUtil.getData(ui, UnloadObserver.class);
        if(instance == null) {
            instance = new UnloadObserver();
            ComponentUtil.setData(ui, UnloadObserver.class, instance);
        }
        return instance;
    }

    private boolean queryingOnUnload;

    private boolean clientInitialised;

    /**
     * Creates the unload observer and by default queries the user on unloading the page.
     */
    private UnloadObserver() {
        this(true);
    }

    /**
     * Creates the unload observer.
     * @param queryOnUnload Whether or not to query the user on unloading the page.
     */
    private UnloadObserver(boolean queryOnUnload) {
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
        this.clientInitialised = false;
        super.onDetach(detachEvent);
    }

    @ClientCallable
    private void unloadHappened() {
        this.fireUnloadEvent(new UnloadEvent(this, false));
    }

    @ClientCallable
    private void unloadAttempted() {
        this.fireUnloadEvent(new UnloadEvent(this, true));
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
