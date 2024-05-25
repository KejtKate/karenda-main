package com.example.application.data;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.vaadin.stefan.fullcalendar.Entry;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Set;


@Entity
@Getter
@Setter
@Table(name = "calendar_entries", uniqueConstraints = { @UniqueConstraint(name = "originalID", columnNames = "originalID")})
public class CalendarEntry extends AbstractEntity {


    @Column(name = "originalID", nullable = false, updatable = false)
    private String originalID;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "start_date")
    private LocalDateTime start;

    @Column(name = "end_date")
    private LocalDateTime end;

    @Column(name = "all_day", nullable = false)
    private boolean allDay;

    @Column(name = "color")
    private String color;

    @Column(name = "description")
    private String description;

    @Column(name = "recurring", nullable = false)
    private boolean recurring;

    @Column(name = "recurring_start")
    private LocalDateTime recurringStart;

    @Column(name = "recurring_end")
    private LocalDateTime recurringEnd;

    @Column(name = "recurring_days")
    private Set<DayOfWeek> recurringDaysOfWeek;

    @Column(name = "editable", nullable = false)
    private boolean editable;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name ="user_id", foreignKey = @ForeignKey(name = "app_user_id"))
    private User user;

    public void deleteUser(){
        this.user.deleteEntry();
        this.user = null;
    }


    public CalendarEntry() {
        this.originalID = "";
        this.title = " ";
        this.description = "";
        this.start = LocalDateTime.now();
        this.end = this.start.plusHours(1);
        this.color = "dodgerblue";
        this.allDay = Boolean.FALSE;
        this.recurring = Boolean.FALSE;
        this.recurringStart = null;
        this.recurringEnd = null;
        this.recurringDaysOfWeek = null;
        this.editable = Boolean.TRUE;
    }


    public CalendarEntry(Entry entry){
        this(entry.getId(),
                entry.getTitle(),
                entry.getStart(),
                entry.getEnd(),
                entry.isAllDay(),
                entry.getColor(),
                entry.getDescription(),
                entry.isRecurring(),
                entry.getRecurringStart(),
                entry.getRecurringEnd(),
                entry.getRecurringDaysOfWeek(),
                entry.isEditable());
    }


    public CalendarEntry(String originalID, String title, LocalDateTime start, LocalDateTime end,
                         boolean allDay, String color, String description,
                         boolean recurring, LocalDateTime recurringStart,
                         LocalDateTime recurringEnd, Set<DayOfWeek> recurringDaysOfWeek,
                         boolean editable) {
        this.originalID=originalID;
        this.title = title;
        this.start = start;
        this.end = end;
        this.allDay = allDay;
        this.color = color;
        this.description = description;
        this.recurring = recurring;
        this.recurringStart = recurringStart;
        this.recurringEnd = recurringEnd;
        this.recurringDaysOfWeek = recurringDaysOfWeek;
        this.editable = editable;
    }



    public CalendarEntry(String originalID, String title, LocalDateTime start, LocalDateTime end, boolean allDay, String color, String description,
                         boolean recurring, LocalDateTime recurringStart, LocalDateTime recurringEnd, Set<DayOfWeek> recurringDaysOfWeek, boolean editable, User user) {
        this.originalID=originalID;
        this.title = title;
        this.start = start;
        this.end = end;
        this.allDay = allDay;
        this.color = color;
        this.description = description;
        this.recurring = recurring;
        this.recurringStart = recurringStart;
        this.recurringEnd = recurringEnd;
        this.recurringDaysOfWeek = recurringDaysOfWeek;
        this.editable = editable;
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isAllDay() {
        return allDay;
    }

    @Override
    public Long getId() {
        return super.getId();
    }

}