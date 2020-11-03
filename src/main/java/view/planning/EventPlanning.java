package view.planning;

import view.planning.modelview.UserView;

import java.time.LocalDateTime;
import java.util.List;

public interface EventPlanning {
    String getEventName();
    LocalDateTime getEventDate();
    List<UserView> getUsers();
    void setOnSaveEvent(Runnable action);
}
