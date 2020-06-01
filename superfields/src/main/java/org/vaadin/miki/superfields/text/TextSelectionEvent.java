package org.vaadin.miki.superfields.text;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;

import java.util.Optional;

/**
 * Component event with information about selected text.
 * @param <T> Type of component that broadcast the event.
 * @author miki
 * @since 2020-05-30
 */
public final class TextSelectionEvent<T extends Component & CanSelectText> extends ComponentEvent<T> {

    public static final int NO_SELECTION = -1;

    private final int selectionStart;
    private final int selectionEnd;
    private final String selectedText;

    /**
     * Creates a new event using the given source and indicator whether the
     * event originated from the client side or the server side.
     *  @param source     the source component
     * @param fromClient <code>true</code> if the event originated from the client
     * @param selectionStart Where selection starts in the component. Can be {@link #NO_SELECTION}.
     * @param selectionEnd Where selection ends in the component. Can be {@link #NO_SELECTION}.
     * @param selectedText What is the selected text. Can be empty.
     */
    public TextSelectionEvent(T source, boolean fromClient, int selectionStart, int selectionEnd, String selectedText) {
        super(source, fromClient);
        this.selectionStart = selectionStart;
        this.selectionEnd = selectionEnd;
        this.selectedText = Optional.ofNullable(selectedText).orElse("");
    }

    public int getSelectionStart() {
        return selectionStart;
    }

    public int getSelectionEnd() {
        return selectionEnd;
    }

    public String getSelectedText() {
        return selectedText;
    }

    public boolean isAnythingSelected() {
        return this.selectionStart != NO_SELECTION && this.selectionEnd != this.selectionStart && !this.selectedText.isEmpty();
    }
}
