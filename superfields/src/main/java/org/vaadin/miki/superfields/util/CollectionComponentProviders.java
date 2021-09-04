package org.vaadin.miki.superfields.util;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import org.vaadin.miki.markers.HasIndex;
import org.vaadin.miki.superfields.collections.CollectionComponentProvider;
import org.vaadin.miki.superfields.collections.CollectionController;
import org.vaadin.miki.superfields.collections.IndexedButton;
import org.vaadin.miki.superfields.collections.ValueComponentProvider;
import org.vaadin.miki.superfields.layouts.FlexLayoutHelpers;
import org.vaadin.miki.superfields.layouts.HeaderFooterFieldWrapper;
import org.vaadin.miki.superfields.text.SuperTextArea;
import org.vaadin.miki.superfields.text.SuperTextField;

import java.util.Collection;
import java.util.Collections;

/**
 * Utility class with common use-cases for {@link ValueComponentProvider}.
 *
 * @author miki
 * @since 2021-09-04
 */
public class CollectionComponentProviders {

    public static SuperTextField textField(int index, CollectionController controller) {
        return new SuperTextField();
    }

    public static SuperTextArea textArea(int index, CollectionController controller) {
        return new SuperTextArea();
    }

    public static ValueComponentProvider<String, SuperTextField> textField(String label) {
        return (index, controller) -> new SuperTextField(String.format(label, index));
    }

    public static ValueComponentProvider<String, SuperTextArea> textArea(String label) {
        return ((index, controller) -> new SuperTextArea(String.format(label, index)));
    }

    public static <T, F extends Component & HasValue<?, T>> ValueComponentProvider<T, HeaderFooterFieldWrapper<T, FlexLayout, FlexLayout>> row(ValueComponentProvider<T, F> inner, Collection<CollectionComponentProvider<?>> headerComponents, Collection<CollectionComponentProvider<?>> footerComponents) {
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

    public static <T, F extends Component & HasValue<?, T>> ValueComponentProvider<T, HeaderFooterFieldWrapper<T, FlexLayout, FlexLayout>> rowWithRemoveButtonLast(ValueComponentProvider<T, F> inner, String removeButtonText) {
        return row(inner, Collections.emptyList(), Collections.singleton((index, controller) -> new IndexedButton(removeButtonText, index, event -> controller.remove(((HasIndex)event.getSource()).getIndex()))));
    }

    public static <T, F extends Component & HasValue<?, T>> ValueComponentProvider<T, HeaderFooterFieldWrapper<T, FlexLayout, FlexLayout>> rowWithRemoveButtonFirst(ValueComponentProvider<T, F> inner, String removeButtonText) {
        return row(inner, Collections.singleton((index, controller) -> new IndexedButton(removeButtonText, index, event -> controller.remove(((HasIndex)event.getSource()).getIndex()))), Collections.emptyList());
    }

    private CollectionComponentProviders() {
        // no instances allowed
    }
}
