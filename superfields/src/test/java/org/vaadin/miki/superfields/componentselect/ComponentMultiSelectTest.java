package org.vaadin.miki.superfields.componentselect;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.vaadin.miki.superfields.layouts.FlexLayoutHelpers;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ComponentMultiSelectTest {

  public enum Option {THESE, ARE, THE, OPTIONS, FOR, MULTISELECT}

  private ComponentMultiSelect<Button, Option> select;
  private int eventCounter = 0;

  @Before
  public void setup() {
    this.eventCounter = 0;
    this.select = new ComponentMultiSelect<Button, Option>(FlexLayoutHelpers::row, ComponentSelectHelpers.simpleComponentFactory(Button::new), Option.values());
    this.select.addValueChangeListener(event -> eventCounter++);
  }

  @Test
  public void testEmptyAtStartAndAssignValues() {
    Assert.assertTrue(this.select.getValue().isEmpty());
    final Set<Option> value = new HashSet<>(Arrays.asList(Option.ARE, Option.OPTIONS, Option.MULTISELECT));
    this.select.setValue(value);
    Assert.assertEquals(value, this.select.getValue());
  }

  @Test
  public void testCorrectButtonsSelected() {
    this.select
        .withComponentSelectedAction((index, button) -> button.addThemeVariants(ButtonVariant.LUMO_PRIMARY))
        .setComponentDeselectedAction((index, button) -> button.removeThemeVariants(ButtonVariant.LUMO_PRIMARY));

    final Set<Option> value = new HashSet<>(Arrays.asList(Option.MULTISELECT, Option.THE));
    this.select.setValue(value);
    for(int zmp1=0; zmp1<Option.values().length; zmp1++)
      Assert.assertEquals(this.select.getValue().contains(Option.values()[zmp1]), this.select.getComponent(zmp1).getThemeNames().contains(ButtonVariant.LUMO_PRIMARY.getVariantName()));
  }

  @Test
  public void testMaximumIsRespected() {
    this.select.setMaximumSelectionSize(2);
    this.select.setValue(new HashSet<>(Arrays.asList(Option.THESE, Option.FOR, Option.OPTIONS)));
    // only two values will be selected of the passed value
    final Set<Option> value = this.select.getValue();
    Assert.assertEquals(2, value.size());
    // trying to select a third option changes nothing
    int eventsSoFar = this.eventCounter;
    this.select.getComponent(5).click();
    Assert.assertEquals(value, this.select.getValue());
    Assert.assertEquals(eventsSoFar, this.eventCounter);
  }

  @Test
  public void testValueChangeByButtonClicks() {
    this.select.getComponent(0).click();
    this.select.getComponent(2).click();
    Assert.assertEquals(new HashSet<>(Arrays.asList(Option.THESE, Option.THE)), this.select.getValue());
    Assert.assertEquals(2, this.eventCounter);
  }

  @Test
  public void testSetMaximumReducesSelectionIfNeeded() {
    this.select
        .withComponentSelectedAction((index, button) -> button.addThemeVariants(ButtonVariant.LUMO_PRIMARY))
        .withComponentDeselectedAction((index, button) -> button.removeThemeVariants(ButtonVariant.LUMO_PRIMARY))
        .setValue(new HashSet<>(Arrays.asList(Option.THESE, Option.FOR, Option.MULTISELECT)));
    this.select.setMaximumSelectionSize(2);
    Assert.assertEquals(2, this.select.getValue().size());
    // also, value change event should be fired
    Assert.assertEquals(2, this.eventCounter);
    // and only two buttons must be marked selected
    int count = 0;
    for(int zmp1=0; zmp1 < this.select.getComponentCount(); zmp1++)
      if(this.select.getComponent(zmp1).getThemeNames().contains(ButtonVariant.LUMO_PRIMARY.getVariantName()))
        count++;
    Assert.assertEquals(2, count);
  }

}
