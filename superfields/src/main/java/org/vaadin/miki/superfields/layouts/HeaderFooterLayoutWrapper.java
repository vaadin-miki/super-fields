package org.vaadin.miki.superfields.layouts;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasComponents;
import org.vaadin.miki.markers.HasReadOnly;
import org.vaadin.miki.markers.WithIdMixin;

import java.util.Iterator;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * A wrapper for a typical header-body-footer layout that exposes header and footer, and delegates all
 * methods from {@link HasComponents} to the body. In other words, it allows using predefined layouts.
 *
 * This component is {@link Iterable} over the {@link Component}s contained in the body.
 * Similarly, {@link #getComponents()} ()} returns {@link Component}s things put in the body.
 *
 * @param <R> Root layout of the component. Obtainable from {@link #getContent()}.
 * @param <H> Header layout. Obtainable from {@link #getHeader()}.
 * @param <B> Body layout.
 * @param <F> Footer layout. Obtainable from {@link #getFooter()}.
 *
 * @author miki
 * @since 2021-09-03
 */
public class HeaderFooterLayoutWrapper<R extends Component & HasComponents,
                                 H extends Component & HasComponents,
                                 B extends Component & HasComponents,
                                 F extends Component & HasComponents>
        extends Composite<R>
        implements HasComponents, Iterable<Component>,
        WithHeaderComponentsMixin<H, HeaderFooterLayoutWrapper<R, H, B, F>>, WithIdMixin<HeaderFooterLayoutWrapper<R, H, B, F>>,
        WithFooterComponentsMixin<F, HeaderFooterLayoutWrapper<R, H, B, F>>, HasReadOnly
{

    private final R root;
    private final H header;
    private final B body;
    private final F footer;

    private boolean readOnly = false;

    /**
     * Creates the layout.
     * @param rootSupplier Callback to create root layout. {@code header}, {@code body}, and {@code footer} will all be added to it.
     * @param header Header.
     * @param body Body.
     * @param footer Footer.
     */
    public HeaderFooterLayoutWrapper(Supplier<R> rootSupplier, H header, B body, F footer) {
        super();
        this.root = rootSupplier.get();
        this.header = header;
        this.body = body;
        this.footer = footer;
        this.root.add(header, body, footer);
    }

    @Override
    protected R initContent() {
        return this.root;
    }

    @Override
    public void add(Component... components) {
        this.body.add(components);
    }

    @Override
    public void addComponentAtIndex(int index, Component component) {
        this.body.addComponentAtIndex(index, component);
    }

    @Override
    public void removeAll() {
        this.body.removeAll();
    }

    @Override
    public void remove(Component... components) {
        this.body.remove(components);
    }

    @Override
    public Optional<H> getHeader() {
        return Optional.ofNullable(this.header);
    }

    @Override
    public Optional<F> getFooter() {
        return Optional.ofNullable(this.footer);
    }

    @Override
    public Iterator<Component> iterator() {
        return this.getChildren().iterator();
    }

    /**
     * Returns the body of this component.
     * @return Body.
     */
    public B getBody() {
        return body;
    }

    /**
     * Returns components contained in the body.
     * @return A {@link Stream} with components contained in the body.
     * @see #iterator()
     * @see #getChildren()
     */
    public Stream<Component> getComponents() {
        return this.body.getChildren();
    }

    private void forwardReadOnly(boolean state, Stream<Component> components) {
        components.forEach(component -> HasReadOnly.setReadOnly(state, component));
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
        Stream.of(this.getHeader(), this.getFooter())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(Component::getChildren)
                .forEach(stream -> this.forwardReadOnly(readOnly, stream));
        this.forwardReadOnly(readOnly, this.getComponents());
    }

    @Override
    public boolean isReadOnly() {
        return readOnly;
    }
}
