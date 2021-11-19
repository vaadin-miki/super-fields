package org.vaadin.miki.superfields;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author miki
 * @since 2021-11-19
 */
public class SanityTest {

    @Test
    public void noCyclesInCode() {
        final JavaClasses classes = new ClassFileImporter().importPackages("org.vaadin.miki");

        final var rule = SlicesRuleDefinition.slices().matching("org.vaadin.miki.(*)..").should().beFreeOfCycles();

        final var result = rule.evaluate(classes);

        Assert.assertFalse(String.join(", ", result.getFailureReport().getDetails()), result.hasViolation());
    }

}
