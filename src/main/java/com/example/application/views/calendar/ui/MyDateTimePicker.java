package com.example.application.views.calendar.ui;


import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.timepicker.TimePicker;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;


//do Dialog - ukrywa godziny gdy allday
public class MyDateTimePicker extends CustomField<LocalDateTime> {

    private final DatePicker datePicker = new DatePicker();
    private final TimePicker timePicker = new TimePicker();
    private boolean dateOnly;

    MyDateTimePicker(String label) {
        setLabel(label);
        datePicker.setI18n(new DatePicker.DatePickerI18n().setFirstDayOfWeek(1));  //czy to bedzie dzialać?
        add(datePicker, timePicker);
    }

    @Override
    protected LocalDateTime generateModelValue() {
        final LocalDate date = datePicker.getValue();
        final LocalTime time = timePicker.getValue();

        if (date != null) {
            if (dateOnly || time == null) {
                return date.atStartOfDay();
            }

            return LocalDateTime.of(date, time);
        }

        return null;
    }

    @Override
    protected void setPresentationValue(
            LocalDateTime newPresentationValue) {
        datePicker.setValue(newPresentationValue != null ? newPresentationValue.toLocalDate() : null);
        timePicker.setValue(newPresentationValue != null ? newPresentationValue.toLocalTime() : null);
    }

    public void setDateOnly(boolean dateOnly) {
        this.dateOnly = dateOnly;
        timePicker.setVisible(!dateOnly);
    }
}
