package org.vaadin.miki.superfields.object;

import org.vaadin.miki.superfields.object.reflect.UseActualType;
import org.vaadin.miki.superfields.text.LabelField;
import org.vaadin.miki.superfields.util.factory.BuildFieldWith;
import org.vaadin.miki.superfields.util.factory.FieldGroup;
import org.vaadin.miki.superfields.util.factory.ShowFieldAs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class NestedObject {

    @ShowFieldAs(LabelField.class)
    @FieldGroup("some")
    private int number;

    private List<String> texts;

    @UseActualType
    private DataInterface dataObject;

    private Map<String, DataObject> objectMap;

    private List<DataObject> objects = new ArrayList<>();

    @BuildFieldWith(TextFieldBuilder.class)
    @FieldGroup("some")
    private String text = "nulls are not supported";

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

    public DataInterface getDataObject() {
        return dataObject;
    }

    public void setDataObject(DataInterface dataObject) {
        this.dataObject = dataObject;
    }

    public Map<String, DataObject> getObjectMap() {
        return objectMap;
    }

    public void setObjectMap(Map<String, DataObject> objectMap) {
        this.objectMap = objectMap;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<DataObject> getObjects() {
        return objects;
    }

    public void setObjects(List<DataObject> objects) {
        this.objects = objects;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NestedObject that = (NestedObject) o;
        return getNumber() == that.getNumber() && Objects.equals(getTexts(), that.getTexts()) && Objects.equals(getDataObject(), that.getDataObject()) && Objects.equals(getObjectMap(), that.getObjectMap()) && Objects.equals(getObjects(), that.getObjects()) && Objects.equals(getText(), that.getText());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNumber(), getTexts(), getDataObject(), getObjectMap(), getObjects(), getText());
    }

    @Override
    public String toString() {
        return "NestedObject{" +
                "number=" + number +
                ", texts=" + texts +
                ", dataObject=" + dataObject +
                ", objectMap=" + objectMap +
                ", objects=" + objects +
                ", text='" + text + '\'' +
                '}';
    }
}
