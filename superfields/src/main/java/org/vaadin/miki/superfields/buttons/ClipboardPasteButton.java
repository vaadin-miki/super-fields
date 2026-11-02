package org.vaadin.miki.superfields.buttons;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.shared.Registration;
import org.vaadin.miki.events.clipboard.ClipboardPasteEvent;
import org.vaadin.miki.events.clipboard.ClipboardPasteListener;
import org.vaadin.miki.events.clipboard.ClipboardPasteNotifier;

@Tag("clipboard-paste-button")
@JsModule("./clipboard-paste-button.ts")
public class ClipboardPasteButton extends Div implements ClipboardPasteNotifier<ClipboardPasteButton> {

  @ClientCallable
  private void clipboardPasted(String value) {
    this.fireEvent(new ClipboardPasteEvent<>(this, value));
  }

  @Override
  @SuppressWarnings("unchecked")
  public Registration addClipboardPasteListener(ClipboardPasteListener<ClipboardPasteButton> listener) {
    return this.getEventBus().addListener((Class<ClipboardPasteEvent<ClipboardPasteButton>>) (Class<?>) ClipboardPasteEvent.class, listener);
  }
}
