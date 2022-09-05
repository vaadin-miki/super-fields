package org.vaadin.miki.superfields.variant.reflect;

import org.junit.Assert;
import org.junit.Test;
import org.vaadin.miki.superfields.variant.DataObject;
import org.vaadin.miki.superfields.variant.FieldGroup;
import org.vaadin.miki.superfields.variant.FieldOrder;
import org.vaadin.miki.superfields.variant.ObjectPropertyDefinition;
import org.vaadin.miki.superfields.variant.ObjectPropertyDefinitionProvider;

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

    private static void assertBasicDefinitions(Map<String, ObjectPropertyDefinition<DataObject, ?>> definitions) {
        Assert.assertEquals(8, definitions.size());

        definitions.forEach((name, def) -> {
            Assert.assertEquals(name, def.getName());
            Assert.assertTrue(def.getGetter().isPresent());
            Assert.assertSame(DataObject.class, def.getOwner());
        });

        // this method is final
        Assert.assertTrue(definitions.get("fixed").getSetter().isEmpty());

        Assert.assertSame(BigDecimal.class, definitions.get("currency").getType());
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
        final ObjectPropertyDefinitionProvider provider = new ReflectiveDefinitionProvider();
        final Map<String, ObjectPropertyDefinition<DataObject, ?>> definitions = provider.getObjectPropertyDefinitions(DataObject.class, new DataObject()).stream().collect(Collectors.toMap(ObjectPropertyDefinition::getName, Function.identity()));

        assertBasicDefinitions(definitions);
    }

    @Test
    public void testDataObjectWithAnnotationProcessor() {
        final ObjectPropertyDefinitionProvider provider = new ReflectiveDefinitionProvider().withMetadataProvider(new AnnotationMetadataProvider()
                .withRegisteredAnnotation("group", FieldGroup.class, String.class, FieldGroup::value)
                .withRegisteredAnnotation("order", FieldOrder.class, int.class, FieldOrder::value)
        );
        final Map<String, ObjectPropertyDefinition<DataObject, ?>> definitions = provider.getObjectPropertyDefinitions(DataObject.class, new DataObject()).stream().collect(Collectors.toMap(ObjectPropertyDefinition::getName, Function.identity()));

        assertBasicDefinitions(definitions);
        // all fields except "fixed" have an order
        definitions.values().forEach(def ->
                Assert.assertTrue("property " + def.getName() + " must have an order in metadata " + def.getMetadata().toString(),
                        "fixed".equals(def.getName()) ^ (def.getMetadata().containsKey("order") && isNonNegativeNumber(def.getMetadata().get("order").getValue())))
        );
        // date and number belong to "random-group"
        definitions.values().forEach(def ->
            Assert.assertTrue(!Set.of("date", "number").contains(def.getName()) ^ (def.getMetadata().containsKey("group") && "random-group".equals(def.getMetadata().get("group").getValue())))
        );
    }

}