package org.vaadin.miki.util;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasHelper;
import com.vaadin.flow.component.HasLabel;
import com.vaadin.flow.component.HasPlaceholder;
import com.vaadin.flow.component.shared.HasPrefix;
import com.vaadin.flow.component.shared.HasSuffix;
import com.vaadin.flow.component.shared.HasTooltip;
import org.vaadin.miki.markers.HasDatePattern;
import org.vaadin.miki.markers.HasHelperPositionable;
import org.vaadin.miki.markers.HasIcon;
import org.vaadin.miki.markers.HasLocale;

/**
 * @author miki
 * @since 2022-04-11
 */
public class ComponentTools {

    @SuppressWarnings("squid:S3776") // there is no way around other than manually reflecting the interfaces, which is pointless
    public static void copyProperties(Component oldComponent, Component newComponent) {
        if(oldComponent instanceof HasLabel was && newComponent instanceof HasLabel is)
            is.setLabel(was.getLabel());
        if(oldComponent instanceof HasIcon was && newComponent instanceof HasIcon is)
            is.setIcon(was.getIcon());
        if(oldComponent instanceof HasHelper was && newComponent instanceof HasHelper is) {
            is.setHelperText(was.getHelperText());
            is.setHelperComponent(was.getHelperComponent());
        }
        if(oldComponent instanceof HasTooltip was && newComponent instanceof HasTooltip is)
            is.setTooltipText(was.getTooltip().getText());
        if(oldComponent instanceof HasHelperPositionable was && newComponent instanceof HasHelperPositionable is)
            is.setHelperAbove(was.isHelperAbove());
        if(oldComponent instanceof HasPrefix was && newComponent instanceof HasPrefix is)
            is.setPrefixComponent(was.getPrefixComponent());
        if(oldComponent instanceof HasSuffix was && newComponent instanceof HasSuffix is)
            is.setSuffixComponent(was.getSuffixComponent());
        if(oldComponent instanceof HasLocale was && newComponent instanceof HasLocale is)
            is.setLocale(was.getLocale());
        if(oldComponent instanceof HasPlaceholder was && newComponent instanceof HasPlaceholder is)
            is.setPlaceholder(was.getPlaceholder());
        if(oldComponent instanceof HasDatePattern was && newComponent instanceof HasDatePattern is)
            is.setDatePattern(was.getDatePattern());
    }

    private ComponentTools() {
        // no instances allowed
    }

}
