package org.vaadin.miki.superfields.collections;

import com.github.mvysny.kaributesting.v10.MockVaadin;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.vaadin.miki.superfields.numbers.SuperIntegerField;

import java.util.Set;
import java.util.TreeSet;

/**
 * @author miki
 * @since 2022-04-28
 */
public class IntegerNonNullableSetCollectionFieldTest {

    private CollectionField<Integer, Set<Integer>> collectionField;

    @Before
    public void setup() {
        MockVaadin.setup();
        this.collectionField = new CollectionField<>(TreeSet::new, (index, controller) -> new FlexLayout(),
                (CollectionValueComponentProvider<Integer, SuperIntegerField>)(index, controller) -> new SuperIntegerField(null, "element at index "+index).withNullValueAllowed(true));
    }

    @After
    public void tearDown() {
        MockVaadin.tearDown();
    }

    // reported in #374
    @Test
    public void testFilterNullItemsWorksByDefault() {
        this.collectionField.add(0);
        final Set<Integer> value = this.collectionField.getValue();
        Assert.assertTrue(value.isEmpty());
    }

}
