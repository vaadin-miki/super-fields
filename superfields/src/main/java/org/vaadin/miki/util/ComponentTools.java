package org.vaadin.miki.util;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasHelper;
import com.vaadin.flow.component.HasLabel;
import com.vaadin.flow.component.textfield.HasPrefixAndSuffix;
import org.vaadin.miki.markers.HasDatePattern;
import org.vaadin.miki.markers.HasHelperPositionable;
import org.vaadin.miki.markers.HasIcon;
import org.vaadin.miki.markers.HasLocale;
import org.vaadin.miki.markers.HasPlaceholder;

/**
 * @author miki
 * @since 2022-04-11
 */
public class ComponentTools {

    @SuppressWarnings("squid:S3776") // there is no way around other than manually reflecting the interfaces, which is pointless
    public static void copyProperties(Component oldComponent, Component newComponent) {
        if(oldComponent instanceof HasLabel && newComponent instanceof HasLabel)
            ((HasLabel) newComponent).setLabel(((HasLabel) oldComponent).getLabel());
        if(oldComponent instanceof HasIcon && newComponent instanceof HasIcon)
            ((HasIcon) newComponent).setIcon(((HasIcon) oldComponent).getIcon());
        if(oldComponent instanceof HasHelper && newComponent instanceof HasHelper) {
            ((HasHelper) newComponent).setHelperText(((HasHelper) oldComponent).getHelperText());
            ((HasHelper) newComponent).setHelperComponent(((HasHelper) oldComponent).getHelperComponent());
        }
        if(oldComponent instanceof HasHelperPositionable && newComponent instanceof HasHelperPositionable)
            ((HasHelperPositionable) newComponent).setHelperAbove(((HasHelperPositionable) oldComponent).isHelperAbove());
        if(oldComponent instanceof HasPrefixAndSuffix && newComponent instanceof HasPrefixAndSuffix) {
            ((HasPrefixAndSuffix) newComponent).setPrefixComponent(((HasPrefixAndSuffix) oldComponent).getPrefixComponent());
            ((HasPrefixAndSuffix) newComponent).setSuffixComponent(((HasPrefixAndSuffix) oldComponent).getSuffixComponent());
        }
        if(oldComponent instanceof HasLocale && newComponent instanceof HasLocale)
            ((HasLocale) newComponent).setLocale(((HasLocale)oldComponent).getLocale());
        if(oldComponent instanceof HasPlaceholder && newComponent instanceof HasPlaceholder)
            ((HasPlaceholder) newComponent).setPlaceholder(((HasPlaceholder) oldComponent).getPlaceholder());
        if(oldComponent instanceof HasDatePattern && newComponent instanceof HasDatePattern)
            ((HasDatePattern) newComponent).setDatePattern(((HasDatePattern) oldComponent).getDatePattern());
    }

    private ComponentTools() {
        // no instances allowed
    }

}
