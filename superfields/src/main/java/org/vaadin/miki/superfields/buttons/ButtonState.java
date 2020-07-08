package org.vaadin.miki.superfields.buttons;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

/**
 * Encapsulation of a state of a button (text, icon, style names, etc.).
 * @author miki
 * @since 2020-07-07
 */
public interface ButtonState extends Serializable {

    /**
     * Returns current text of the button.
     * @return Text.
     */
    String getText();

    /**
     * Returns current icon of the button.
     * @return Icon.
     */
    Component getIcon();

    /**
     * Returns current (ordered) set of style class names associated with this state.
     * @return An ordered set of style class names. Changes to the returned object affect this object.
     */
    Set<String> getClassNames();

    /**
     * Returns current (ordered) set of theme names associated with this state.
     * @return An ordered set of theme names. Changes to the returned object affect this object.
     */
    Set<String> getThemeNames();

    /**
     * Returns current (ordered) set of theme variants associated with this state.
     * @return An ordered set of theme variants. Changes to the returned object affect this object.
     */
    Set<ButtonVariant> getThemeVariants();

    /**
     * Converts a {@link Button} into information about its state.
     * @param button Button to get state from.
     * @return A {@link ButtonState}.
     */
    static ButtonState of(Button button) {
        final String text = button.getText();
        final Component icon = button.getIcon();
        final Set<String> classes = Collections.unmodifiableSet(button.getClassNames());
        final Set<String> themes = Collections.unmodifiableSet(button.getThemeNames());
        return new ButtonState() {
            @Override
            public String getText() {
                return text;
            }

            @Override
            public Component getIcon() {
                return icon;
            }

            @Override
            public Set<String> getClassNames() {
                return classes;
            }

            @Override
            public Set<String> getThemeNames() {
                return themes;
            }

            @Override
            public Set<ButtonVariant> getThemeVariants() {
                return Collections.emptySet();
            }
        };
    }

}
