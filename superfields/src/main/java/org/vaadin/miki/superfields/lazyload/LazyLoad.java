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

    public static final String EMPTY_CLASS_NAME = "lazy-load-empty";

    public static final String LOADED_CLASS_NAME = "lazy-loaded";

    private final Supplier<C> contentProvider;

    private C content = null;

    /**
     * Creates lazy load wrapper for given contents.
     * @param contents Contents to wrap for lazy loading.
     */
    public LazyLoad(C contents) {
        this(() -> contents);
    }

    /**
     * Creates lazy load wrapper for given component supplier.
     * @param supplier {@link Supplier} that will be called when the component gets into view.
     */
    public LazyLoad(Supplier<C> supplier) {
        super();
        this.getContent().addClassNames("lazy-load-container", EMPTY_CLASS_NAME);

        // more details: https://webdesign.tutsplus.com/tutorials/how-to-intersection-observer--cms-30250
        this.getElement().executeJs(
                "new IntersectionObserver(" +
                        " (entries, observer) => {" +
                        "if(entries[0].intersectionRatio == 1) {" +
                        " this.$server.onNowVisible(); " +
                        " observer.unobserve(this);" +
                        "}" +
                        "}," +
                        " {root: null, rootMargin: '0px', threshold: [0.0, 1.0]}" +
                        ").observe(this)"
        );
        this.contentProvider = supplier;
    }

    @ClientCallable
    private void onNowVisible() {
        if(this.content == null) {
            this.getContent().removeClassName(EMPTY_CLASS_NAME);
            this.getContent().addClassName(LOADED_CLASS_NAME);
            this.content = this.contentProvider.get();
            this.getContent().add(this.content);
        }
    }

    /**
     * Gets the content that has been already loaded.
     * @return A component that was lazy-loaded. If the component was not yet shown on-screen, returns {@link Optional#empty()}.
     * @see #isLoaded()
     */
    public Optional<C> getLoadedContent() {
        return Optional.ofNullable(this.content);
    }

    /**
     * Checks if the content has been already loaded.
     * @return Whether or not the content has been loaded.
     * @see #getLoadedContent()
     */
    public boolean isLoaded() {
        return this.content == null;
    }
}
