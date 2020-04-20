package view.planning;

import java.time.LocalDateTime;

public interface EventPlanning {
    LocalDateTime getSelectedDateTime();
    void setSelectedDateTime(LocalDateTime localDateTime);
    String getEventTitle();
    void setEventTitle(String title);
    String getEventDescription();
    void setEventDescription(String eventDescription);

    void setOnDateSelected(Runnable onDateSelected);
    void setOnEventSaved(Runnable onEventSaved);
    void setOnShowGuestList(Runnable onShowGuestList);
    void setOnEventPlanningClosed(Runnable onEventPlanningClosed);
}
