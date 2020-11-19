package org.vaadin.miki.demo.providers;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import org.vaadin.miki.demo.ComponentProvider;
import org.vaadin.miki.superfields.buttons.MultiClickButton;
import org.vaadin.miki.superfields.buttons.SimpleButtonState;

/**
 * Provides {@link MultiClickButton}.
 * @author miki
 * @since 2020-11-17
 */
public class MultiClickButtonProvider implements ComponentProvider<MultiClickButton> {
    @Override
    public MultiClickButton getComponent() {
        return new MultiClickButton(
                event -> UI.getCurrent().navigate(""),
                new SimpleButtonState("Click to navigate to Info Page").withThemeVariant(ButtonVariant.LUMO_PRIMARY),
                new SimpleButtonState("Are you sure?", VaadinIcon.INFO_CIRCLE.create()),
                new SimpleButtonState("Really navigate away?", VaadinIcon.INFO.create()).withThemeVariant(ButtonVariant.LUMO_ERROR)
        ).withId("multi-click-button");
    }
}
