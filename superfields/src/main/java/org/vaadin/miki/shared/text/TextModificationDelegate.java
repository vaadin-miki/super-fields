package org.vaadin.miki.shared.text;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventBus;
import com.vaadin.flow.function.SerializableSupplier;
import org.vaadin.miki.markers.CanModifyText;
import org.vaadin.miki.markers.CanReceiveSelectionEventsFromClient;
import org.vaadin.miki.markers.CanSelectText;

/**
 * A delegate to handle {@link CanModifyText} in various components.
 * Extension of the {@link TextSelectionDelegate}, as the js function is in the same module, but should not be exposed to all components.
 *
 * @author miki
 * @since 2021-03-26
 */
public class TextModificationDelegate<C extends Component & CanSelectText & CanReceiveSelectionEventsFromClient & CanModifyText>
        extends TextSelectionDelegate<C>
        implements CanModifyText {

    /**
     * Creates the delegate for a given component.
     *
     * @param source              Source of all events, data, etc.
     * @param eventBus            Event bus to use for firing events. Typically, {@code source.getEventBus()}.
     * @param stringValueSupplier Method to obtain current value of the component as a {@link String}.
     */
    public TextModificationDelegate(C source, ComponentEventBus eventBus, SerializableSupplier<String> stringValueSupplier) {
        super(source, eventBus, stringValueSupplier);
    }

    @Override
    public void modifyText(String replacement, int from, int to) {
        // the js function lives in text-selection-mixin.js, just because it was much easier
        this.getSourceElement().getNode().runWhenAttached(ui -> ui.beforeClientResponse(this.getSource(), context ->
                this.getSourceElement().callJsFunction("replaceText", this.getSource().getElement(), replacement, from, to)
        ));
    }

}
