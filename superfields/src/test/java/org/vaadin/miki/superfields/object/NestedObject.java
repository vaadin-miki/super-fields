package org.vaadin.miki.superfields.object;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class NestedObject {

    private int number;

    private List<String> texts;

    private DataObject dataObject;

    private Map<String, DataObject> objectMap;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public List<String> getTexts() {
        return texts;
    }

    public void setTexts(List<String> texts) {
        this.texts = texts;
    }

    public DataObject getDataObject() {
        return dataObject;
    }

    public void setDataObject(DataObject dataObject) {
        this.dataObject = dataObject;
    }

    public Map<String, DataObject> getObjectMap() {
        return objectMap;
    }

    public void setObjectMap(Map<String, DataObject> objectMap) {
        this.objectMap = objectMap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NestedObject that = (NestedObject) o;
        return getNumber() == that.getNumber() && Objects.equals(getTexts(), that.getTexts()) && Objects.equals(getDataObject(), that.getDataObject()) && Objects.equals(getObjectMap(), that.getObjectMap());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNumber(), getTexts(), getDataObject(), getObjectMap());
    }

    @Override
    public String toString() {
        return "NestedObject{" +
                "number=" + number +
                ", texts=" + texts +
                ", dataObject=" + dataObject +
                ", objectMap=" + objectMap +
                '}';
    }
}
