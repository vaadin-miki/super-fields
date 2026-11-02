package org.vaadin.miki.superfields.buttons;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.dom.DomEvent;
import com.vaadin.flow.shared.Registration;
import org.vaadin.miki.events.clipboard.ClipboardPasteEvent;
import org.vaadin.miki.events.clipboard.ClipboardPasteListener;
import org.vaadin.miki.events.clipboard.ClipboardPasteNotifier;

@Tag("clipboard-paste-button")
@JsModule("./clipboard-paste-button.ts")
public class ClipboardPasteButton extends AbstractAlwaysClickableButton implements ClipboardPasteNotifier<ClipboardPasteButton> {

  private static final String PASTED_VALUE_EVENT_PATH = "event.detail.value";

  public ClipboardPasteButton() {
    super(PASTED_VALUE_EVENT_PATH);
    this.setIcon(VaadinIcon.PASTE.create());
  }

  @Override
  protected void handleDomEvent(DomEvent event) {
    this.onValuePasted(event.getEventData().get(PASTED_VALUE_EVENT_PATH).asString());
  }

  private void onValuePasted(String value) {
    this.fireEvent(new ClipboardPasteEvent<>(this, value));
  }

  @Override
  @SuppressWarnings("unchecked")
  public Registration addClipboardPasteListener(ClipboardPasteListener<ClipboardPasteButton> listener) {
    return this.getEventBus().addListener((Class<ClipboardPasteEvent<ClipboardPasteButton>>) (Class<?>) ClipboardPasteEvent.class, listener);
  }
}
