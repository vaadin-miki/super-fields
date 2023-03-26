package org.vaadin.miki.demo.providers;

import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;
import org.vaadin.miki.demo.ComponentProvider;
import org.vaadin.miki.demo.Order;
import org.vaadin.miki.superfields.collections.CollectionField;
import org.vaadin.miki.superfields.text.SuperTextField;
import org.vaadin.miki.superfields.util.CollectionComponentProviders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Provides a {@link CollectionField} for {@code List<String>}s.
 * @author miki
 * @since 2021-08-25
 */
@Order(93)
public class CollectionFieldProvider implements ComponentProvider<CollectionField<String, List<String>>>, Validator<List<String>> {
    @Override
    public CollectionField<String, List<String>> getComponent() {
        return new CollectionField<String, List<String>>(
                ArrayList::new,
                CollectionComponentProviders.columnWithHeaderAndFooterRows(
                        Arrays.asList(
                            CollectionComponentProviders.removeAllButton("Clear"),
                            CollectionComponentProviders.addFirstButton("Add as first")
                        ),
                        Collections.singletonList(CollectionComponentProviders.addLastButton("Add as last"))),
                CollectionComponentProviders.rowWithRemoveButtonFirst(CollectionComponentProviders.labelledField(SuperTextField::new, "Value"), "Remove")
        )
                .withHelperText("(validator requires precisely three elements)")
                .withLabel("Enter some words:")
                ;
    }

    @Override
    public ValidationResult apply(List<String> strings, ValueContext valueContext) {
        return strings.size() != 3 ? ValidationResult.error("the list must have exactly 3 elements") : ValidationResult.ok();
    }
}
