package org.vaadin.miki.demo.builders;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.vaadin.miki.demo.ContentBuilder;
import org.vaadin.miki.demo.Order;
import org.vaadin.miki.events.text.TextSelectionNotifier;
import org.vaadin.miki.markers.CanReceiveSelectionEventsFromClient;
import org.vaadin.miki.markers.CanSelectText;

import java.util.function.Consumer;

/**
 * Builds content for {@link CanSelectText}.
 * @author miki
 * @since 2020-11-18
 */
@Order(10)
public class CanSelectTextBuilder implements ContentBuilder<CanSelectText> {

    @Override
    public void buildContent(CanSelectText component, Consumer<Component[]> callback) {
        final Button selectAll = new Button("Select all", event -> component.selectAll());
        final Button selectNone = new Button("Select none", event -> component.selectNone());
        final HorizontalLayout layout = new HorizontalLayout(new Span("Type something in the field, then click one of the buttons:"), selectAll, selectNone);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        callback.accept(new Component[]{
                layout
        });
        if(component instanceof CanReceiveSelectionEventsFromClient canReceive) {
            final Checkbox receiveFromClient = new Checkbox("Allow selection events initiated by keyboard or mouse?",
                    event -> canReceive.setReceivingSelectionEventsFromClient(event.getValue()));
            callback.accept(new Component[] {receiveFromClient});
        }
        if(component instanceof TextSelectionNotifier<?>) {
            final Span selection = new Span();
            ((TextSelectionNotifier<?>) component).addTextSelectionListener(event -> selection.setText(event.getSelectedText()));
            Icon icon = VaadinIcon.INFO_CIRCLE.create();
            icon.setColor("green");
            icon.getElement().setAttribute("title", "When the component does not receive events from the browser, selection events will only be called for server-side initiated actions.");
            callback.accept(new Component[]{
                    new HorizontalLayout(new Span("Most recently selected text: <"), selection, new Span(">"), icon)
            });
        }
    }
}
