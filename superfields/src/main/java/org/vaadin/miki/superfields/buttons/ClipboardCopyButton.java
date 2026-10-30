package org.vaadin.miki.superfields.buttons;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A button that can always be clicked, no matter the state it is in.
 *
 * @author miki
 * @since 2025-08-16
 */
@Tag("clipboard-copy-button")
@JsModule("./clipboard-copy-button.ts")
public class ClipboardCopyButton extends Div {

  private static final Logger LOGGER = LoggerFactory.getLogger(ClipboardCopyButton.class);

  private HasValue<?, String> source;

  public void setSource(HasValue<?, String> source) {
    this.source = source;
  }

  @ClientCallable
  private void clipboardCopied(String value) {
    LOGGER.info("copied to clipboard: {}", value);
    if (this.source != null)
      this.source.setValue(value);
  }

}
