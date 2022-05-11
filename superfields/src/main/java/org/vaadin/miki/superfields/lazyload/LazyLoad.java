package org.vaadin.miki.superfields.lazyload;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.function.SerializableSupplier;
import org.vaadin.miki.markers.WithIdMixin;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * A component that remains empty until it gets into view. After that it displays the lazy-loaded content.
 * Note that by default the size of this component is 0px, as there is no styling to it. Be sure to apply styling.
 * As a result and by default, if there are many of lazy load components one next to another, all of them will trigger.
 *
 * @param <C> Type of component that is lazy-loaded.
 * @author miki
 * @since 2020-04-22
 */
public class LazyLoad<C extends Component> extends Composite<LazyLoad.LazyLoadElement> implements WithIdMixin<LazyLoad<C>>, HasStyle {

    /**
     * This class exists so that {@link LazyLoad} can have a custom tag name.
     */
    @Tag("lazy-load")
    public static final class LazyLoadElement extends Div {}

    /**
     * Style name when this component is empty (not showing anything).
     */
    public static final String EMPTY_CLASS_NAME = "lazy-load-empty";

    /**
     * Style name when this component has content.
     */
    public static final String LOADED_CLASS_NAME = "lazy-loaded";

    private final SerializableSupplier<C> componentProvider;

    private final ComponentObserver observer = new ComponentObserver();

    private final boolean onlyLoadedOnce;

    private C lazyLoadedContent = null;

    /**
     * Creates lazy load wrapper for given contents. It will be displayed the first time this component becomes shown on screen.
     * @param contents Contents to wrap for lazy loading.
     */
    public LazyLoad(C contents) {
        this(contents, false);
    }

    /**
     * Creates lazy load wrapper for given component supplier. It will be called exactly once, the first time this component becomes shown on screen.
     * @param supplier {@link Supplier} that will be called when the component gets into view.
     */
    public LazyLoad(SerializableSupplier<C> supplier) {
        this(supplier, false);
    }

    /**
     * Creates lazy load wrapper for given contents.
     * @param contents Contents to wrap for lazy loading.
     * @param removeOnHide Whether to remove the component when this object gets hidden.
     */
    public LazyLoad(C contents, boolean removeOnHide) {
        this(() -> contents, removeOnHide);
    }

    /**
     * Creates lazy load wrapper for given component supplier.
     * @param supplier {@link Supplier} that will be called when the component gets into view.
     * @param removeOnHide Whether to remove the component when this object gets hidden.
     */
    public LazyLoad(SerializableSupplier<C> supplier, boolean removeOnHide) {
        super();
        this.componentProvider = supplier;
        this.onlyLoadedOnce = !removeOnHide;
        this.getContent().addClassNames(EMPTY_CLASS_NAME);
        this.getContent().add(this.observer);
        this.observer.addComponentObservationListener(this::onComponentObserved);
        this.observer.observe(this);
    }

    private void onComponentObserved(ComponentObservationEvent event) {
        if(event.isFullyVisible()) {
            this.onNowVisible();
            if(this.onlyLoadedOnce)
                this.observer.unobserve(this);
        }
        else if(event.isNotVisible())
            this.onNowHidden();
    }

    protected void onNowHidden() {
        if(this.lazyLoadedContent != null) {
            this.getContent().removeClassName(LOADED_CLASS_NAME);
            this.getContent().addClassName(EMPTY_CLASS_NAME);
            this.getContent().remove(this.lazyLoadedContent);
            this.lazyLoadedContent = null;
        }
    }

    protected void onNowVisible() {
        if(this.lazyLoadedContent == null) {
            this.getContent().removeClassName(EMPTY_CLASS_NAME);
            this.getContent().addClassName(LOADED_CLASS_NAME);
            this.lazyLoadedContent = this.componentProvider.get();
            this.getContent().add(this.lazyLoadedContent);
        }
    }

    /**
     * Returns if the lazy loading happens only on the first showing.
     * @return {@code true} when the target component will be loaded only once, the first time this component is shown; otherwise {@code false}.
     */
    public boolean isOnlyLoadedOnce() {
        return this.onlyLoadedOnce;
    }

    /**
     * Gets the content if it was already loaded ({@link #isOnlyLoadedOnce()} ()} is {@code true}) or if it is currently showing.
     * @return A component that was lazy-loaded. If the component was not yet shown on-screen, returns {@link Optional#empty()}.
     * @see #isLoaded()
     * @see #isOnlyLoadedOnce()
     */
    public Optional<C> getLoadedContent() {
        return Optional.ofNullable(this.lazyLoadedContent);
    }

    /**
     * Checks if the content has been already loaded ({@link #isOnlyLoadedOnce()} ()} is {@code true}) or is currently loaded.
     * @return Whether or not the content has been loaded.
     * @see #getLoadedContent()
     * @see #isOnlyLoadedOnce()
     */
    public boolean isLoaded() {
        return this.lazyLoadedContent == null;
    }

}
