package org.vaadin.miki.superfields.object.reflect;

import org.junit.Assert;
import org.junit.Test;
import org.vaadin.miki.superfields.object.DataInterface;
import org.vaadin.miki.superfields.object.DataObject;
import org.vaadin.miki.superfields.object.DataObjectConfiguration;
import org.vaadin.miki.superfields.object.Property;

import java.util.Collection;

public class ReflectivePropertyProviderTest {

    @Test
    public void testProviderUsesActualTypes() {
        final ReflectivePropertyProvider provider = new ReflectivePropertyProvider();
        final Collection<Property<DataInterface,?>> definitions = provider.getObjectPropertyDefinitions(DataInterface.class, new DataObject());
        Assert.assertFalse(definitions.isEmpty());
        DataObjectConfiguration.EXPECTED_FIELDS.keySet().forEach(name -> Assert.assertTrue(String.format("did not find property %s", name), definitions.stream().anyMatch(prop -> name.equals(prop.getName()))));
    }

}