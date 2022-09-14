package org.vaadin.miki.superfields.checkbox;

import com.vaadin.flow.component.checkbox.Checkbox;

/**
 * A regular {@link Checkbox} that has its read-only state synchronised with enabledness.
 * This exists purely as a workaround for <a href="https://github.com/vaadin/web-components/issues/688">a known issue of Vaadin</a>.
 *
 * @author miki
 * @since 2022-09-14
 */
@SuppressWarnings("squid:S110") // no way around big number of parent classes
public class SuperCheckbox extends Checkbox {

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
}
