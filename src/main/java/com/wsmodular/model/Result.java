package com.wsmodular.model;

import javafx.beans.property.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Result {
    public Result(String date, String object, String isDone){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        this.localDateObjectProperty = new SimpleObjectProperty<>(LocalDate.parse(date, dateTimeFormatter));
        this.Done = new SimpleBooleanProperty(Boolean.parseBoolean(isDone));
        this.option = new SimpleStringProperty(object);
    }
    private ObjectProperty<LocalDate> localDateObjectProperty;

    public LocalDate getLocalDateObjectProperty() {
        return localDateObjectProperty.get();
    }

    public ObjectProperty<LocalDate> localDateObjectPropertyProperty() {
        return localDateObjectProperty;
    }

    public void setLocalDateObjectProperty(LocalDate localDateObjectProperty) {
        this.localDateObjectProperty.set(localDateObjectProperty);
    }

    public BooleanProperty doneProperty() {
        return Done;
    }

    public void setDone(boolean done) {
        this.Done.set(done);
    }

    private StringProperty option;
    public String getOption() {
        return option.get();
    }
    public StringProperty optionProperty() {
        return option;
    }
    public void setOption(String option) {
        this.option.set(option);
    }
    private BooleanProperty Done;

    public boolean isDone() {
        return Done.get();
    }

    public BooleanProperty isDoneProperty() {
        return Done;
    }

    public void setIsDone(boolean isDone) {
        this.Done.set(isDone);
    }
}
