package controller.planning;

import db.DBConnectionProvider;
import db.DBInitializer;
import db.PostgresqlConnectionProvider;
import model.Participation;
import model.Party;
import model.Thing;
import model.User;
import repository.ParticipationRepository;
import repository.PartyRepository;
import repository.ThingRepository;
import repository.UserRepository;
import view.planning.EventPlanning;
import view.planning.EventPlanningWindow;
import view.planning.modelview.ThingView;
import view.planning.modelview.UserView;

import javax.swing.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

public class EventPlanningController {
    private EventPlanning eventPlanningWindow;
    private DBInitializer tablesInitializer;
    private PartyRepository partyRepository;
    private UserRepository userRepository;
    private ParticipationRepository participationRepository;
    private ThingRepository thingRepository;
    private Long partyID;
    private List<Long> usersIDs;
    private List<ThingView> listOfThingsUserHas;
    private List<Long> numberOfThingsUserHas;
    private List<Long> participationsIDs;

    public void start() {
        eventPlanningWindow = new EventPlanningWindow();
        eventPlanningWindow.setOnSaveEvent(this::saveEventInDatabase);

        DBConnectionProvider dbConnectionProviderForDBInitializer = new PostgresqlConnectionProvider();
        try {
            tablesInitializer = new DBInitializer(dbConnectionProviderForDBInitializer.getConnection());
            tablesInitializer.createTables();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error during initialization!", "Error", JOptionPane.INFORMATION_MESSAGE);
            e.printStackTrace();
        }
    }

    private void saveEventInDatabase() {
        String eventName = eventPlanningWindow.getEventName();
        LocalDateTime eventDate = eventPlanningWindow.getEventDate();
        List<UserView> users = eventPlanningWindow.getUsers();

        createPartyInDatabase(eventName, eventDate);
        createUsersInDatabase(users);
        createParticipationsInDatabase(usersIDs, partyID);
        createThingsInDatabase(listOfThingsUserHas, participationsIDs);
    }

    private void createPartyInDatabase(String partyName, LocalDateTime partyDateTime) {
        DBConnectionProvider postgresqlConnectionProvider = new PostgresqlConnectionProvider();
        partyID = null;
        Party party = new Party();
        try {
            partyRepository = new PartyRepository(postgresqlConnectionProvider.getConnection());
            party = partyRepository.create(new Party(partyName, partyDateTime));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        partyID = party.getId();
    }

    private void createUsersInDatabase(List<UserView> partyUsers) {
        DBConnectionProvider postgresqlConnectionProvider = new PostgresqlConnectionProvider();
        usersIDs = new ArrayList<>();
        listOfThingsUserHas = new ArrayList<>();
        numberOfThingsUserHas = new ArrayList<>();
        User user;
        try {
            userRepository = new UserRepository(postgresqlConnectionProvider.getConnection());
            for (UserView userFromList : partyUsers) {
                user = userRepository.create(new User(userFromList.getName()));
                usersIDs.add(user.getId());
                for (ThingView thing : userFromList.getThings()) {
                    listOfThingsUserHas.add(thing);
                }
                numberOfThingsUserHas.add((long) userFromList.getThings().size());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createParticipationsInDatabase(List<Long> partyUsers_id, Long partyParty_id) {
        DBConnectionProvider dbConnectionProvider = new PostgresqlConnectionProvider();
        Participation participation;
        participationsIDs = new ArrayList<>();
        try {
            participationRepository = new ParticipationRepository(dbConnectionProvider.getConnection());
            int indexOfNumberOfThingsUserHasList = 0;
            for (Long user_id : partyUsers_id) {
                Participation participationToSave = new Participation(partyParty_id, user_id);

                participation = participationRepository.create(participationToSave);

                for (int i = 0; i < numberOfThingsUserHas.get(indexOfNumberOfThingsUserHasList); i++) {
                    participationsIDs.add(participation.getId());
                }
                indexOfNumberOfThingsUserHasList++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createThingsInDatabase(List<ThingView> partyListOfThingsUserHas, List<Long> partyPartcipations_id) {
        DBConnectionProvider dbConnectionProvider = new PostgresqlConnectionProvider();
        try {
            thingRepository = new ThingRepository(dbConnectionProvider.getConnection());
            for (int i = 0; i < partyPartcipations_id.size(); i++) {
                Thing thing = new Thing(partyListOfThingsUserHas.get(i).toString(), partyPartcipations_id.get(i));
                thingRepository.create(thing);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
