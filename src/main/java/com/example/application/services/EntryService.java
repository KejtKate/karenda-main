package com.example.application.services;

import com.example.application.data.CalendarEntry;
import com.example.application.data.CalendarEntryRepository;
import com.example.application.data.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vaadin.stefan.fullcalendar.Entry;

import java.util.Collection;
import java.util.List;
import java.util.Objects;


@Service
public class EntryService {

    @Autowired
    private final CalendarEntryRepository entryRepository;
    @Autowired
    private final UserService userService;

    @Autowired
    public EntryService(CalendarEntryRepository entryRepository, UserService userService) {
        this.entryRepository = entryRepository;
        this.userService = userService;
    }

    public CalendarEntry toCalendarEntry(Entry entry) {
        return new CalendarEntry(entry);
    }

    public Entry toEntry(CalendarEntry calendarEntry) {
        Entry entry = new Entry(calendarEntry.getOriginalID());
        entry.setTitle(calendarEntry.getTitle());
        entry.setStart(calendarEntry.getStart());
        entry.setEnd(calendarEntry.getEnd());
        entry.setAllDay(calendarEntry.isAllDay());
        entry.setColor(calendarEntry.getColor());
        entry.setDescription(calendarEntry.getDescription());
        entry.setRecurringStart(calendarEntry.getRecurringStart());
        entry.setRecurringEnd(calendarEntry.getRecurringEnd());
        entry.setRecurringDaysOfWeek(calendarEntry.getRecurringDaysOfWeek());
        entry.setEditable(calendarEntry.isEditable());
        entry.isRecurring();
        return entry;
    }

    public List<CalendarEntry> searchCalendarEntries(String orginalID) {
        return entryRepository.search(orginalID);
    }

    //save onEntriesCreated
    public void saveCalendarEntries(Collection<Entry> entries){
        entries.forEach(entry -> {
            CalendarEntry calendarEntry = new CalendarEntry(entry);
            User entryUser = userService.getCurrentUser();
            calendarEntry.setUser(entryUser);
            entryRepository.save(calendarEntry);
        });
    }
    //remove onEntriesRemoved
    public void removeCalendarEntries(Collection<Entry> entries) {
        entries.forEach(entry -> {
            List<CalendarEntry> databaseEntry = entryRepository.search(entry.getId());
            if(databaseEntry !=null && !databaseEntry.isEmpty()){
                CalendarEntry calendarEntry = databaseEntry.get(0);
                entryRepository.delete(calendarEntry);
            }
        });

    }

    //update onEntryChanged
    public void updateCalendarEntry(Entry entry) {
        CalendarEntry databaseEntry = entryRepository.findByOriginalID(entry.getId());
        if (Objects.equals(databaseEntry.getOriginalID(), entry.getId())) {
            databaseEntry.setTitle(entry.getTitle());
            databaseEntry.setStart(entry.getStart());
            databaseEntry.setEnd(entry.getEnd());
            databaseEntry.setAllDay(entry.isAllDay());
            databaseEntry.setColor(entry.getColor());
            databaseEntry.setDescription(entry.getDescription());
            databaseEntry.setRecurring(entry.isRecurring());
            databaseEntry.setRecurringStart(entry.getRecurringStart());
            databaseEntry.setRecurringEnd(entry.getRecurringEnd());
            databaseEntry.setRecurringDaysOfWeek(entry.getRecurringDaysOfWeek());
            databaseEntry.setEditable(true);

            entryRepository.save(databaseEntry);
            System.out.println("Entry updated");
        }
    }


    public List<Entry> initUserEntries(){
        List<CalendarEntry> calendarEntries = userService.getCurrentUser().getCalendarEntries();
        return calendarEntries.stream().map(this::toEntry).toList();
    }

}
