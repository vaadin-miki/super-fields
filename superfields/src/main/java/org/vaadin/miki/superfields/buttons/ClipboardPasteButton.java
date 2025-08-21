package org.vaadin.miki.superfields.buttons;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Tag("clipboard-paste-button")
@JsModule("./clipboard-paste-button.ts")
public class ClipboardPasteButton extends Div  {

  private static final Logger LOGGER = LoggerFactory.getLogger(ClipboardPasteButton.class);

  private HasValue<?, ?> target;

  public HasValue<?, ?> getTarget() {
    return target;
  }

  public void setTarget(HasValue<?, ?> target) {
    this.target = target;
    if(target != null)
      this.getElement().getNode().runWhenAttached(ui -> ui.beforeClientResponse(this, context ->
          this.getElement().callJsFunction(
              "setTargetComponent",
              this.target
          )
      ));
    else this.getElement().getNode().runWhenAttached(ui -> ui.beforeClientResponse(this, context ->
        this.getElement().callJsFunction(
            "clearTargetComponent"
        )
    ));
  }

  @ClientCallable
  private void clipboardPasted(String value) {
    LOGGER.info("pasted from clipboard: {}", value);
  }

}
