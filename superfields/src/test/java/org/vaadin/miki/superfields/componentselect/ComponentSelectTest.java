package org.vaadin.miki.superfields.componentselect;

import com.vaadin.browserless.BrowserlessUIContext;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.customfield.CustomField;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.vaadin.miki.superfields.layouts.FlexLayoutHelpers;

/**
 * @author miki
 * @since 2023-11-17
 */
public class ComponentSelectTest {

  private BrowserlessUIContext window;

  private static final String[] OPTIONS = {"these", "are", "the", "test", "options"};
  
  private int mostRecentlySelectedButton = -1;
  private int eventCounter = 0;
  private ComponentSelect<Button, String> select;

  private void valueChanged(AbstractField.ComponentValueChangeEvent<CustomField<String>, String> event) {
    this.eventCounter++;
  }

  @BeforeEach
  public void setup() {
    this.mostRecentlySelectedButton = -1;
    this.eventCounter = 0;
    this.window = BrowserlessUIContext.forComponent(() -> {
      this.select = new ComponentSelect<Button, String>(
            FlexLayoutHelpers::row,
            ComponentSelectHelpers.simpleComponentFactory(Button::new),
            OPTIONS)
          .withComponentSelectedAction((index, button) -> this.mostRecentlySelectedButton = index);
      return this.select;
    });
    this.select.addValueChangeListener(this::valueChanged);
  }

  @AfterEach
  public void closeWindow() {
    if (this.window != null) {
      this.window.close();
    }
  }

  @Test
  public void testValueChangeWorks() {
    Assertions.assertEquals(ComponentSelect.NO_SELECTION, this.select.getSelectedIndex());
    Assertions.assertNull(this.select.getValue());
    Assertions.assertEquals(0, this.eventCounter);
    Assertions.assertEquals(-1, this.mostRecentlySelectedButton);

    Assertions.assertEquals(OPTIONS.length, this.select.getComponentCount());
    for(int zmp1=0; zmp1<OPTIONS.length; zmp1++)
      Assertions.assertEquals(OPTIONS[zmp1], this.select.getComponent(zmp1).getText());

    this.select.setValue(OPTIONS[1]);
    Assertions.assertEquals(1, this.select.getSelectedIndex());
    Assertions.assertEquals(1, this.eventCounter);
    Assertions.assertEquals(1, this.mostRecentlySelectedButton);
    Assertions.assertEquals(OPTIONS[1], this.select.getValue());

    this.select.setValue(OPTIONS[0]);
    Assertions.assertEquals(0, this.select.getSelectedIndex());
    Assertions.assertEquals(2, this.eventCounter);
    Assertions.assertEquals(0, this.mostRecentlySelectedButton);
    Assertions.assertEquals(OPTIONS[0], this.select.getValue());

    // should change to no selection
    this.select.setValue("DOES NOT COMPUTE");
    Assertions.assertEquals(-1, this.select.getSelectedIndex());
    Assertions.assertEquals(3, this.eventCounter);
    // no button was selected
    Assertions.assertEquals(0, this.mostRecentlySelectedButton);
    Assertions.assertNull(this.select.getValue());
  }

  @Test
  public void testNullValueDisallowed() {
    this.select.setNullValueAllowed(false);
    this.select.setValue(OPTIONS[2]);
    this.select.setValue(null);
    // null value disallowed, no value change should happen
    Assertions.assertEquals(OPTIONS[2], this.select.getValue());
    Assertions.assertEquals(2, this.select.getSelectedIndex());
    Assertions.assertEquals(1, this.eventCounter);
    Assertions.assertEquals(2, this.mostRecentlySelectedButton);
  }

  @Test
  public void testButtonClickSelectsDeselects() {
    this.select.setValue(OPTIONS[2]);
    this.select.getComponent(2).click();
    // null selection is allowed by default
    Assertions.assertNull(this.select.getValue());
    Assertions.assertEquals(2, this.eventCounter);
  }

  @Test
  public void testButtonClickNullDisallowed() {
    this.select.setNullValueAllowed(false);
    this.select.setValue(OPTIONS[2]);
    this.select.getComponent(2).click();
    // value should not be changed, it is disallowed
    Assertions.assertEquals(OPTIONS[2], this.select.getValue());
    Assertions.assertEquals(1, this.eventCounter);
  }

  @Test
  public void testVariantChanges() {
    this.select.withComponentSelectedAction(ComponentSelectHelpers.addVariant(ButtonVariant.LUMO_PRIMARY))
        .setComponentDeselectedAction(ComponentSelectHelpers.removeVariant(ButtonVariant.LUMO_PRIMARY));
    // nothing is selected at start
    for(int zmp1=0; zmp1<OPTIONS.length; zmp1++)
      Assertions.assertFalse(this.select.getComponent(zmp1).getThemeNames().contains(ButtonVariant.LUMO_PRIMARY.getVariantName()));

    // select each option in turn, there should be only one selected button at a time
    for(int selection = 0; selection < OPTIONS.length; selection++) {
      // alternative way of setting value
      this.select.setSelectedIndex(selection);
      Assertions.assertEquals(OPTIONS[selection], this.select.getValue());
      Assertions.assertEquals(selection + 1, this.eventCounter);
      for (int zmp1 = 0; zmp1 < OPTIONS.length; zmp1++)
        Assertions.assertEquals(zmp1 == selection, this.select.getComponent(zmp1).getThemeNames().contains(ButtonVariant.LUMO_PRIMARY.getVariantName()));
    }

    // select items by clicking them
    // select each option in turn, there should be only one selected button at a time
    for(int selection = 0; selection < OPTIONS.length; selection++) {
      // alternative way of setting value
      this.select.getComponent(selection).click();
      Assertions.assertEquals(OPTIONS[selection], this.select.getValue());
      for (int zmp1 = 0; zmp1 < OPTIONS.length; zmp1++)
        Assertions.assertEquals(zmp1 == selection, this.select.getComponent(zmp1).getThemeNames().contains(ButtonVariant.LUMO_PRIMARY.getVariantName()));
    }

    // now do the same, but disallow null value
    this.select.setNullValueAllowed(false);
    // select items by clicking them
    // select each option in turn, there should be only one selected button at a time
    for(int selection = 0; selection < OPTIONS.length; selection++) {
      // alternative way of setting value
      this.select.getComponent(selection).click();
      Assertions.assertEquals(OPTIONS[selection], this.select.getValue());
      for (int zmp1 = 0; zmp1 < OPTIONS.length; zmp1++)
        Assertions.assertEquals(zmp1 == selection, this.select.getComponent(zmp1).getThemeNames().contains(ButtonVariant.LUMO_PRIMARY.getVariantName()));
    }

    this.select.setNullValueAllowed(true);

    // deselect
    this.select.setValue(null);
    for(int zmp1=0; zmp1<OPTIONS.length; zmp1++)
      Assertions.assertFalse(this.select.getComponent(zmp1).getThemeNames().contains(ButtonVariant.LUMO_PRIMARY.getVariantName()));
  }

}