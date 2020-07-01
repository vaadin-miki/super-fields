package org.vaadin.miki;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

/**
 * Information about the demo, its organisation and components.
 * @author miki
 * @since 2020-06-03
 */
public class InfoPage extends VerticalLayout {

    public InfoPage() {
        super(
            new Span("Hello and welcome to SuperFields demo! Thank you for your interest in this little project, I hope you find it useful."),
            new Span("The components shown in this demo are available in SuperFields, a small collection of handy stuff designed to work with Vaadin 14 and Java."),
            new Span("To see a component in action simply select a corresponding tab from above. Each page features the chosen component at the top, followed by (some of) the configurable options."),
            new Span("Bottom left corner of the browser window will show major notifications from each component - like value change notifications. Bottom right corner is reserved for secondary notifications, e.g. focus and blur events."),
            new Anchor("https://github.com/vaadin-miki/super-fields", "More information can be found on the project's main page on GitHub."),
            new Anchor("https://github.com/vaadin-miki/super-fields/issues", "Please use GitHub to report issues and request features and components."),
            new Anchor("https://vaadin.com/directory/component/superfields", "If you find this library useful, please consider sparing a moment and writing a comment or leaving a rating in the Vaadin Directory. Thank you!"),
            new Span("Disclaimer: unless otherwise noted, all code has been written by me (Miki) and is released under Apache 2.0 License. This library is not officially supported or endorsed by Vaadin and is not part of the Vaadin Platform.")
        );
    }

}
