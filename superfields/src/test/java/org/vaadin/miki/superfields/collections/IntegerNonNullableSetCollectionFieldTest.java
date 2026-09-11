package org.vaadin.miki.superfields.collections;

import com.vaadin.browserless.BrowserlessUIContext;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.vaadin.miki.superfields.numbers.SuperIntegerField;

import java.util.Set;
import java.util.TreeSet;

/**
 * @author miki
 * @since 2022-04-28
 */
public class IntegerNonNullableSetCollectionFieldTest {

    private BrowserlessUIContext window;

    private CollectionField<Integer, Set<Integer>> collectionField;

    @BeforeEach
    public void setup() {
        this.window = BrowserlessUIContext.forComponent(() -> {
            this.collectionField = new CollectionField<>(TreeSet::new, (index, controller) -> new FlexLayout(),
                    (CollectionValueComponentProvider<Integer, SuperIntegerField>)(index, controller) -> new SuperIntegerField(null, "element at index "+index).withNullValueAllowed(true));
            return this.collectionField;
        });
    }

    @AfterEach
    public void closeWindow() {
        if (this.window != null) {
            this.window.close();
        }
    }

    // reported in #374
    @Test
    public void testFilterNullItemsWorksByDefault() {
        this.collectionField.add(0);
        final Set<Integer> value = this.collectionField.getValue();
        Assertions.assertTrue(value.isEmpty());
    }

}
