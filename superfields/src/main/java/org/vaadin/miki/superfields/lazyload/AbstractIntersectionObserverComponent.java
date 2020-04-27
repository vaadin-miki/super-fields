package org.vaadin.miki.superfields.lazyload;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;

/**
 * Base class for all components using Intersection Observer API.
 * @author miki
 * @since 2020-04-24
 */
public abstract class AbstractIntersectionObserverComponent<C extends Component> extends Composite<C> {

    private final boolean onlyLoadOnce;

    public AbstractIntersectionObserverComponent(boolean onlyLoadOnce) {
        this.onlyLoadOnce = onlyLoadOnce;
        // more details: https://webdesign.tutsplus.com/tutorials/how-to-intersection-observer--cms-30250
        StringBuilder observerJs = new StringBuilder();
        observerJs.append("new IntersectionObserver((entries, observer) => {if(entries[0].intersectionRatio == 1) {this.$server.becameVisible();");
        if(onlyLoadOnce)
            observerJs.append(" observer.unobserve(this);");
        else
            observerJs.append("} else if(entries[0].intersectionRatio == 0) {this.$server.becameHidden(); ");
        observerJs.append("}},{root: null, rootMargin: '0px', threshold: ");
        observerJs.append(!onlyLoadOnce ? "[0.0, 1.0]" : "1.0");
        observerJs.append("}).observe(this)");
        this.getElement().executeJs(observerJs.toString());
    }

    @ClientCallable
    private void becameVisible() {
        this.onNowVisible();
    }

    @ClientCallable
    private void becameHidden() {
        this.onNowHidden();
    }

    /**
     * Method called when this component becomes hidden.
     */
    protected abstract void onNowHidden();

    /**
     * Method called when this component becomes visible.
     */
    protected abstract void onNowVisible();

    /**
     * Checks the mode for lazy loading.
     * @return When {@code true}, the {@link #onNowVisible()} will happen only once.
     */
    public boolean isOnlyLoadOnce() {
        return onlyLoadOnce;
    }
}
