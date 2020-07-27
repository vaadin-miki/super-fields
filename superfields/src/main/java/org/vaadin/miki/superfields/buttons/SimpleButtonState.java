package org.vaadin.miki.superfields.buttons;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.ButtonVariant;
import org.vaadin.miki.markers.WithComponentAsIconMixin;
import org.vaadin.miki.markers.WithTextMixin;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SimpleButtonState implements ButtonState, WithComponentAsIconMixin<SimpleButtonState>, WithTextMixin<SimpleButtonState> {

    private static final long serialVersionUID = 20200708L;

    /**
     * Turns given texts into corresponding {@link ButtonState}s, one for each text.
     * @param texts Texts to produce {@link ButtonState}s for.
     * @return A non-null list of {@link ButtonState}s.
     */
    public static List<ButtonState> forTexts(String... texts) {
        return Stream.of(texts).map(SimpleButtonState::new).collect(Collectors.toList());
    }

    private String text;

    private Component icon;

    private final Set<String> classNames = new LinkedHashSet<>();

    private final Set<String> themeNames = new LinkedHashSet<>();

    private final Set<ButtonVariant> themeVariants = new LinkedHashSet<>();

    /**
     * Creates a completely blank state.
     */
    public SimpleButtonState() {
        this(null, (Component)null);
    }

    /**
     * Creates button state with corresponding text.
     * @param text Text.
     * @param classNames Optional style class names associated with this state.
     */
    public SimpleButtonState(String text, String... classNames) {
        this(text, null, classNames);
    }

    /**
     * Creates button state with corresponding icon.
     * @param icon A {@link Component} to use as an icon.
     * @param classNames Optional style class names associated with this state.
     */
    public SimpleButtonState(Component icon, String... classNames) {
        this(null, icon, classNames);
    }

    /**
     * Creates button state.
     * @param text Text.
     * @param icon A {@link Component} to use as an icon.
     * @param classNames Optional style class names associated with this state.
     */
    public SimpleButtonState(String text, Component icon, String... classNames) {
        this.setText(text);
        this.setIcon(icon);
        this.classNames.addAll(Arrays.asList(classNames));
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public Component getIcon() {
        return icon;
    }

    @Override
    public void setIcon(Component icon) {
        this.icon = icon;
    }

    /**
     * Adds given style class names to this state.
     * @param classNames Style class names to be associated with this state. If a style class name already exists, it is ignored.
     */
    public void addClassName(String... classNames) {
        this.classNames.addAll(Arrays.asList(classNames));
    }

    /**
     * Removes given style class names from this state.
     * @param classNames Style class names to be removed from this state. If a style class name is not present, nothing happens.
     */
    public void removeClassName(String... classNames) {
        this.classNames.removeAll(Arrays.asList(classNames));
    }

    @Override
    public Set<String> getClassNames() {
        return this.classNames;
    }

    /**
     * Chains {@link #addClassName(String...)} and returns itself.
     * @param classNames Style class names to add.
     * @return This.
     */
    public SimpleButtonState withClassName(String... classNames) {
        this.addClassName(classNames);
        return this;
    }

    /**
     * Chains {@link #removeClassName(String...)} and returns itself.
     * @param classNames Style class names to remove.
     * @return This.
     */
    public SimpleButtonState withoutClassName(String... classNames) {
        this.removeClassName(classNames);
        return this;
    }

    /**
     * Adds given theme names to this state.
     * @param themeNames Theme names to be associated with this state. If a  theme name already exists, it is ignored.
     */
    public void addThemeName(String... themeNames) {
        this.themeNames.addAll(Arrays.asList(themeNames));
    }

    /**
     * Removes given theme names from this state.
     * @param themeNames Theme names to be removed from this state. If a theme name is not present, nothing happens.
     */
    public void removeThemeName(String... themeNames) {
        this.themeNames.removeAll(Arrays.asList(themeNames));
    }

    @Override
    public Set<String> getThemeNames() {
        return this.themeNames;
    }

    /**
     * Chains {@link #addThemeName(String...)} and returns itself.
     * @param themeNames Theme names to add.
     * @return This.
     */
    public SimpleButtonState withThemeName(String... themeNames) {
        this.addThemeName(themeNames);
        return this;
    }

    /**
     * Chains {@link #removeThemeName(String...)} and returns itself.
     * @param themeNames Names to remove.
     * @return This.
     */
    public SimpleButtonState withoutThemeName(String... themeNames) {
        this.removeThemeName(themeNames);
        return this;
    }

    /**
     * Adds given theme variants to this state.
     * @param variants Theme variants to be associated with this state. If a theme variant already exists, it is ignored.
     */
    public void addThemeVariant(ButtonVariant... variants) {
        this.themeVariants.addAll(Arrays.asList(variants));
    }

    /**
     * Removes given theme variants from this state.
     * @param variants Theme variants to be removed from this state. If a variant is not present, nothing happens.
     */
    public void removeThemeVariant(ButtonVariant... variants) {
        this.themeVariants.removeAll(Arrays.asList(variants));
    }

    @Override
    public Set<ButtonVariant> getThemeVariants() {
        return this.themeVariants;
    }

    /**
     * Chains {@link #addThemeVariant(ButtonVariant...)} and returns itself.
     * @param variants Variants to add.
     * @return This.
     */
    public SimpleButtonState withThemeVariant(ButtonVariant... variants) {
        this.addThemeVariant(variants);
        return this;
    }

    /**
     * Chains {@link #removeThemeVariant(ButtonVariant...)} and returns itself.
     * @param variants Variants to remove.
     * @return This.
     */
    public SimpleButtonState withoutThemeVariant(ButtonVariant... variants) {
        this.removeThemeVariant(variants);
        return this;
    }

    /**
     * Returns a deep copy of this object.
     * @return A copy of this object. Changes to the copy do not affect this object.
     */
    public ButtonState copy() {
        return new SimpleButtonState(this.getText(), this.getIcon(), this.getClassNames().toArray(new String[0]))
                .withThemeName(this.getThemeNames().toArray(new String[0]))
                .withThemeVariant(this.getThemeVariants().toArray(new ButtonVariant[0]));
    }

}
