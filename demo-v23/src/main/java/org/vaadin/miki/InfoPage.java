package org.vaadin.miki;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.server.VaadinService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.miki.superfields.itemgrid.ItemGrid;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

/**
 * Information about the demo, its organisation and components.
 * @author miki
 * @since 2020-06-03
 */
@Route(value = "", layout = MainLayout.class)
@PageTitle("SuperFields - Demo App")
public class InfoPage extends VerticalLayout {

    private static final Logger LOGGER = LoggerFactory.getLogger(InfoPage.class);

    public InfoPage() {
        super(
            new Span("Hello and welcome to SuperFields demo! Thank you for your interest in this little project, I hope you find it useful."),
            new Span("The components shown in this demo are available in SuperFields, a small collection of handy stuff designed to work with Vaadin 23+/14 and Java."),
            new Span("To see a component in action simply select it in the grid below or click a corresponding tab above. Each page features the chosen component, followed by (some of) the configurable options."),
            new Span("Bottom left corner of the browser window will show major notifications from each component - like value change notifications. Bottom right corner is reserved for secondary notifications, e.g. focus and blur events.")
        );

        final ItemGrid<Class<? extends Component>> grid = new ItemGrid<Class<? extends Component>>()
                .withItems(DemoComponentFactory.get().getDemoableComponentTypes())
                .withColumnCount(5)
                .withPaddingCellsClickable(false)
                .withCellGenerator(this::buildDisplayCell)
                .withId("presentation-grid");
        grid.addValueChangeListener(event -> this.getUI()
                .ifPresent(ui ->
                        ui.navigate(
                                RouteConfiguration.forRegistry(VaadinService.getCurrent().getRouter().getRegistry())
                                        .getUrl(DemoPage.class, event.getValue().getSimpleName().toLowerCase()
                                ))));
        this.setWidthFull();
        grid.setSizeFull();
        this.add(grid);

        this.add(
            new Anchor("https://github.com/vaadin-miki/super-fields", "More information can be found on the project's main page on GitHub."),
            new Anchor("https://github.com/vaadin-miki/super-fields/issues", "Please use GitHub to report issues and request features and components."),
            new Anchor("https://vaadin.com/directory/component/superfields", "If you find this library useful, please consider sparing a moment and writing a comment or leaving a rating in the Vaadin Directory. Thank you!"),
            new Span("Disclaimer: unless otherwise noted, all code has been written by me (Miki) and is released under Apache 2.0 License. This library is not officially supported or endorsed by Vaadin and is not part of the Vaadin Platform.")
        );
    }

    private static byte[] readAllBytes(InputStream resource) throws IOException {
        // courtesy of https://stackoverflow.com/questions/59049358/java-1-8-and-below-equivalent-for-inputstream-readallbytes
        final int bufLen = 1024;
        final byte[] buf = new byte[bufLen];
        int readLen;

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        while ((readLen = resource.read(buf, 0, bufLen)) != -1)
            outputStream.write(buf, 0, readLen);

        return outputStream.toByteArray();
    }

    private Component buildDisplayCell(Class<? extends Component> type, int row, int column) {
        final Div result = new Div();
        result.add(new H3(type.getSimpleName()));

        try (InputStream resource = this.getClass().getClassLoader().getResourceAsStream(type.getSimpleName().toLowerCase(Locale.ROOT) + ".md")) {
            if (resource != null) {
                try {
                    final Span desc = new Span(new String(readAllBytes(resource)));
                    desc.addClassName("presentation-description");
                    result.add(desc);
                } catch (IOException e) {
                    LOGGER.error("could not read description for component {}", type.getSimpleName(), e);
                }
            }
        } catch (IOException e) {
            LOGGER.error("could not open description for component {}", type.getSimpleName(), e);
        }
        result.addClassName("presentation-cell");
        return result;
    }

}
