package org.vaadin.miki.demo.builders;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.vaadin.miki.demo.ContentBuilder;
import org.vaadin.miki.demo.Order;
import org.vaadin.miki.markers.CanModifyText;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.function.Consumer;

@Order(12)
public class CanModifyTextBuilder implements ContentBuilder<CanModifyText> {

    @Override
    public void buildContent(CanModifyText component, Consumer<Component[]> callback) {
        final Button addTextFromServer = new Button("Replace selection", event ->
                component.modifyText(String.format("(utc epoch time is %d)", LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)))
        );
        final HorizontalLayout layout = new HorizontalLayout(new Span("Select something (or not) in the field above, then press the button: "), addTextFromServer);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        callback.accept(new Component[]{layout});
    }
}
