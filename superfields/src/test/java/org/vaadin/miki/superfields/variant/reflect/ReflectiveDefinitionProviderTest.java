package org.vaadin.miki.superfields.variant.reflect;

import org.junit.Assert;
import org.junit.Test;
import org.vaadin.miki.superfields.variant.DataObject;
import org.vaadin.miki.superfields.variant.ObjectPropertyDefinition;
import org.vaadin.miki.superfields.variant.ObjectPropertyDefinitionProvider;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author miki
 * @since 2022-06-23
 */
public class ReflectiveDefinitionProviderTest {

    @Test
    public void testDataObject() {
        final ObjectPropertyDefinitionProvider provider = new ReflectiveDefinitionProvider();
        final Map<String, ObjectPropertyDefinition<DataObject, ?>> definitions = provider.getObjectPropertyDefinitions(DataObject.class, new DataObject()).stream().collect(Collectors.toMap(ObjectPropertyDefinition::getName, Function.identity()));

        Assert.assertEquals(8, definitions.size());

        definitions.forEach((name, def) -> {
            Assert.assertEquals(name, def.getName());
            Assert.assertNotNull(def.getGetter());
        });

        // this method is final
        Assert.assertNull(definitions.get("fixed").getSetter());

    }

}