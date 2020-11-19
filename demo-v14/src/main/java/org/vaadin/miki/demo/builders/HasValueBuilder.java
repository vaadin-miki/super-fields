package org.vaadin.miki.demo.builders;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Validator;
import org.vaadin.miki.SampleModel;
import org.vaadin.miki.demo.ContentBuilder;
import org.vaadin.miki.demo.NeedsValidatorStorage;
import org.vaadin.miki.demo.ValidatorStorage;

import java.util.function.Consumer;

/**
 * Builds content for {@link HasValue}.
 * Reuses {@link ValidatorStorage}.
 * @author miki
 * @since 2020-11-18
 */
public class HasValueBuilder implements ContentBuilder<HasValue<?, ?>>, NeedsValidatorStorage {

    private ValidatorStorage storage;

    @Override
    public void setValidatorStorage(ValidatorStorage storage) {
        this.storage = storage;
    }

    @Override
    public void buildContent(HasValue<?, ?> component, Consumer<Component[]> callback) {
        final Checkbox toggle = new Checkbox("Mark component as read only?", event -> component.setReadOnly(event.getValue()));
        component.addValueChangeListener(this::onAnyValueChanged);
        callback.accept(new Component[]{toggle});
        if(this.storage.isValidatorPresent(component)) {
            final Span binder = new Span("This component has a validation check.");
            this.addBinder(component);
            callback.accept(new Component[]{binder});
        }
    }

    private void onAnyValueChanged(HasValue.ValueChangeEvent<?> valueChangeEvent) {
        Notification.show(String.format("%s changed value to %s", valueChangeEvent.getHasValue().getClass().getSimpleName(), valueChangeEvent.getValue()));
    }

    @SuppressWarnings("unchecked")
    private <T> void addBinder(HasValue<?, T> component) {
        final Validator<T> validator = this.storage.getValidator(component);
        final SampleModel<T> sampleModel = new SampleModel<>();
        final Binder<SampleModel<T>> binder = new Binder<>((Class<SampleModel<T>>)(Class<?>) SampleModel.class);
        binder.setBean(sampleModel);
        binder.forField(component)
                .withValidator(validator)
                .bind(SampleModel::getValue, SampleModel::setValue);
    }

}
