package controller.planning;

import db.DBConnectionProvider;
import db.DBInitializer;
import db.PostgresqlConnectionProvider;
import model.Party;
import model.User;
import repository.PartyRepository;
import repository.UserRepository;
import view.planning.EventPlanning;
import view.planning.EventPlanningWindow;
import view.planning.modelview.UserView;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class EventPlanningController {
    private EventPlanning eventPlanningWindow;
    private DBInitializer tablesInitializer;
    private UserRepository userRepository;
    private PartyRepository partyRepository;

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
    }

    private void createPartyInDatabase(String eventName, LocalDateTime eventDate){
        DBConnectionProvider postgresqlConnectionProvider = new PostgresqlConnectionProvider();
        try{
            partyRepository = new PartyRepository(postgresqlConnectionProvider.getConnection());
        }catch (SQLException e){
            e.printStackTrace();
        }

        try{
            partyRepository.create(new Party(eventName, eventDate));
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    private void createUsersInDatabase(List<UserView> listOfUsers) {
        DBConnectionProvider postgresqlConnectionProvider = new PostgresqlConnectionProvider();
        try{
            userRepository = new UserRepository(postgresqlConnectionProvider.getConnection());
        } catch(SQLException e){
            e.printStackTrace();
        }

        for(UserView user : listOfUsers){
            try{
                userRepository.create(new User(user.getName()));
            }catch(SQLException e){
                e.printStackTrace();
            }
        }
    }

    private void createPartitipationsInDatabase(List<UserView> listOfUsers, Party party){

    }
}
