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

    private final ComponentObserver observer;

    private final boolean onlyLoadedOnce;

    private C lazyLoadedContent = null;

    private double hiddenVisibilityRange = 0.0d;
    private double shownVisibilityRange = 1.0d;

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
        this(supplier, ComponentObserver::new, removeOnHide);
    }

    /**
     * Creates lazy load wrapper for given contents. It will be displayed the first time this component becomes shown on screen.
     * @param contents Contents to wrap for lazy loading.
     * @param observerSupplier a way to create a customised {@link ComponentObserver} that will be used by this component.
     */
    public LazyLoad(C contents, SerializableSupplier<ComponentObserver> observerSupplier) {
        this(() -> contents, observerSupplier, false);
    }

    /**
     * Creates lazy load wrapper for given component supplier. It will be called exactly once, the first time this component becomes shown on screen.
     * @param contentSupplier {@link Supplier} that will be called when the component gets into view.
     * @param observerSupplier a way to create a customised {@link ComponentObserver} that will be used by this component.
     */
    public LazyLoad(SerializableSupplier<C> contentSupplier, SerializableSupplier<ComponentObserver> observerSupplier) {
        this(contentSupplier, observerSupplier, false);
    }

    /**
     * Creates lazy load wrapper for given component supplier.
     * @param contentSupplier {@link Supplier} that will be called when the component gets into view.
     * @param observerSupplier a way to create a customised {@link ComponentObserver} that will be used by this component.
     * @param removeOnHide Whether to remove the component when this object gets hidden.
     */
    public LazyLoad(SerializableSupplier<C> contentSupplier, SerializableSupplier<ComponentObserver> observerSupplier, boolean removeOnHide) {
        super();
        this.componentProvider = contentSupplier;
        this.onlyLoadedOnce = !removeOnHide;
        this.observer = observerSupplier.get();
        this.getContent().addClassNames(EMPTY_CLASS_NAME);
        this.getContent().add(this.observer);
        this.observer.addComponentObservationListener(this::onComponentObserved);
        this.observer.observe(this);
    }

    private void onComponentObserved(ComponentObservationEvent event) {
        if(event.getVisibilityRange() >= this.getContentShownVisibilityRange()) {
            this.onNowVisible();
            if(this.onlyLoadedOnce)
                this.observer.unobserve(this);
        }
        else if(event.getVisibilityRange() <= this.getContentHiddenVisibilityRange())
            this.onNowHidden();
    }

    /**
     * Returns the current range for considering the lazy-loaded component hidden.
     * @return A number between 0 (inclusive) and {@link #getContentShownVisibilityRange()} (inclusive).
     * @see #setContentVisibilityRanges(double, double)
     */
    public double getContentHiddenVisibilityRange() {
        return this.hiddenVisibilityRange;
    }

    /**
     * Returns the current range for considering the lazy-loaded component visible.
     * @return A number between {@link #getContentHiddenVisibilityRange()} (inclusive) and 1 (inclusive).
     * @see #setContentVisibilityRanges(double, double)
     */
    public double getContentShownVisibilityRange() {
        return this.shownVisibilityRange;
    }

    /**
     * Defines visibility ranges. {@link LazyLoad} uses a {@link ComponentObserver} to decide if the content should be shown or not.
     * By default, the component is lazy-loaded when {@link LazyLoad} is fully visible and hidden when it is fully invisible.
     * Defining ranges allows for lazy-loading the contents when it becomes partially visible. Be sure to know what you are doing and why.
     * @param hiddenOnOrBelow Visibility range at which or below which the component is considered hidden. Must be between 0 (inclusive) and the other parameter (inclusive).
     * @param visibleOnOrAbove Visibility range at which or above which the component is considered visible. Must be between the other parameter (inclusive) and 1 (inclusive).
     * @see <a href="https://github.com/vaadin-miki/super-fields/issues/498">issue 498</a>
     * @throws IllegalArgumentException when either range is below 0, above 1, or the first parameter is greater than the second
     */
    public void setContentVisibilityRanges(double hiddenOnOrBelow, double visibleOnOrAbove) {
        if(hiddenOnOrBelow < 0 || visibleOnOrAbove > 1)
            throw new IllegalArgumentException("component visibility boundaries must be between 0 and 1 (inclusive), but were %.3f and %.3f".formatted(hiddenOnOrBelow, visibleOnOrAbove));
        else if(hiddenOnOrBelow > visibleOnOrAbove)
            throw new IllegalArgumentException("visibility boundary for hiding the component (%.3f) must not be greater than the boundary for showing it (%.3f)".formatted(hiddenOnOrBelow, visibleOnOrAbove));
        else {
            this.shownVisibilityRange = visibleOnOrAbove;
            this.hiddenVisibilityRange = hiddenOnOrBelow;
        }
    }

    /**
     * Chains {@link #setContentVisibilityRanges(double, double)} and returns itself.
     * @param hiddenOnOrBelow Visibility range at which or below which the component is considered hidden. Must be between 0 (inclusive) and the other parameter (inclusive).
     * @param visibleOnOrAbove Visibility range at which or above which the component is considered visible. Must be between the other parameter (inclusive) and 1 (inclusive).
     * @return This.
     * @see #setContentVisibilityRanges(double, double)
     */
    public LazyLoad<C> withContentVisibilityRanges(double hiddenOnOrBelow, double visibleOnOrAbove) {
        this.setContentVisibilityRanges(hiddenOnOrBelow, visibleOnOrAbove);
        return this;
    }

    /**
     * Called when the content becomes hidden.
     */
    protected void onNowHidden() {
        if(this.lazyLoadedContent != null) {
            this.getContent().removeClassName(LOADED_CLASS_NAME);
            this.getContent().addClassName(EMPTY_CLASS_NAME);
            this.getContent().remove(this.lazyLoadedContent);
            this.lazyLoadedContent = null;
        }
    }

    /**
     * Called when the content becomes visible.
     */
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
     * @return Whether the content has been loaded.
     * @see #getLoadedContent()
     * @see #isOnlyLoadedOnce()
     */
    public boolean isLoaded() {
        return this.lazyLoadedContent == null;
    }

}
