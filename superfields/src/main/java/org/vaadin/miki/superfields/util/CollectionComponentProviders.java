package org.vaadin.miki.superfields.util;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import org.vaadin.miki.markers.HasIndex;
import org.vaadin.miki.markers.HasLabel;
import org.vaadin.miki.superfields.collections.CollectionComponentProvider;
import org.vaadin.miki.superfields.collections.CollectionController;
import org.vaadin.miki.superfields.collections.CollectionLayoutProvider;
import org.vaadin.miki.superfields.collections.CollectionValueComponentProvider;
import org.vaadin.miki.superfields.collections.IndexedButton;
import org.vaadin.miki.superfields.layouts.FlexLayoutHelpers;
import org.vaadin.miki.superfields.layouts.HeaderFooterFieldWrapper;
import org.vaadin.miki.superfields.layouts.HeaderFooterLayoutWrapper;
import org.vaadin.miki.superfields.text.SuperTextArea;
import org.vaadin.miki.superfields.text.SuperTextField;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;

/**
 * Utility class with common use-cases for {@link CollectionComponentProvider} and subclasses.
 *
 * @author miki
 * @since 2021-09-04
 */
public class CollectionComponentProviders {

    /**
     * Produces plain text field. Method signature follows {@link CollectionValueComponentProvider}.
     * @param index Index.
     * @param controller Controller.
     * @return A {@link SuperTextField}.
     */
    @SuppressWarnings("squid:S1172") // parameters present to follow method signature
    public static SuperTextField textField(int index, CollectionController controller) {
        return new SuperTextField();
    }

    /**
     * Produces plain text area. Method signature follows {@link CollectionValueComponentProvider}.
     * @param index Index.
     * @param controller Controller.
     * @return A {@link SuperTextArea}.
     */
    @SuppressWarnings("squid:S1172") // parameters present to follow method signature
    public static SuperTextArea textArea(int index, CollectionController controller) {
        return new SuperTextArea();
    }

    /**
     * Returns a {@link CollectionValueComponentProvider} that generates a given field and sets its label.
     * @param fieldSupplier Field supplier.
     * @param label Label to use. {@code String.format(label, index)} will be applied, so any {@code %d} will be replaced with an index.
     * @param <T> Type of data.
     * @param <F> Type of component to display the data.
     * @return A {@link CollectionValueComponentProvider} that generates given field and sets its label.
     */
    public static <T, F extends Component & HasValue<?, T> & HasLabel> CollectionValueComponentProvider<T, F> labelledField(Supplier<F> fieldSupplier, String label) {
        return (index, controller) -> {
            final F result = fieldSupplier.get();
            result.setLabel(String.format(label, index));
            return result;
        };
    }

    /**
     * A {@link CollectionValueComponentProvider} that wraps given field with a layout containing header and footer.
     * The wrapper is row-aligned, with header and footer being column-based.
     * None of the parameters may ever be {@code null}.
     * @param inner Inner field generator.
     * @param headerComponents A collection of generators for components to appear in the header.
     * @param footerComponents A collection of generators for components to appear in the footer.
     * @param <T> Type of the value.
     * @param <F> Type of the field used to show the value.
     * @return A {@link CollectionValueComponentProvider} that generates a {@link HeaderFooterFieldWrapper} with a given field, header and footer.
     */
    public static <T, F extends Component & HasValue<?, T>> CollectionValueComponentProvider<T, HeaderFooterFieldWrapper<T, FlexLayout, FlexLayout>> row(CollectionValueComponentProvider<T, F> inner, Collection<CollectionComponentProvider<?>> headerComponents, Collection<CollectionComponentProvider<?>> footerComponents) {
        return (index, controller) ->
                new HeaderFooterFieldWrapper<>(
                        FlexLayoutHelpers::row,
                        FlexLayoutHelpers.column(),
                        inner.provideComponent(index, controller),
                        FlexLayoutHelpers.column()
                )
                .withHeaderComponents(headerComponents.stream().map(f -> f.provideComponent(index, controller)).toArray(Component[]::new))
                .withFooterComponents(footerComponents.stream().map(f -> f.provideComponent(index, controller)).toArray(Component[]::new));
    }

    /**
     * A {@link CollectionValueComponentProvider} that wraps given field with a layout containing a header and footer.
     * The footer contains a button that removes the entry from the collection.
     * @param inner Inner field generator.
     * @param removeButtonText Text to appear on the button.
     * @param <T> Type of the value.
     * @param <F> Type of the field used to show the value.
     * @return A {@link CollectionValueComponentProvider} that generates a {@link HeaderFooterFieldWrapper} with a given field, header and footer (with a button).
     */
    public static <T, F extends Component & HasValue<?, T>> CollectionValueComponentProvider<T, HeaderFooterFieldWrapper<T, FlexLayout, FlexLayout>> rowWithRemoveButtonLast(CollectionValueComponentProvider<T, F> inner, String removeButtonText) {
        return row(inner, Collections.emptyList(), Collections.singleton((index, controller) -> new IndexedButton(removeButtonText, index, event -> controller.remove(((HasIndex)event.getSource()).getIndex()))));
    }

    /**
     * A {@link CollectionValueComponentProvider} that wraps given field with a layout containing a header and footer.
     * The header contains a button that removes the entry from the collection.
     * @param inner Inner field generator.
     * @param removeButtonText Text to appear on the button.
     * @param <T> Type of the value.
     * @param <F> Type of the field used to show the value.
     * @return A {@link CollectionValueComponentProvider} that generates a {@link HeaderFooterFieldWrapper} with a given field, header (with a button) and a footer.
     */
    public static <T, F extends Component & HasValue<?, T>> CollectionValueComponentProvider<T, HeaderFooterFieldWrapper<T, FlexLayout, FlexLayout>> rowWithRemoveButtonFirst(CollectionValueComponentProvider<T, F> inner, String removeButtonText) {
        return row(inner, Collections.singleton(CollectionComponentProviders.removeButton(removeButtonText)), Collections.emptyList());
    }

    /**
     * A {@link CollectionLayoutProvider} for a {@link HeaderFooterLayoutWrapper} based on {@link FlexLayout}s.
     * The content is column-based. The footer and header are row-based.
     * No parameter may ever be {@code null}.
     * @param headerComponents Components to place in the header.
     * @param footerComponents Components to place in the footer.
     * @return A {@link CollectionLayoutProvider}.
     */
    public static CollectionLayoutProvider<HeaderFooterLayoutWrapper<FlexLayout, FlexLayout, FlexLayout, FlexLayout>> columnWithHeaderAndFooterRows(
            Collection<CollectionComponentProvider<?>> headerComponents, Collection<CollectionComponentProvider<?>> footerComponents
    ) {
        return (index, controller) -> new HeaderFooterLayoutWrapper<>(
                FlexLayoutHelpers::column,
                FlexLayoutHelpers.row(),
                FlexLayoutHelpers.column(),
                FlexLayoutHelpers.row()
        )
                .withHeaderComponents(headerComponents.stream().map(h -> h.provideComponent(index, controller)).toArray(Component[]::new))
                .withFooterComponents(footerComponents.stream().map(f -> f.provideComponent(index, controller)).toArray(Component[]::new));
    }

    /**
     * A {@link CollectionComponentProvider} that produces a {@link Button} that clears the collection by calling {@link CollectionController#removeAll()}.
     * @param text Text to appear on the button.
     * @return A {@link CollectionComponentProvider} that produces a {@link Button}.
     */
    public static CollectionComponentProvider<Button> removeAllButton(String text) {
        return (index, controller) -> new Button(text, event -> controller.removeAll());
    }

    /**
     * A {@link CollectionComponentProvider} that produces a {@link Button} that adds an element to the collection by calling {@link CollectionController#add(int)} with {@code 0} as parameter.
     * @param text Text to appear on the button.
     * @return A {@link CollectionComponentProvider} that produces a {@link Button}.
     */
    public static CollectionComponentProvider<Button> addFirstButton(String text) {
        return (index, controller) -> new Button(text, event -> controller.add(0));
    }

    /**
     * A {@link CollectionComponentProvider} that produces a {@link Button} that adds an element to the collection by calling {@link CollectionController#add()}.
     * @param text Text to appear on the button.
     * @return A {@link CollectionComponentProvider} that produces a {@link Button}.
     */
    public static CollectionComponentProvider<Button> addLastButton(String text) {
        return (index, controller) -> new Button(text, event -> controller.add());
    }

    /**
     * A {@link CollectionComponentProvider} that produces an {@link IndexedButton} that removes a given element from the collection by calling {@link CollectionController#remove(int)}.
     * @param text Text to appear on the button.
     * @return A {@link CollectionComponentProvider} that produces a {@link Button}.
     */
    public static CollectionComponentProvider<IndexedButton> removeButton(String text) {
        return (index, controller) -> new IndexedButton(text, index, event -> controller.remove(((HasIndex)event.getSource()).getIndex()));
    }

    private CollectionComponentProviders() {
        // no instances allowed
    }
}
