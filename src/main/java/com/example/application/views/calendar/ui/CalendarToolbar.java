package com.example.application.views.calendar.ui;

import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import org.vaadin.stefan.fullcalendar.CalendarView;
import org.vaadin.stefan.fullcalendar.CalendarViewImpl;
import org.vaadin.stefan.fullcalendar.FullCalendar;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import static org.vaadin.stefan.fullcalendar.CalendarViewImpl.*;


public class CalendarToolbar extends MenuBar {

    private static final long serialVersionUID = 1L;

    private Button buttonDatePicker;
    private MenuItem viewSelector;

    private ComboBox<CalendarView> viewComboBox;
    private CalendarView selectedView = CalendarViewImpl.DAY_GRID_MONTH;


    public CalendarToolbar(FullCalendar calendar){
        addThemeVariants(MenuBarVariant.LUMO_SMALL);

        addItem(VaadinIcon.ANGLE_LEFT.create(), e -> calendar.previous()).setId("period-previous-button");

        DatePicker datePicker = new DatePicker();
        datePicker.addValueChangeListener(e -> calendar.gotoDate(e.getValue()));
        datePicker.getElement().getStyle().set("visibility", "hidden");
        datePicker.getElement().getStyle().set("position", "absolute");
        datePicker.setWidth("0px");
        datePicker.setHeight("0px");
        datePicker.setWeekNumbersVisible(true);
        datePicker.setI18n(new DatePicker.DatePickerI18n().setFirstDayOfWeek(1));
        buttonDatePicker = new Button(VaadinIcon.CALENDAR.create());
        buttonDatePicker.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH)));
        buttonDatePicker.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        buttonDatePicker.getElement().appendChild(datePicker.getElement());
        buttonDatePicker.addClickListener(event -> datePicker.open());
        buttonDatePicker.setWidthFull();

        addItem(buttonDatePicker);
        addItem(VaadinIcon.ANGLE_RIGHT.create(), e -> calendar.next());
        addItem("Today", e -> calendar.today());

        final List<CalendarView> calendarViews = List.of(DAY_GRID_MONTH, TIME_GRID_DAY, TIME_GRID_WEEK, MULTI_MONTH);
        viewSelector = addItem(getViewName(selectedView));
        SubMenu subMenu = viewSelector.getSubMenu();
        calendarViews.stream()
                .sorted(Comparator.comparing(this::getViewName))
                .forEach(view -> {
                    String viewName = getViewName(view);
                    subMenu.addItem(viewName, event -> {
                        calendar.changeView(view);
                        viewSelector.setText(viewName);
                        selectedView = view;
                    });
                });
    }

    private String getViewName(CalendarView view) {
        switch ((CalendarViewImpl) view){
                        case DAY_GRID_MONTH:
                            return "Month";
                        case TIME_GRID_DAY:
                            return "Day";
                        case TIME_GRID_WEEK:
                            return "Week";
                        case MULTI_MONTH:
                            return "Year";
                        default:
                            return "Undefined";
        }
    }


    public void updateDate(LocalDate intervalStart) {
        if(buttonDatePicker != null && selectedView != null){
            updateIntervalLabel(buttonDatePicker, selectedView, intervalStart);
        }
    }
    public void updateSelectedView(CalendarView view) {
        if (viewSelector != null) {
            viewSelector.setText(getViewName(view));
        }
        selectedView = view;
    }

    public void updateIntervalLabel(
            final HasText intervalLabel, final CalendarView calendarView, final LocalDate intervalStart) { //local date
        String text = " ";
        if (calendarView == null) {
            text = intervalStart.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH));
        } else if (calendarView instanceof CalendarViewImpl) {
            switch ((CalendarViewImpl) calendarView) {
                case DAY_GRID_MONTH:
                    text = intervalStart.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH));
                    break;
                case TIME_GRID_DAY:
                    text = intervalStart.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                    break;
                case TIME_GRID_WEEK:
                    text = intervalStart.format(DateTimeFormatter.ofPattern("dd.MM.yy")) + " - " + intervalStart.plusDays(6).format(DateTimeFormatter.ofPattern("dd.MM.yy"));
                    break;
                case MULTI_MONTH:
                    text = intervalStart.format(DateTimeFormatter.ofPattern("yyyy"));
                    break;
            }
        }
        intervalLabel.setText(text);
    }

}

