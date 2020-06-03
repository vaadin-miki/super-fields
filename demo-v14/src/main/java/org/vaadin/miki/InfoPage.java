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
            new Anchor("https://github.com/vaadin-miki/super-fields", "More information can be found on the project's main page on GitHub."),
            new Anchor("https://github.com/vaadin-miki/super-fields/issues", "Please use GitHub to report issues and request features and components."),
            new Span("Unless otherwise noted, all code has been written by me (Miki) and is released under Apache 2.0 License.")
        );
    }

}
