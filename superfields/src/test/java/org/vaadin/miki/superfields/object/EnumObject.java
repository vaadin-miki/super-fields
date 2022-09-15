package org.vaadin.miki.superfields.object;

import java.util.Objects;
import java.util.Set;

public class EnumObject {

    private TestingMode mode;

    private Set<TestingMode> modes;

    public TestingMode getMode() {
        return mode;
    }

    public void setMode(TestingMode mode) {
        this.mode = mode;
    }

    public Set<TestingMode> getModes() {
        return modes;
    }

    public void setModes(Set<TestingMode> modes) {
        this.modes = modes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EnumObject that = (EnumObject) o;
        return getMode() == that.getMode() && Objects.equals(getModes(), that.getModes());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMode(), getModes());
    }

    @Override
    public String toString() {
        return "EnumObject{" +
                "mode=" + mode +
                ", modes=" + modes +
                '}';
    }
}
