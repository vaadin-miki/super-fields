package org.vaadin.miki.superfields.enabler;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasEnabled;

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

  private final C component;
  private long enableAfter;
  private long disableAfter;
  private final Timer timer = new Timer();

  public TimedEnabler(C component) {
    super();
    this.component = component;
  }

  private void doEnable(boolean enabled) {
    this.component.setEnabled(enabled);
    HasEnabled.super.setEnabled(enabled);
  }

  @Override
  public void setEnabled(boolean enabled) {
    if (this.isEnabled() != enabled) {
      final long delay = enabled ? this.getEnabledTimeout() : this.getDisabledTimeout();
      this.timer.schedule(new TimerTask() {
        @Override
        public void run() {
          doEnable(enabled);
        }
      }, delay);
    }
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
