package org.vaadin.miki.superfields.checkbox;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.CssImport;
import org.vaadin.miki.markers.WithIdMixin;
import org.vaadin.miki.markers.WithLabelMixin;
import org.vaadin.miki.markers.WithLabelPositionableMixin;
import org.vaadin.miki.markers.WithTitleMixin;
import org.vaadin.miki.markers.WithValueMixin;

import java.util.Objects;

/**
 * A regular {@link Checkbox} that has its read-only state synchronised with enabledness.
 * This exists purely as a workaround for <a href="https://github.com/vaadin/web-components/issues/688">a known issue of Vaadin</a>.
 *
 * @author miki
 * @since 2022-09-14
 */
@SuppressWarnings("squid:S110") // no way around big number of parent classes
@CssImport(value = "./styles/label-positions-checkbox.css", themeFor = "vaadin-checkbox")
public class SuperCheckbox extends Checkbox implements
        WithLabelMixin<SuperCheckbox>, WithValueMixin<AbstractField.ComponentValueChangeEvent<Checkbox, Boolean>, Boolean, SuperCheckbox>,
        WithIdMixin<SuperCheckbox>, WithTitleMixin<SuperCheckbox>, WithLabelPositionableMixin<SuperCheckbox> {

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        super.setEnabled(!readOnly);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setReadOnly(!enabled);
        super.setEnabled(enabled);
    }

    @Override
    public void setTitle(String title) {
        this.getElement().setProperty("title", Objects.requireNonNullElse(title, ""));
    }

    @Override
    public String getTitle() {
        return Objects.requireNonNullElse(this.getElement().getProperty("title"), "");
    }

}
