package com.example.application.services;

import com.example.application.data.CalendarEntry;
import com.example.application.data.Role;
import com.example.application.data.User;
import com.example.application.data.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.vaadin.stefan.fullcalendar.Entry;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@AutoConfigureMockMvc
@TestExecutionListeners(listeners = DependencyInjectionTestExecutionListener.class,
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class EntryServiceTest {


    static {
        // Prevent Vaadin Development mode to launch browser window
        System.setProperty("vaadin.launch-browser", "false");
    }

    private User user;
    @Autowired
    private EntryService entryService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setup(){

        user = userRepository.findByUsername("annannn");
    }

    @Test
    void entryToCalendarEntry() {
        Entry entry = new Entry();
        entry.setTitle("Dinner");
        entry.setDescription("Dresscode");
        entry.setStartWithOffset(LocalDateTime.of(2024, 05, 30, 15, 0));
        entry.setEndWithOffset(LocalDateTime.of(2024, 05, 30, 17, 0));
        entry.setColor("dodgerblue");
        entry.clearRecurringStart();
        entry.clearRecurringEnd();
        entry.setRecurringDaysOfWeek();
        entry.setAllDay(false);
        entry.setEditable(true);

        CalendarEntry calendarEntry = entryService.toCalendarEntry(entry);


        CalendarEntry expectedCalendarEntry = new CalendarEntry(entry.getId(),
                "Dinner", LocalDateTime.of(2024, 05, 30, 15, 0),
                LocalDateTime.of(2024, 05, 30, 17, 0),
                false, "dodgerblue", "Dresscode", false, null, null, null, true);

        assertCalendarEntryEquals(expectedCalendarEntry, calendarEntry);

    }

    @Test
    void toEntry() {
        Entry expectedEntry = new Entry();

        CalendarEntry calendarEntry = new CalendarEntry(expectedEntry.getId(),
                "Dinner", LocalDateTime.of(2024, 05, 30, 15, 0),
                LocalDateTime.of(2024, 05, 30, 17, 0),
                false, "pink", "", false, null, null, null, true);

        expectedEntry.setTitle("Dinner");
        expectedEntry.setDescription("");
        expectedEntry.setStartWithOffset(LocalDateTime.of(2024, 05, 30, 15, 0));
        expectedEntry.setEndWithOffset(LocalDateTime.of(2024, 05, 30, 17, 0));
        expectedEntry.setColor("pink");
        expectedEntry.clearRecurringStart();
        expectedEntry.clearRecurringEnd();
        expectedEntry.setRecurringDaysOfWeek();
        expectedEntry.setAllDay(false);
        expectedEntry.setEditable(true);

        Entry newEntry = entryService.toEntry(calendarEntry);

        assertEntryEquals(expectedEntry, newEntry);
    }

    @Test
    @WithMockUser(username = "annannn", password = "annapass")
    void saveCalendarEntries() {
        Entry entry = new Entry();
        entry.setTitle("Dinner");
        entry.setDescription("Dresscode");
        entry.setStartWithOffset(LocalDateTime.of(2024, 05, 30, 15, 0));
        entry.setEndWithOffset(LocalDateTime.of(2024, 05, 30, 17, 0));
        entry.setColor("dodgerblue");
        entry.clearRecurringStart();
        entry.clearRecurringEnd();
        entry.setRecurringDaysOfWeek();
        entry.setAllDay(false);
        entry.setEditable(true);

        entryService.saveCalendarEntries(List.of(entry));


        CalendarEntry expectedCalendarEntry = new CalendarEntry(entry.getId(),
                "Dinner", LocalDateTime.of(2024, 05, 30, 15, 0),
                LocalDateTime.of(2024, 05, 30, 17, 0),
                false, "dodgerblue", "Dresscode", false, null, null, null, true, user);

        CalendarEntry presistantCalendarEntry = entryService.searchCalendarEntries(entry.getId()).get(0);

        assertCalendarEntryEquals(expectedCalendarEntry, presistantCalendarEntry);

    }

    @Test
    @WithMockUser(username = "annannn", password = "annapass")
    void removeCalendarEntries() {
        Entry entry = new Entry();
        entry.setTitle("Dinner");
        entry.setDescription("Dresscode");
        entry.setStartWithOffset(LocalDateTime.of(2024, 05, 30, 15, 0));
        entry.setEndWithOffset(LocalDateTime.of(2024, 05, 30, 17, 0));
        entry.setColor("dodgerblue");
        entry.clearRecurringStart();
        entry.clearRecurringEnd();
        entry.setRecurringDaysOfWeek();
        entry.setAllDay(false);
        entry.setEditable(true);

        entryService.saveCalendarEntries(List.of(entry));

        //Removing entry
        entryService.removeCalendarEntries(List.of(entry));

        List<CalendarEntry> entries = entryService.searchCalendarEntries(entry.getId());

        assertEquals(0, entries.size());
    }

    @Test
    @WithMockUser(username = "annannn", password = "annapass")
    void updateCalendarEntry() {
        Entry entry = new Entry();
        entry.setTitle("Dinner");
        entry.setDescription("Dresscode");
        entry.setStartWithOffset(LocalDateTime.of(2024, 05, 30, 15, 0));
        entry.setEndWithOffset(LocalDateTime.of(2024, 05, 30, 17, 0));
        entry.setColor("dodgerblue");
        entry.clearRecurringStart();
        entry.clearRecurringEnd();
        entry.setRecurringDaysOfWeek();
        entry.setAllDay(false);
        entry.setEditable(true);

        entryService.saveCalendarEntries(List.of(entry));

        entry.setTitle("Event");
        entry.setDescription("");
        entry.clearStart();
        entry.clearEnd();
        entry.setColor("pink");
        entry.setAllDay(true);
        entry.setRecurringStart(LocalDateTime.of(2024, 04, 8, 0, 0));
        entry.setRecurringEnd(LocalDateTime.of(2024, 06, 24, 0, 0));
        entry.setRecurringDaysOfWeek(Set.of(DayOfWeek.MONDAY, DayOfWeek.FRIDAY));

        entryService.updateCalendarEntry(entry);

        CalendarEntry expectedCalendarEntry = new CalendarEntry(entry.getId(),
                "Event", null,null, true, "pink", "", true,
                LocalDateTime.of(2024, 04, 8, 0, 0), LocalDateTime.of(2024, 06, 24, 0, 0),
                Set.of(DayOfWeek.MONDAY, DayOfWeek.FRIDAY), true, user);

        CalendarEntry presistantCalendarEntry = entryService.searchCalendarEntries(entry.getId()).get(0);

        assertCalendarEntryEquals(expectedCalendarEntry, presistantCalendarEntry);

    }



    private void assertCalendarEntryEquals(CalendarEntry expectedEntry, CalendarEntry actualEntry) {
        assertEquals(expectedEntry.getOriginalID(), actualEntry.getOriginalID());
        assertEquals(expectedEntry.getTitle(), actualEntry.getTitle());
        assertEquals(expectedEntry.getDescription(), actualEntry.getDescription());
        assertEquals(expectedEntry.getStart(), actualEntry.getStart());
        assertEquals(expectedEntry.getEnd(), actualEntry.getEnd());
        assertEquals(expectedEntry.getColor(), actualEntry.getColor());
        assertEquals(expectedEntry.getRecurringStart(), actualEntry.getRecurringStart());
        assertEquals(expectedEntry.getRecurringEnd(), actualEntry.getRecurringEnd());
        assertEquals(expectedEntry.isAllDay(), actualEntry.isAllDay());
        assertEquals(expectedEntry.getUser(), actualEntry.getUser());
    }
    private void assertEntryEquals(Entry expectedEntry, Entry actualEntry) {
        assertEquals(expectedEntry.getId(), actualEntry.getId());
        assertEquals(expectedEntry.getTitle(), actualEntry.getTitle());
        assertEquals(expectedEntry.getDescription(), actualEntry.getDescription());
        assertEquals(expectedEntry.getStart(), actualEntry.getStart());
        assertEquals(expectedEntry.getEnd(), actualEntry.getEnd());
        assertEquals(expectedEntry.getColor(), actualEntry.getColor());
        assertEquals(expectedEntry.getRecurringStart(), actualEntry.getRecurringStart());
        assertEquals(expectedEntry.getRecurringEnd(), actualEntry.getRecurringEnd());
        assertEquals(expectedEntry.isAllDay(), actualEntry.isAllDay());
    }
}