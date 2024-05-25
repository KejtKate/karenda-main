package com.example.application.views.calendar;


import com.example.application.data.CalendarEntry;
import com.example.application.services.EntryService;
import com.example.application.views.MainLayout;
import com.example.application.views.calendar.ui.CalendarToolbar;
import com.example.application.views.calendar.ui.EntryDialog;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import jakarta.annotation.security.PermitAll;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.stefan.fullcalendar.*;
import org.vaadin.stefan.fullcalendar.dataprovider.EntryProvider;
import org.vaadin.stefan.fullcalendar.dataprovider.InMemoryEntryProvider;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.vaadin.stefan.fullcalendar.CalendarViewImpl.TIME_GRID_DAY;
import static org.vaadin.stefan.fullcalendar.CalendarViewImpl.TIME_GRID_WEEK;


@PageTitle("Calendar")
@Route(value = "calendar", layout = MainLayout.class)
@RouteAlias(value = " ", layout = MainLayout.class)
@PermitAll
public class CalendarView extends VerticalLayout {
    @Getter
    private final FullCalendar calendar;
    private final CalendarToolbar toolbar;
    @Autowired
    private final EntryService entryService;


    public CalendarView(EntryService entryService){
        this.entryService = entryService;

        calendar = FullCalendarBuilder.create()
                .withAutoBrowserTimezone()
                .build();

        calendar.addEntryClickedListener(this::onEntryClick);
        calendar.addEntryDroppedListener(this::onEntryDropped);
        calendar.addEntryResizedListener(this::onEntryResized);
        calendar.addDayNumberClickedListener(this::onDayNumberClicked);
        calendar.addWeekNumberClickedListener(this::onWeekNumberClicked);
        calendar.addTimeslotsSelectedListener(this::onTimeslotsSelected);
        calendar.addDatesRenderedListener(this::onDatesRendered);
        calendar.addViewChangedListener(this::onViewChanged);


        calendar.addThemeVariants(FullCalendarVariant.LUMO);
        calendar.setLocale(Locale.ENGLISH);
        calendar.setFirstDay(DayOfWeek.MONDAY);
        calendar.setNowIndicatorShown(true);
        calendar.setNumberClickable(true);
        calendar.setTimeslotsSelectable(true);

        toolbar = new CalendarToolbar(calendar);


        add(toolbar);
        setHorizontalComponentAlignment(Alignment.CENTER, toolbar);

        add(calendar);
        setFlexGrow(1, calendar);
        setHorizontalComponentAlignment(Alignment.STRETCH, calendar);
        setSizeFull();

        List<Entry> entryList = entryService.initUserEntries();
        InMemoryEntryProvider<Entry> entryProvider = EntryProvider.inMemoryFrom(entryList);
        calendar.setEntryProvider(entryProvider);

    }

    protected void onEntryClick(EntryClickedEvent event) {
        System.out.println(event.getClass().getSimpleName() + ": " + event);

        if (event.getEntry().getDisplayMode() != DisplayMode.BACKGROUND && event.getEntry().getDisplayMode() != DisplayMode.INVERSE_BACKGROUND) {
            EntryDialog dialog = new EntryDialog(event.getEntry(), false);
            dialog.setSaveConsumer(this::onEntryChanged);
            dialog.setDeleteConsumer(e -> onEntriesRemoved(Collections.singletonList(e)));
            dialog.open();
        }
    }

    //called by the calendar's entry drop listener (i. e. an entry has been dragged around / moved by the user)
    protected void onEntryDropped(EntryDroppedEvent event) {
        event.applyChangesOnEntry();
        onEntryChanged(event.getEntry());
    }

    protected void onEntryResized(EntryResizedEvent event) {
        event.applyChangesOnEntry();
        onEntryChanged(event.getEntry());
    }


    protected void onWeekNumberClicked(WeekNumberClickedEvent event) {
        calendar.changeView(TIME_GRID_WEEK);
        calendar.gotoDate(event.getDate());
    }

//Called by the calendar's dates rendered listener. Noop by default.
//Please note, that there is a separate dates rendered listener taking care of updating the toolbar.
    protected void onDatesRendered(DatesRenderedEvent event) {
        toolbar.updateDate(event.getIntervalStart());
    }

    protected void onViewChanged(ViewSkeletonRenderedEvent event){
        event.getCalendarView().ifPresent(toolbar::updateSelectedView);
    }

    protected void onTimeslotsSelected(TimeslotsSelectedEvent event) {
        System.out.println("getStart(): " + event.getStart());
        System.out.println("getStartWithOffset():  " + event.getStartWithOffset());
        Entry entry = new Entry();
        entry.setStart(event.getStart());
        entry.setEnd(event.getEnd());
        entry.setAllDay(event.isAllDay());
        entry.setColor("dodgerblue");
        entry.setCalendar(event.getSource());
        EntryDialog dialog = new EntryDialog(entry, true);

        dialog.setSaveConsumer(e -> onEntriesCreated(Collections.singletonList(e)));
        dialog.setDeleteConsumer(e -> onEntriesRemoved(Collections.singletonList(e)));
        dialog.open();

    }


    protected void onDayNumberClicked(DayNumberClickedEvent event) {
        calendar.changeView(TIME_GRID_DAY);
        calendar.gotoDate(event.getDate());
    }


    protected void onEntriesCreated(Collection<Entry> entries) {
        entryService.saveCalendarEntries(entries);
        if (getCalendar().isInMemoryEntryProvider()) {
            InMemoryEntryProvider<Entry> entryProvider = getCalendar().getEntryProvider();
            entryProvider.addEntries(entries);
            entryProvider.refreshAll();
        }
    }


    protected void onEntriesRemoved(Collection<Entry> entries) {
        entryService.removeCalendarEntries(entries);
        if (getCalendar().isInMemoryEntryProvider()) {
            InMemoryEntryProvider<Entry> provider = getCalendar().getEntryProvider();
            provider.removeEntries(entries);
            provider.refreshAll();
        }
    }

    protected void onEntryChanged(Entry entry) {
        entryService.updateCalendarEntry(entry);
        if (getCalendar().isInMemoryEntryProvider()) {
            getCalendar().getEntryProvider().refreshItem(entry);
        }
    }
}
