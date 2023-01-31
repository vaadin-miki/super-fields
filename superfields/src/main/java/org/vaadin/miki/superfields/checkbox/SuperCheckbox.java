package org.vaadin.miki.superfields.checkbox;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.CssImport;
import org.vaadin.miki.markers.WithIdMixin;
import org.vaadin.miki.markers.WithLabelMixin;
import org.vaadin.miki.markers.WithLabelPositionableMixin;
import org.vaadin.miki.markers.WithTooltipMixin;
import org.vaadin.miki.markers.WithValueMixin;

/**
 * A regular {@link Checkbox} that can be made read-only (it becomes disabled when set to read-only).
 * While this exists mostly as a workaround for <a href="https://github.com/vaadin/web-components/issues/688">a known issue of Vaadin</a>,
 * it also supports label position (though only {@link org.vaadin.miki.shared.labels.LabelPosition}{@code #BEFORE_*}, thus allowing
 * the label to be positioned on the other side of the actual checkbox).
 *
 * @author miki
 * @since 2022-09-14
 */
@SuppressWarnings("squid:S110") // no way around big number of parent classes
@CssImport(value = "./styles/label-positions-checkbox.css", themeFor = "vaadin-checkbox")
public class SuperCheckbox extends Checkbox implements
        WithLabelMixin<SuperCheckbox>, WithValueMixin<AbstractField.ComponentValueChangeEvent<Checkbox, Boolean>, Boolean, SuperCheckbox>,
        WithIdMixin<SuperCheckbox>, WithLabelPositionableMixin<SuperCheckbox>, WithTooltipMixin<SuperCheckbox> {

    private boolean enabled = true;
    private boolean readOnly = false;

    protected boolean isReallyEnabled() {
        return this.enabled && !this.readOnly;
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
        super.setReadOnly(readOnly);
        super.setEnabled(this.isReallyEnabled());
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        super.setEnabled(this.isReallyEnabled());
    }

    @Override
    public void setTooltipText(String title) {
        this.getElement().setProperty("title", title == null ? "" : title);
    }

    @Override
    public String getTooltipText() {
        final String title = this.getElement().getProperty("title");
        return title == null ? "" : title;
    }

}
