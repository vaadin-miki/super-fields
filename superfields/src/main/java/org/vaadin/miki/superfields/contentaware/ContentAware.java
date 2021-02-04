package org.vaadin.miki.superfields.contentaware;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.shared.Registration;
import org.slf4j.LoggerFactory;

/**
 * Content-aware that sends events when a component is added to or removed from anywhere in its contents (even in a nested layout).
 *
 * @author miki
 * @since 2021-01-20
 */
@Tag("content-aware")
public class ContentAware extends Div implements ContentChangeNotifier {

    private boolean active = false;

    @ClientCallable
    private void mutationObserved(int addedNodeCount, int removedNodeCount) {
        LoggerFactory.getLogger(this.getClass()).info("mutation observed; added {}, removed {} nodes", addedNodeCount, removedNodeCount);
        this.fireEvent(new ContentChangeEvent(this, true, addedNodeCount, removedNodeCount));
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        this.getElement().getNode().runWhenAttached(ui -> ui.beforeClientResponse(this, context -> {
            this.getElement().executeJs("console.log('CA: setting up content-aware'); " +
                    "if($0.observer === undefined) {" +
                    "$0.observer = new MutationObserver((list, obs) => {" +
                    "list.forEach( (mutation) => {" +
                    "$0.$server.mutationObserved(mutation.addedNodes.length, mutation.removedNodes.length);" +
//                    "mutation.addedNodes.forEach( (node) => {console.warn('added '+node);});" +
                    "});" +
                            "});" +
                    "}"
            );
            this.notifyClient();
        }));
    }

    private void notifyClient() {
        if(this.isActive())
            this.getElement().executeJs("$0.observer.observe($0, {attributes: false, childList: true, subtree: true});");
        else
            this.getElement().executeJs("$0.observer.disconnect();");
    }

    /**
     * Checks whether observation for changes is active.
     * @return Whether or not changes to the dom structure are reported to this object; {@code false} by default.
     */
    public boolean isActive() {
        return this.active;
    }

    /**
     * Activates or deactivates observation for changes in the dom structure.
     * @param active Whether or not to listen to changes.
     */
    public void setActive(boolean active) {
        this.active = active;
        this.notifyClient();
    }

    @Override
    public Registration addContentChangeListener(ContentChangeListener listener) {
        return this.getEventBus().addListener(ContentChangeEvent.class, listener);
    }
}
