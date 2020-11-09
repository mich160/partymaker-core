package controller.planning;

import db.DBConnectionProvider;
import db.DBInitializer;
import db.PostgresqlConnectionProvider;
import model.Participation;
import model.Party;
import model.User;
import repository.PartyRepository;
import repository.UserRepository;
import view.planning.EventPlanning;
import view.planning.EventPlanningWindow;
import view.planning.modelview.UserView;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventPlanningController {
    private EventPlanning eventPlanningWindow;
    private DBInitializer tablesInitializer;
    private UserRepository userRepository;
    private PartyRepository partyRepository;
    private List<Long> users_id;
    private Long party_id;

    public void start() {
        eventPlanningWindow = new EventPlanningWindow();
        eventPlanningWindow.setOnSaveEvent(this::saveEventInDatabase);

        DBConnectionProvider dbConnectionProviderForDBInitializer = new PostgresqlConnectionProvider();
        try{
            tablesInitializer = new DBInitializer(dbConnectionProviderForDBInitializer.getConnection());
        }catch(SQLException e){
            System.out.println("Error during initializing connection");
            e.printStackTrace();
        }
        try{
            tablesInitializer.createTables();
        }catch(SQLException e){
            System.out.println("Error during creating tables");
            e.printStackTrace();
        }
    }

    private void saveEventInDatabase() {
        String eventName = eventPlanningWindow.getEventName();
        LocalDateTime eventDate = eventPlanningWindow.getEventDate();
        List<UserView> users = eventPlanningWindow.getUsers();

        createPartyInDatabase(eventName, eventDate);
        createUsersInDatabase(users);
        createPartitipationsInDatabase(users_id, party_id);
    }

    private void createPartyInDatabase(String eventName, LocalDateTime eventDate){
        DBConnectionProvider postgresqlConnectionProvider = new PostgresqlConnectionProvider();
        party_id = null;
        Party tempParty = new Party();
        try{
            partyRepository = new PartyRepository(postgresqlConnectionProvider.getConnection());
        }catch (SQLException e){
            e.printStackTrace();
        }

        try{
            tempParty = partyRepository.create(new Party(eventName, eventDate));
        }catch(SQLException e){
            e.printStackTrace();
        }
        party_id = tempParty.getId();
    }

    private void createUsersInDatabase(List<UserView> listOfUsers) {
        DBConnectionProvider postgresqlConnectionProvider = new PostgresqlConnectionProvider();
        users_id = new ArrayList<>();
        User tempUser = new User();
        try{
            userRepository = new UserRepository(postgresqlConnectionProvider.getConnection());
        } catch(SQLException e){
            e.printStackTrace();
        }

        for(UserView user : listOfUsers){
            try{
                tempUser = userRepository.create(new User(user.getName()));
            }catch(SQLException e){
                e.printStackTrace();
            }
            users_id.add(tempUser.getId());
        }
    }

    private void createPartitipationsInDatabase(List<Long> users_id, Long party_id){
        List<Participation> participationsList = new ArrayList<>();
        for(Long user_id : users_id){
            Participation participation = new Participation(party_id, user_id);
            participationsList.add(participation);
        }
    }
}
