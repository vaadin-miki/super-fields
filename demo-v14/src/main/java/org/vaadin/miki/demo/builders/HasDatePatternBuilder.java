package org.vaadin.miki.demo.builders;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.vaadin.miki.demo.ContentBuilder;
import org.vaadin.miki.markers.HasDatePattern;
import org.vaadin.miki.shared.dates.DatePattern;
import org.vaadin.miki.shared.dates.DatePatterns;
import org.vaadin.miki.superfields.dates.SuperDatePicker;

import java.util.function.Consumer;

/**
 * Builds content for {@link HasDatePattern}.
 * @author miki
 * @since 2020-11-19
 */
public class HasDatePatternBuilder implements ContentBuilder<HasDatePattern> {

    @Override
    public void buildContent(HasDatePattern component, Consumer<Component[]> callback) {
        final ComboBox<DatePattern> patterns = new ComboBox<>("Select date display pattern:",
                DatePatterns.YYYY_MM_DD, DatePatterns.M_D_YYYY_SLASH,
                DatePatterns.DD_MM_YYYY_DOTTED, DatePatterns.DD_MM_YY_OR_YYYY_DOTTED, DatePatterns.D_M_YY_DOTTED,
                DatePatterns.YYYYMMDD, DatePatterns.DDMMYY
        );
        final Button clearPattern = new Button("Clear pattern", event -> component.setDatePattern(null));
        clearPattern.setDisableOnClick(true);
        final Component clearPatternOrContainer;
        // issue #87 requires a note
        if(component instanceof SuperDatePicker)
            clearPatternOrContainer = clearPattern;
        else {
            Icon icon = new Icon(VaadinIcon.INFO);
            icon.setColor("green");
            icon.getElement().setAttribute("title", "Setting pattern does not work out of the box for SuperDateTimePicker if it is in an invisible layout. See issue #87, https://github.com/vaadin-miki/super-fields/issues/87.");
            clearPatternOrContainer = new HorizontalLayout(clearPattern, icon);
            ((HorizontalLayout)clearPatternOrContainer).setAlignItems(FlexComponent.Alignment.CENTER);
        }
        patterns.addValueChangeListener(event -> {
            component.setDatePattern(event.getValue());
            clearPattern.setEnabled(true);
        });
        callback.accept(new Component[]{patterns, clearPatternOrContainer});
    }
}
