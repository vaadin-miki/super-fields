package org.vaadin.miki.superfields.object.reflect;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.vaadin.miki.superfields.object.DataObject;
import org.vaadin.miki.superfields.util.factory.FieldGroup;
import org.vaadin.miki.superfields.util.factory.FieldOrder;
import org.vaadin.miki.superfields.object.Property;
import org.vaadin.miki.superfields.object.PropertyProvider;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author miki
 * @since 2022-06-23
 */
public class ReflectiveDefinitionProviderTest {

    private static void assertBasicDefinitions(Map<String, Property<DataObject, ?>> definitions) {
        Assertions.assertEquals(8, definitions.size());

        definitions.forEach((name, def) -> {
            Assertions.assertEquals(name, def.getName());
            Assertions.assertTrue(def.getGetter().isPresent());
            Assertions.assertSame(DataObject.class, def.getOwner());
        });

        // this method is final
        Assertions.assertTrue(definitions.get("fixed").getSetter().isEmpty());

        Assertions.assertSame(BigDecimal.class, definitions.get("currency").getType());
    }

    private static boolean isNonNegativeNumber(Object object) {
        try {
            return (Integer) object >= 0;
        }
        catch(ClassCastException cce) {
            return false;
        }
    }

    @Test
    public void testDataObjectWithoutAnnotationProcessor() {
        final PropertyProvider provider = new ReflectivePropertyProvider();
        final Map<String, Property<DataObject, ?>> definitions = provider.getObjectPropertyDefinitions(DataObject.class, new DataObject()).stream().collect(Collectors.toMap(Property::getName, Function.identity()));

        assertBasicDefinitions(definitions);
    }

    @Test
    public void testDataObjectWithAnnotationProcessor() {
        final PropertyProvider provider = new ReflectivePropertyProvider().withMetadataProvider(new AnnotationMetadataProvider()
                .withRegisteredAnnotation("group", FieldGroup.class, String.class, FieldGroup::value)
                .withRegisteredAnnotation("order", FieldOrder.class, int.class, FieldOrder::value)
        );
        final Map<String, Property<DataObject, ?>> definitions = provider.getObjectPropertyDefinitions(DataObject.class, new DataObject()).stream().collect(Collectors.toMap(Property::getName, Function.identity()));

        assertBasicDefinitions(definitions);
        // all fields except "fixed" have an order
        definitions.values().forEach(def ->
                Assertions.assertTrue("fixed".equals(def.getName()) ^ (def.getMetadata().containsKey("order") && isNonNegativeNumber(def.getMetadata().get("order").getValue())), "property " + def.getName() + " must have an order in metadata " + def.getMetadata().toString())
        );
        // date and number belong to "random-group"
        definitions.values().forEach(def ->
            Assertions.assertTrue(!Set.of("date", "number").contains(def.getName()) ^ (def.getMetadata().containsKey("group") && "random-group".equals(def.getMetadata().get("group").getValue())))
        );
    }

}