package org.vaadin.miki.superfields.buttons;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.dom.DisabledUpdateMode;
import com.vaadin.flow.dom.DomEvent;
import com.vaadin.flow.dom.DomListenerRegistration;
import org.vaadin.miki.markers.WithIconMixin;
import org.vaadin.miki.markers.WithTextMixin;
import org.vaadin.miki.markers.WithTooltipMixin;

/**
 * Base class for all always clickable buttons.
 * Handles server-side stuff only. Client-side implementation done in subclasses.
 *
 * @author miki
 * @since 2026-11-02
 */
public abstract class AbstractAlwaysClickableButton extends Component implements WithTextMixin<ClipboardCopyButton>, WithIconMixin<ClipboardCopyButton>, WithTooltipMixin<ClipboardCopyButton> {

  private static final String EVENT_NAME = "button-pressed";
  private static final String TEXT_PROPERTY = "text";
  private static final String ICON_SLOT_NAME = "icon";

  private final DomListenerRegistration eventRegistration;

  private Icon icon;

  /**
   * Creates the button, with given data to listen to from events.
   * @param customEventData Events to be synchronised from the custom event.
   */
  protected AbstractAlwaysClickableButton(String... customEventData) {
    super();

    // handle events from client
    this.eventRegistration = this.getElement()
        .addEventListener(EVENT_NAME, this::handleDomEvent);
    // register details for the event handling
    for (String data: customEventData)
      this.eventRegistration.addEventData(data);
    // listen to events no matter what
    this.eventRegistration.setDisabledUpdateMode(DisabledUpdateMode.ALWAYS);
  }

  /**
   * Returns details of the inner click/custom event registration.
   * Custom event data is already added. Disable update mode is initially set to {@link DisabledUpdateMode#ALWAYS}.
   * @return A non-{@code null} registration details.
   */
  protected final DomListenerRegistration getEventRegistration() {
    return this.eventRegistration;
  }

  /**
   * Handles the event from the client.
   * @param event Event details.
   */
  protected abstract void handleDomEvent(DomEvent event);

  @Override
  public void setText(String text) {
    this.getElement().setProperty(TEXT_PROPERTY, text);
  }

  @Override
  public String getText() {
    return this.getElement().getProperty(TEXT_PROPERTY);
  }

  @Override
  public void setIcon(Icon icon) {
    if (this.icon != null)
      this.getElement().removeChild(this.icon.getElement());
    this.icon = icon;
    if (this.icon != null) {
      this.icon.getElement().setAttribute("slot", ICON_SLOT_NAME);
      this.getElement().appendChild(this.icon.getElement());
    }
  }

  @Override
  public Icon getIcon() {
    return this.icon;
  }

  @Override
  public Tooltip setTooltipText(String text) {
    // without this explicit setup the tooltip is not rendered
    final Tooltip result = Tooltip.forComponent(this);
    result.setText(text);
    return result;
  }

  @Override
  public Tooltip setTooltipMarkdown(String markdown) {
    final Tooltip result = Tooltip.forComponent(this);
    result.setMarkdown(markdown);
    return result;
  }

}
