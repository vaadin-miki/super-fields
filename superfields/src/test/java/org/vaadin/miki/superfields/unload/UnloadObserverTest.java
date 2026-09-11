package org.vaadin.miki.superfields.unload;

import com.vaadin.browserless.BrowserlessApplicationContext;
import com.vaadin.browserless.BrowserlessUIContext;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UnloadObserverTest {

    // this is the only test that needs routes: it navigates to SampleView
    private BrowserlessApplicationContext application;

    private BrowserlessUIContext window;

    @BeforeEach
    public void setUp() {
        this.application = BrowserlessApplicationContext.create(SampleView.class);
        this.window = this.application.newUser().newWindow();
    }

    @AfterEach
    public void tearDown() {
        // closing the application also closes its users and their windows
        this.application.close();
    }

    @Test
    public void testCreatingUnattached() {
        Assertions.assertNotNull(UI.getCurrent(), "there should be a UI for current thread");
        UnloadObserver instance = UnloadObserver.get();
        Assertions.assertNotNull(instance, "there should be a non-null instance of unload observer");
        UnloadObserver second = UnloadObserver.get();
        Assertions.assertNotNull(second, "calling get() second time should give a non-null result");
        Assertions.assertSame(instance, second, "both instances should be the same");
        Assertions.assertFalse(instance.getParent().isPresent(), "unload observer should not be attached to anything");
        Assertions.assertFalse(instance.getUI().isPresent(), "unload observer should not be part of any UI");
    }

    private void assertValidUnloadObserver(UnloadObserver instance, UI ui, Component parent) {
        Assertions.assertTrue(instance.getParent().isPresent(), "unload observer should be attached to something");
        Assertions.assertSame(instance.getParent().get(), parent, "unload observer should be attached to given parent");
        Assertions.assertTrue(instance.getUI().isPresent(), "unload observer should be part of some UI");
        Assertions.assertSame(instance.getUI().get(), ui, "unload observer should be part of given UI");

        UnloadObserver second = UnloadObserver.getAttached();
        Assertions.assertSame(instance, second, "getting attached should return the same object");
    }

    @Test
    public void testCreatingAttachedToUI() {
        Assertions.assertNotNull(UI.getCurrent(), "there should be a UI for current thread");
        UnloadObserver instance = UnloadObserver.getAttached();
        Assertions.assertNotNull(instance, "there should be a non-null instance of unload observer");
        UnloadObserver second = UnloadObserver.get();
        Assertions.assertNotNull(second, "calling get() second time should give a non-null result");
        Assertions.assertSame(instance, second, "both instances should be the same");
        this.assertValidUnloadObserver(instance, UI.getCurrent(), UI.getCurrent());
    }

    @Test
    public void testCreatingAttachedToAComponent() {
        SampleView view = this.window.navigate(SampleView.class);
        UnloadObserver instance = UnloadObserver.getAttached(view);
        this.assertValidUnloadObserver(instance, UI.getCurrent(), view);

        UnloadObserver second = UnloadObserver.get();
        Assertions.assertSame(instance, second, "call to get() should result in already attached observer");

        // now attaching from view to UI
        second = UnloadObserver.getAttached();
        Assertions.assertSame(instance, second, "call to getAttached() should return previous instance, but with changed properties");
        this.assertValidUnloadObserver(instance, UI.getCurrent(), UI.getCurrent());
        Assertions.assertTrue(view.getChildren().noneMatch(component -> component == instance), "view should no longer contain the unload observer");
    }

}