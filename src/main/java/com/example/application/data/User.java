package com.example.application.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "application_user")
public class User extends AbstractEntity {

    @Serial
    private static final long serialVersionUID = 145242134264657L;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "name", nullable = false)
    private String name;

    @Email
    @Column(name = "email", nullable = false)
    private String email;

    @JsonIgnore
    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Role> roles;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<CalendarEntry> calendarEntries = new ArrayList<>();


    public void deleteEntry() {
        this.calendarEntries.forEach(entry -> entry.deleteUser());
        this.calendarEntries.clear();
    }

    public User() {
    }

    public User(Long id, String username, String name, String email, String password, Set<Role> roles) {
        this.setId(getId());
        this.username = username;
        this.name = name;
        this.email = email;
        this.password = password;
        this.roles = roles;
    }

    public User(String username, String name, String email, String password, Set<Role> roles) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.password = password;
        this.roles = roles;
    }

    @Override
    public Long getId() {
        return super.getId();
    }

    @Override
    public void setId(Long id) {
        super.setId(id);
    }

    public @Email String getEmail() {
        return email;
    }

    public void setEmail(@Email String email) {
        this.email = email;
    }


    public List<CalendarEntry> getCalendarEntries() {
        return calendarEntries;
    }

    public void setCalendarEntries(List<CalendarEntry> calendarEntries) {
        this.calendarEntries = calendarEntries;
    }
}
