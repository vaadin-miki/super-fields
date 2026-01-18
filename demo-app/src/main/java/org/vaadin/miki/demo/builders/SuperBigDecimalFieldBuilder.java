package org.vaadin.miki.demo.builders;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import org.vaadin.miki.demo.ContentBuilder;
import org.vaadin.miki.demo.Order;
import org.vaadin.miki.superfields.numbers.SuperBigDecimalField;

import java.util.function.Consumer;

/**
 * Supports content for {@link SuperBigDecimalField}.
 *
 * @author miki
 * @since 2021-01-15
 */
@Order(25)
public class SuperBigDecimalFieldBuilder implements ContentBuilder<SuperBigDecimalField> {

  @Override
  @SuppressWarnings("squid:S5411") // no way around boxed value
  public void buildContent(SuperBigDecimalField component, Consumer<Component[]> callback) {
    final Checkbox allowScientificNotation = new Checkbox("Support scientific notation as input? (format: 1 + 3 e 2)?");
    allowScientificNotation.addValueChangeListener(event -> {
      if (event.getValue())
        component.withMaximumExponentDigits(2)
            .withMaximumSignificandFractionDigits(3)
            .withMaximumSignificandIntegerDigits(1);
      else
        component.setMaximumExponentDigits(0);
    });
    callback.accept(new Component[]{allowScientificNotation});
  }
}
