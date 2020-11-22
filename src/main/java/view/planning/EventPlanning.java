package view.planning;

import view.planning.modelview.GuestView;

import java.time.LocalDateTime;
import java.util.List;

public interface EventPlanning {
    String getEventName();

    LocalDateTime getEventDate();

    List<GuestView> getGuests();

    void setOnSaveEvent(Runnable action);
}
