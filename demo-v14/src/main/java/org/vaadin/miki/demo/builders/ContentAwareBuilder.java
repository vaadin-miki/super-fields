package org.vaadin.miki.demo.builders;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import org.vaadin.miki.demo.ContentBuilder;
import org.vaadin.miki.demo.Order;
import org.vaadin.miki.superfields.contentaware.ContentAware;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.function.Consumer;

@Order(165)
public class ContentAwareBuilder implements ContentBuilder<ContentAware> {

    @Override
    public void buildContent(ContentAware component, Consumer<Component[]> callback) {
        final Button addText = new Button("Add text", event -> component.add(new Text("epoch time is "+ LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))));
        final Button addDivAndText = new Button("Add text and div", event -> component.add(new Div(new Text("epoch day is "+ LocalDate.now().toEpochDay()))));

        callback.accept(new Component[]{addText, addDivAndText});
    }
}
