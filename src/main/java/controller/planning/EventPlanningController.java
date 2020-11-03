package controller.planning;

import view.planning.EventPlanning;
import view.planning.EventPlanningWindow;
import view.planning.modelview.UserView;

import java.time.LocalDateTime;
import java.util.List;

public class EventPlanningController {
    private EventPlanning eventPlanningWindow;

    public void start() {
        eventPlanningWindow = new EventPlanningWindow();
        eventPlanningWindow.setOnSaveEvent(this::saveEventInDatabase);
    }

    private void saveEventInDatabase() {
        String eventName = eventPlanningWindow.getEventName();
        LocalDateTime eventDate = eventPlanningWindow.getEventDate();
        List<UserView> users = eventPlanningWindow.getUsers();
    }
}
