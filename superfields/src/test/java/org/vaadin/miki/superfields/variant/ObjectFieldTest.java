package org.vaadin.miki.superfields.variant;

import com.github.mvysny.kaributesting.v10.MockVaadin;
import com.vaadin.flow.component.HasValue;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.vaadin.miki.superfields.layouts.FlexLayoutHelpers;

import java.util.Map;

/**
 * @author miki
 * @since 2022-06-23
 */
public class ObjectFieldTest {

    private ObjectField<DataObject> field;

    @Before
    public void setup() {
        MockVaadin.setup();
        this.field = new ObjectField<>(DataObject.class, DataObject::new, FlexLayoutHelpers::column);
    }

    @After
    public void tearDown() {
        MockVaadin.tearDown();
    }

    @Test
    public void testInitialisedWithAllProperties() {
        final Map<ObjectPropertyDefinition<DataObject, ?>, HasValue<?, ?>> map = this.field.getPropertiesAndComponents();
        Assert.assertEquals(8, map.size());
    }

}