package org.vaadin.miki.superfields.contentaware;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Div;
import org.slf4j.LoggerFactory;

/**
 * Content-aware that sends events when a component is added to or removed from anywhere in its contents (even in a nested layout).
 *
 * @author miki
 * @since 2021-01-20
 */
@Tag("content-aware")
public class ContentAware extends Div {

    private boolean active = true;

    @ClientCallable
    private void mutationObserved() {
        LoggerFactory.getLogger(this.getClass()).info("mutation observed!");
        this.fireEvent(new ContentEvent(this, true));
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        this.getElement().getNode().runWhenAttached(ui -> ui.beforeClientResponse(this, context -> {
            this.getElement().executeJs("console.warn('setting up'); " +
                    "if($0.observer === undefined) {" +
                    "$0.observer = new MutationObserver((list, obs) => {" +
                    "console.warn('mutation observed'); " +
                    "$0.$server.mutationObserved();" +
                    "});" +
                    "}" +
                    "$0.observer.observe($0, {attributes: false, childList: true, subtree: true});");
        }));
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
