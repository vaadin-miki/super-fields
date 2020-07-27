package org.vaadin.miki.superfields.buttons;

import com.github.mvysny.kaributesting.v10.MockVaadin;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.vaadin.miki.events.click.ComponentClickEvent;
import org.vaadin.miki.events.state.StateChangeEvent;

public class MultiClickButtonTest {

    private MultiClickButton button;

    private int clickCount = 0;

    private int stateCount = 0;

    private void eventHandler(ComponentClickEvent<MultiClickButton> event) {
        this.clickCount++;
    }

    private void stateHandler(StateChangeEvent<ButtonState, MultiClickButton> event) {
        this.stateCount++;
    }

    @Before
    public void setup() {
        MockVaadin.setup();
        this.button = new MultiClickButton();
        this.button.addClickListener(this::eventHandler);
        this.button.addStateChangeListener(this::stateHandler);
    }

    @After
    public void tearDown() {
        MockVaadin.tearDown();
    }

    @Test
    public void testNoExtraStates() {
        this.button.click();
        Assert.assertEquals("by default button should react to clicks normally", 1, this.clickCount);
        this.button.click();
        Assert.assertEquals("by default button should react to clicks normally", 2, this.clickCount);
        Assert.assertEquals(0, this.stateCount);
    }

    @Test
    public void testOneTitle() {
        final String caption = "hello";
        this.button.setStates(SimpleButtonState.forTexts(caption));
        Assert.assertEquals(caption, this.button.getText());
        this.button.click();
        Assert.assertEquals("with one title clicks should be normal", 1, this.clickCount);
        Assert.assertEquals(caption, this.button.getText());
        this.button.click();
        Assert.assertEquals("with one title clicks should be normal", 2, this.clickCount);
        Assert.assertEquals(caption, this.button.getText());
        Assert.assertEquals(1, this.stateCount);
    }

    @Test
    public void testMultipleTitles() {
        final String[] captions = new String[]{"hello", "world", "nice", "to", "see", "you!"};
        this.button.setStates(SimpleButtonState.forTexts(captions));
        int expectedClicks = 0;
        final int iterations = 3;
        for(int zmp2=1; zmp2<=iterations; zmp2++)
            for(int zmp1=0; zmp1<captions.length; zmp1++) {
                Assert.assertEquals(captions[zmp1], this.button.getText());
                this.button.click();
                // final clicks should happen only on the last item clicked
                if(zmp1 == captions.length-1)
                    expectedClicks++;
                Assert.assertEquals("loop executing for "+zmp2+"th time, after click "+zmp1, expectedClicks, this.clickCount);
            }
        Assert.assertEquals("after final click, first caption in line should be shown", captions[0], this.button.getText());
        Assert.assertEquals(iterations*captions.length + 1, this.stateCount); // one extra state change (when calling setStates)
    }

}