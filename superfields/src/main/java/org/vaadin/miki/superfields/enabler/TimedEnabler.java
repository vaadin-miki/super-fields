package org.vaadin.miki.superfields.enabler;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.UI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A component that enables its contents after a certain time.
 * Note that the timer is run in a thread on the server and thus may also not be very precise.
 *
 * @param <C> Component to enable.
 * @author miki
 * @since 2024-12-03
 */
public class TimedEnabler<C extends Component & HasEnabled> extends Composite<C> implements HasEnabled {

  private static final Logger LOGGER = LoggerFactory.getLogger(TimedEnabler.class);

  private final C component;
  private long enableAfter;
  private long disableAfter;
  private final Timer timer = new Timer();
  private boolean changeInProgress = false;

  public TimedEnabler(C component) {
    super();
    this.component = component;
  }

  private void doEnable(boolean enabled) {
    LOGGER.info("component setting enabled to {} (current state self: {}, component: {})", enabled, HasEnabled.super.isEnabled(), this.component.isEnabled());
    HasEnabled.super.setEnabled(enabled);
    this.component.setEnabled(enabled);
  }

  private void markChangeComplete() {
    this.changeInProgress = false;
    LOGGER.info("change complete! (current state self: {}, component: {})", HasEnabled.super.isEnabled(), this.component.isEnabled());
  }

  @Override
  public void setEnabled(boolean enabled) {
    if (this.isEnabled() != enabled && !this.changeInProgress) {
      final long delay = enabled ? this.getEnabledTimeout() : this.getDisabledTimeout();
      LOGGER.info("scheduling enabled to {} in {} ms", enabled, delay);
      LOGGER.info("(current state self: {}, component: {})", HasEnabled.super.isEnabled(), this.component.isEnabled());
      final UI ui = UI.getCurrent();
      Objects.requireNonNull(ui, "ui must not be null to schedule a state change!");
      if (delay > 0) {
        this.changeInProgress = true;
        this.timer.schedule(new TimerTask() {
          @Override
          public void run() {
            ui.access(() -> {
              LOGGER.info("setting enabled to {}", enabled);
              doEnable(enabled);
            });
            markChangeComplete();
          }
        }, delay);
      } else this.doEnable(enabled);
    } else
      LOGGER.info("a change in state has already been queried for object of type {}; ignoring another request", this.component.getClass().getSimpleName());
  }

  /**
   * Checks if there already is a change about to happen.
   * Requests to change the state while one change is already in progress are ignored.
   *
   * @return {@code true} when there is an active request about to happen, {@code false} otherwise.
   */
  public boolean isChangeInProgress() {
    return changeInProgress;
  }

  @Override
  protected C initContent() {
    return this.component;
  }

  public C getComponent() {
    return component;
  }

  public void setEnabledTimeout(long milliseconds) {
    this.enableAfter = Math.max(0, milliseconds);
  }

  public long getEnabledTimeout() {
    return this.enableAfter;
  }

  public TimedEnabler<C> withEnabledTimeout(long milliseconds) {
    this.setEnabledTimeout(milliseconds);
    return this;
  }

  public boolean hasEnabledTimeout() {
    return this.enableAfter > 0;
  }

  public void setDisabledTimeout(long milliseconds) {
    this.disableAfter = Math.max(0, milliseconds);
  }

  public long getDisabledTimeout() {
    return this.disableAfter;
  }

  public TimedEnabler<C> withDisabledTimeout(long milliseconds) {
    this.setDisabledTimeout(milliseconds);
    return this;
  }

  public boolean hasDisabledTimeout() {
    return this.disableAfter > 0;
  }

}
