package org.vaadin.miki.demo.providers;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.TextField;
import org.vaadin.miki.superfields.lazyload.LazyLoad;

/**
 * Container class for generators to item grid.
 * @author miki
 * @since 2020-11-18
 */
public final class ItemGridGenerators {

    /**
     * Simple generator (row, column and type name).
     * @param type Type to generate cell for.
     * @param row Row number.
     * @param column Column number.
     * @return A grid cell.
     */
    public static Component generateParagraph(Class<? extends Component> type, int row, int column) {
        Paragraph result = new Paragraph(type == null ? "(no data)" : type.getSimpleName());
        result.setTitle(String.format("row %d, column %d", row, column));
        return result;
    }

    /**
     * More elaborate cell generator.
     * @param type Type to generate cell for.
     * @param row Row number.
     * @param column Column number.
     * @return A grid cell.
     */
    public static Component generateDiv(Class<? extends Component> type, int row, int column) {
        return new LazyLoad<Div>(() -> {
            final Div result = new Div();
            result.addClassNames("item-grid-cell");
            result.add(new Span(String.format("Row: %d. Column: %d.", row, column)));
            final TextField text = new TextField("Class name: ");
            if(type != null) {
                text.setValue(type.getSimpleName());
                text.addClassName("highlighted");
                text.addBlurListener(event -> text.setValue(type.getSimpleName()));
            }
            else {
                text.setValue("(nothing)");
                text.setReadOnly(true);
            }
            result.add(text);
            return result;
        });
    }

    private ItemGridGenerators() {
        // no instances allowed
    }

}
