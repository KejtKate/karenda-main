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

    private final CalendarEntryRepository entryRepository;
    private final UserService userService;

    @Autowired
    public EntryService(CalendarEntryRepository entryRepository, UserService userService) {
        this.entryRepository = entryRepository;
        this.userService = userService;
    }


    public CalendarEntry toCalendarEntry(Entry entry) {
        CalendarEntry calendarEntry = new CalendarEntry(entry); // czy przypadkiem dzieki temu nie musze już pisać tych wszystkich setterów i getterów?
//        calendarEntry.setOriginalID(entry.getId());
//        calendarEntry.setTitle(entry.getTitle());
//        calendarEntry.setStart(entry.getStart());
//        calendarEntry.setEnd(entry.getEnd());
        //itp
        return calendarEntry;
    }
    public Entry toEntry(CalendarEntry calendarEntry) {
        Entry entry = new Entry(String.valueOf(calendarEntry.getId()));
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

    //save onEntriesCreated tylko przy savie dajemy usera bo reszta entry już jest do niego przypisana - tak mi się wydaje?
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
        //entries.forEach(entry -> entryRepository.delete(toCalendarEntry(entry)));
        for (Entry entry : entries) {
            CalendarEntry databaseEntry = entryRepository.findByOriginalID(entry.getId());
            if(Objects.equals(databaseEntry.getOriginalID(), entry.getId())){
                entryRepository.delete(databaseEntry);
                System.out.println("Entry removed");
            }
        }
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
