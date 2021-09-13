package org.vaadin.miki.superfields.layouts;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasHelper;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.customfield.CustomField;
import org.vaadin.miki.markers.HasIndex;
import org.vaadin.miki.markers.HasLabel;
import org.vaadin.miki.markers.WithHelperMixin;
import org.vaadin.miki.markers.WithIdMixin;
import org.vaadin.miki.markers.WithIndexMixin;
import org.vaadin.miki.markers.WithLabelMixin;
import org.vaadin.miki.markers.WithValueMixin;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * A three-part layout (header, field, footer) for any field that itself is a field.
 * For easier use in collection field it implements {@link WithIndexMixin} and delegates setting index to header and footer components
 * (and to the field, if it also implements it).
 *
 * @param <T> Type of data the field has.
 * @param <H> Type of header {@link Component}.
 * @param <F> Type of footer {@link Component}.
 *
 * @author miki
 * @since 2021-09-03
 */
public class HeaderFooterFieldWrapper<T,
                                      H extends Component & HasComponents,
                                      F extends Component & HasComponents>
        extends CustomField<T>
        implements WithLabelMixin<HeaderFooterFieldWrapper<T, H, F>>,
                   WithIdMixin<HeaderFooterFieldWrapper<T, H, F>>,
                   WithHelperMixin<HeaderFooterFieldWrapper<T, H, F>>,
                   WithHeaderComponentsMixin<H, HeaderFooterFieldWrapper<T, H, F>>,
                   WithFooterComponentsMixin<F, HeaderFooterFieldWrapper<T, H, F>>,
                   WithValueMixin<AbstractField.ComponentValueChangeEvent<CustomField<T>, T>, T, HeaderFooterFieldWrapper<T, H, F>>,
                   WithIndexMixin<HeaderFooterFieldWrapper<T, H, F>> {

    private final HasValue<?, T> field;
    private final H header;
    private final F footer;
    private int index;
    private boolean disablingHeaderOnReadOnly = true;
    private boolean disablingFooterOnReadOnly = true;

    public <R extends Component & HasComponents, V extends Component & HasValue<?, T>> HeaderFooterFieldWrapper(Supplier<R> rootSupplier, H header, V field, F footer) {
        this.field = field;
        this.header = header;
        this.footer = footer;

        final R root = rootSupplier.get();
        root.add(header, field, footer);
        this.add(root);
    }

    @Override
    public void setLabel(String label) {
        if(this.field instanceof HasLabel)
            ((HasLabel) this.field).setLabel(label);
        else super.setLabel(label);
    }

    @Override
    public String getLabel() {
        return this.field instanceof HasLabel ? ((HasLabel) this.field).getLabel() : super.getLabel();
    }

    @Override
    public void setHelperComponent(Component component) {
        if(this.field instanceof HasHelper)
            ((HasHelper) this.field).setHelperComponent(component);
        else super.setHelperComponent(component);
    }

    @Override
    public void setHelperText(String helperText) {
        if(this.field instanceof HasHelper)
            ((HasHelper) this.field).setHelperText(helperText);
        else super.setHelperText(helperText);
    }

    @Override
    protected T generateModelValue() {
        return this.field.getValue();
    }

    @Override
    protected void setPresentationValue(T t) {
        this.field.setValue(Objects.requireNonNullElse(t, this.field.getEmptyValue()));
    }

    @Override
    public Optional<F> getFooter() {
        return Optional.ofNullable(this.footer);
    }

    @Override
    public Optional<H> getHeader() {
        return Optional.ofNullable(this.header);
    }

    @Override
    public int getIndex() {
        return this.index;
    }

    private void propagateSetIndex(int index, Stream<Component> components) {
        components.filter(HasIndex.class::isInstance).map(HasIndex.class::cast).forEach(c -> c.setIndex(index));
    }

    @Override
    public void setIndex(int index) {
        this.index = index;
        Stream.of(this.getHeader(), this.getFooter())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(Component::getChildren)
                .forEach(stream -> this.propagateSetIndex(index, stream));
        if(this.field instanceof HasIndex)
            ((HasIndex) this.field).setIndex(index);
    }

    public void setDisablingFooterOnReadOnly(boolean disablingFooterOnReadOnly) {
        this.disablingFooterOnReadOnly = disablingFooterOnReadOnly;
    }

    public boolean isDisablingFooterOnReadOnly() {
        return disablingFooterOnReadOnly;
    }

    public final HeaderFooterFieldWrapper<T, H, F> withDisablingFooterOnReadOnly(boolean state) {
        this.setDisablingFooterOnReadOnly(state);
        return this;
    }

    public void setDisablingHeaderOnReadOnly(boolean disablingHeaderOnReadOnly) {
        this.disablingHeaderOnReadOnly = disablingHeaderOnReadOnly;
    }

    public boolean isDisablingHeaderOnReadOnly() {
        return disablingHeaderOnReadOnly;
    }

    public final HeaderFooterFieldWrapper<T, H, F> withDisablingHeaderOnReadOnly(boolean state) {
        this.setDisablingHeaderOnReadOnly(state);
        return this;
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        this.field.setReadOnly(readOnly);

        if(this.isDisablingFooterOnReadOnly())
            this.getFooter().ifPresent(f -> f.setEnabled(!readOnly));

        if(this.isDisablingHeaderOnReadOnly())
            this.getHeader().ifPresent(h -> h.setEnabled(!readOnly));
    }

}
