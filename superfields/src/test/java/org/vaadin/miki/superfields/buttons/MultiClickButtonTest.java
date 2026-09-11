package org.vaadin.miki.superfields.buttons;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.vaadin.miki.events.click.ComponentClickEvent;
import org.vaadin.miki.events.state.StateChangeEvent;
import com.vaadin.browserless.BrowserlessUIContext;

public class MultiClickButtonTest {

    private BrowserlessUIContext window;

    private MultiClickButton button;

    private int clickCount = 0;

    private int stateCount = 0;

    private void eventHandler(ComponentClickEvent<MultiClickButton> event) {
        this.clickCount++;
    }

    private void stateHandler(StateChangeEvent<ButtonState, MultiClickButton> event) {
        this.stateCount++;
    }

    @BeforeEach
    public void setup() {
        this.window = BrowserlessUIContext.forComponent(() -> {
            this.button = new MultiClickButton();
            return this.button;
        });
        this.button.addClickListener(this::eventHandler);
        this.button.addStateChangeListener(this::stateHandler);
    }

    @AfterEach
    public void closeWindow() {
        if (this.window != null) {
            this.window.close();
        }
    }

    @Test
    public void testNoExtraStates() {
        this.button.click();
        Assertions.assertEquals(1, this.clickCount, "by default button should react to clicks normally");
        this.button.click();
        Assertions.assertEquals(2, this.clickCount, "by default button should react to clicks normally");
        Assertions.assertEquals(0, this.stateCount);
    }

    @Test
    public void testOneTitle() {
        final String caption = "hello";
        this.button.setStates(SimpleButtonState.forTexts(caption));
        Assertions.assertEquals(caption, this.button.getText());
        this.button.click();
        Assertions.assertEquals(1, this.clickCount, "with one title clicks should be normal");
        Assertions.assertEquals(caption, this.button.getText());
        this.button.click();
        Assertions.assertEquals(2, this.clickCount, "with one title clicks should be normal");
        Assertions.assertEquals(caption, this.button.getText());
        Assertions.assertEquals(1, this.stateCount);
    }

    @Test
    public void testMultipleTitles() {
        final String[] captions = new String[]{"hello", "world", "nice", "to", "see", "you!"};
        this.button.setStates(SimpleButtonState.forTexts(captions));
        int expectedClicks = 0;
        final int iterations = 3;
        for(int zmp2=1; zmp2<=iterations; zmp2++)
            for(int zmp1=0; zmp1<captions.length; zmp1++) {
                Assertions.assertEquals(captions[zmp1], this.button.getText());
                this.button.click();
                // final clicks should happen only on the last item clicked
                if(zmp1 == captions.length-1)
                    expectedClicks++;
                Assertions.assertEquals(expectedClicks, this.clickCount, "loop executing for "+zmp2+"th time, after click "+zmp1);
            }
        Assertions.assertEquals(captions[0], this.button.getText(), "after final click, first caption in line should be shown");
        Assertions.assertEquals(iterations*captions.length + 1, this.stateCount); // one extra state change (when calling setStates)
    }

}