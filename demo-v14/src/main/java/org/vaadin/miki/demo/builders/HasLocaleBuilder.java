package org.vaadin.miki.demo.builders;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import org.vaadin.miki.demo.ContentBuilder;
import org.vaadin.miki.markers.HasLocale;

import java.util.Locale;
import java.util.function.Consumer;

/**
 * Builds content for {@link HasLocale}.
 * @author miki
 * @since 2020-11-19
 */
public class HasLocaleBuilder implements ContentBuilder<HasLocale> {

    @Override
    public void buildContent(HasLocale component, Consumer<Component[]> callback) {
        final ComboBox<Locale> locales = new ComboBox<>("Select locale:", new Locale("pl", "PL"), Locale.UK, Locale.FRANCE, Locale.GERMANY, Locale.CHINA);
        locales.setItemLabelGenerator(locale -> locale.getDisplayCountry() + " / "+locale.getDisplayLanguage());
        locales.setAllowCustomValue(false);
        locales.addValueChangeListener(event -> component.setLocale(event.getValue()));
        callback.accept(new Component[]{locales});
    }
}
