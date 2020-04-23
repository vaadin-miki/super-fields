package org.vaadin.miki.superfields.lazyload;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Div;
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
public class LazyLoad<C extends Component> extends Composite<LazyLoad.LazyLoadElement> implements WithIdMixin<LazyLoad<C>> {

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

    private final Supplier<C> componentProvider;

    private final boolean removingOnHide;

    private C content = null;

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
    public LazyLoad(Supplier<C> supplier) {
        this(supplier, false);
    }

    /**
     * Creates lazy load wrapper for given contents.
     * @param contents Contents to wrap for lazy loading.
     * @param removeOnHide Whether or not to remove the component when this object gets hidden.
     */
    public LazyLoad(C contents, boolean removeOnHide) {
        this(() -> contents, removeOnHide);
    }

    /**
     * Creates lazy load wrapper for given component supplier.
     * @param supplier {@link Supplier} that will be called when the component gets into view.
     * @param removeOnHide Whether or not to remove the component when this object gets hidden.
     */
    public LazyLoad(Supplier<C> supplier, boolean removeOnHide) {
        super();
        this.getContent().addClassNames("lazy-load-container", EMPTY_CLASS_NAME);
        this.removingOnHide = removeOnHide;
        this.componentProvider = supplier;
        // more details: https://webdesign.tutsplus.com/tutorials/how-to-intersection-observer--cms-30250
        StringBuilder observerJs = new StringBuilder();
        observerJs.append("new IntersectionObserver((entries, observer) => {if(entries[0].intersectionRatio == 1) {this.$server.onNowVisible(); ");
        if(!removeOnHide)
            observerJs.append(" observer.unobserve(this);");
        else
            observerJs.append("} else if(entries[0].intersectionRatio == 0) {this.$server.onNowHidden(); ");
        observerJs.append("}},{root: null, rootMargin: '0px', threshold: ");
        observerJs.append(removeOnHide ? "[0.0, 1.0]" : "1.0");
        observerJs.append("}).observe(this)");
        this.getElement().executeJs(observerJs.toString());
    }

    @ClientCallable
    private void onNowHidden() {
        if(this.content != null) {
            this.getContent().removeClassName(LOADED_CLASS_NAME);
            this.getContent().addClassName(EMPTY_CLASS_NAME);
            this.getContent().remove(this.content);
            this.content = null;
        }
    }

    @ClientCallable
    private void onNowVisible() {
        if(this.content == null) {
            this.getContent().removeClassName(EMPTY_CLASS_NAME);
            this.getContent().addClassName(LOADED_CLASS_NAME);
            this.content = this.componentProvider.get();
            this.getContent().add(this.content);
        }
    }

    /**
     * Gets the content if it was already loaded ({@link #isRemovingOnHide()} is {@code true}) or if it is currently showing.
     * @return A component that was lazy-loaded. If the component was not yet shown on-screen, returns {@link Optional#empty()}.
     * @see #isLoaded()
     * @see #isRemovingOnHide()
     */
    public Optional<C> getLoadedContent() {
        return Optional.ofNullable(this.content);
    }

    /**
     * Checks if the content has been already loaded ({@link #isRemovingOnHide()} is {@code true}) or is currently loaded.
     * @return Whether or not the content has been loaded.
     * @see #getLoadedContent()
     * @see #isRemovingOnHide()
     */
    public boolean isLoaded() {
        return this.content == null;
    }

    /**
     * Checks the mode of operation for lazy loading.
     * @return When {@code true}, each time this component gets out of view, its contents are removed and then recreated again on showing. When {@code false}, the lazy-loading will happen only once, first time this component gets shown.
     */
    public boolean isRemovingOnHide() {
        return this.removingOnHide;
    }
}
