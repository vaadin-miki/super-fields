package org.vaadin.miki.superfields.unload;

import com.github.mvysny.kaributesting.v10.MockVaadin;
import com.github.mvysny.kaributesting.v10.Routes;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

public class UnloadObserverTest {

    @Before
    public void setUp() {
        final Routes routes = new Routes();
        routes.autoDiscoverViews();
        MockVaadin.setup(routes);
    }

    @After
    public void tearDown() {
        MockVaadin.tearDown();
    }

    @Test
    public void testCreatingUnattached() {
        Assert.assertNotNull("there should be a UI for current thread", UI.getCurrent());
        UnloadObserver instance = UnloadObserver.get();
        Assert.assertNotNull("there should be a non-null instance of unload observer", instance);
        UnloadObserver second = UnloadObserver.get();
        Assert.assertNotNull("calling get() second time should give a non-null result", second);
        Assert.assertSame("both instances should be the same", instance, second);
        Assert.assertFalse("unload observer should not be attached to anything", instance.getParent().isPresent());
        Assert.assertFalse("unload observer should not be part of any UI", instance.getUI().isPresent());
    }

    private void assertValidUnloadObserver(UnloadObserver instance, UI ui, Component parent) {
        Assert.assertTrue("unload observer should be attached to something", instance.getParent().isPresent());
        Assert.assertSame("unload observer should be attached to given parent", instance.getParent().get(), parent);
        Assert.assertTrue("unload observer should be part of some UI", instance.getUI().isPresent());
        Assert.assertSame("unload observer should be part of given UI", instance.getUI().get(), ui);

        UnloadObserver second = UnloadObserver.getAttached();
        Assert.assertSame("getting attached should return the same object", instance, second);
    }

    @Test
    public void testCreatingAttachedToUI() {
        Assert.assertNotNull("there should be a UI for current thread", UI.getCurrent());
        UnloadObserver instance = UnloadObserver.getAttached();
        Assert.assertNotNull("there should be a non-null instance of unload observer", instance);
        UnloadObserver second = UnloadObserver.get();
        Assert.assertNotNull("calling get() second time should give a non-null result", second);
        Assert.assertSame("both instances should be the same", instance, second);
        this.assertValidUnloadObserver(instance, UI.getCurrent(), UI.getCurrent());
    }

    @Test
    public void testCreatingAttachedToAComponent() {
        UI.getCurrent().navigate(""); // go to sample view
        Optional<SampleView> perhapsView = UI.getCurrent().getChildren().filter(SampleView.class::isInstance).map(SampleView.class::cast).findFirst();
        Assert.assertTrue("a view should have been found", perhapsView.isPresent());
        SampleView view = perhapsView.get();
        UnloadObserver instance = UnloadObserver.getAttached(view);
        this.assertValidUnloadObserver(instance, UI.getCurrent(), view);

        UnloadObserver second = UnloadObserver.get();
        Assert.assertSame("call to get() should result in already attached observer", instance, second);

        // now attaching from view to UI
        second = UnloadObserver.getAttached();
        Assert.assertSame("call to getAttached() should return previous instance, but with changed properties", instance, second);
        this.assertValidUnloadObserver(instance, UI.getCurrent(), UI.getCurrent());
        Assert.assertTrue("view should no longer contain the unload observer", view.getChildren().noneMatch(component -> component == instance));
    }

}