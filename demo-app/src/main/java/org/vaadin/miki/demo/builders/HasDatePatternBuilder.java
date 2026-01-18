package org.vaadin.miki.demo.builders;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import org.vaadin.miki.demo.ContentBuilder;
import org.vaadin.miki.demo.Order;
import org.vaadin.miki.markers.HasDatePattern;
import org.vaadin.miki.shared.dates.DatePattern;
import org.vaadin.miki.shared.dates.DatePatterns;

import java.util.function.Consumer;

/**
 * Builds content for {@link HasDatePattern}.
 * @author miki
 * @since 2020-11-19
 */
@Order(70)
public class HasDatePatternBuilder implements ContentBuilder<HasDatePattern> {

    @Override
    public void buildContent(HasDatePattern component, Consumer<Component[]> callback) {
        final ComboBox<DatePattern> patterns = new ComboBox<>("Select date display pattern:",
                DatePatterns.YYYY_MM_DD, DatePatterns.M_D_YYYY_SLASH,
                DatePatterns.DD_MM_YYYY_DOTTED, DatePatterns.DD_MM_YY_OR_YYYY_DOTTED, DatePatterns.D_M_YY_DOTTED,
                DatePatterns.YYYYMMDD, DatePatterns.DDMMYY, DatePatterns.D_MMMM_YYYY
        );
        final Button clearPattern = new Button("Clear pattern", event -> component.setDatePattern(null));
        clearPattern.setDisableOnClick(true);
        patterns.addValueChangeListener(event -> {
            component.setDatePattern(event.getValue());
            clearPattern.setEnabled(true);
        });
        callback.accept(new Component[]{patterns, clearPattern});
    }
}
