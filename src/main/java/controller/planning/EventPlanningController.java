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
    private Long party_id;
    private List<Long> users_id;
    private List<ThingView> listOfThingsUserHas;
    private List<Long> numberOfThingsUserHas;
    private List<Long> participations_ids;
    
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
        createParticipationsInDatabase(users_id, party_id);
        createThingsInDatabase(listOfThingsUserHas, participations_ids);
    }

    private void createPartyInDatabase(String partyName, LocalDateTime partyDateTime){
        DBConnectionProvider postgresqlConnectionProvider = new PostgresqlConnectionProvider();
        party_id = null;
        Party tempParty = new Party();
        try{
            partyRepository = new PartyRepository(postgresqlConnectionProvider.getConnection());
        }catch (SQLException e){
            e.printStackTrace();
        }

        try{
            tempParty = partyRepository.create(new Party(partyName, partyDateTime));
        }catch(SQLException e){
            e.printStackTrace();
        }
        party_id = tempParty.getId();
    }

    private void createUsersInDatabase(List<UserView> partyUsers) {
        DBConnectionProvider postgresqlConnectionProvider = new PostgresqlConnectionProvider();
        users_id = new ArrayList<>();
        listOfThingsUserHas = new ArrayList<>();
        numberOfThingsUserHas = new ArrayList<>();
        User tempUser = new User();
        try{
            userRepository = new UserRepository(postgresqlConnectionProvider.getConnection());
        } catch(SQLException e){
            e.printStackTrace();
        }

        for(UserView user : partyUsers){
            try{
                tempUser = userRepository.create(new User(user.getName()));
            }catch(SQLException e){
                e.printStackTrace();
            }
            users_id.add(tempUser.getId());
            for(ThingView thing: user.getThings()){
                listOfThingsUserHas.add(thing);
            }
            numberOfThingsUserHas.add((long) user.getThings().size());
        }
    }

    private void createParticipationsInDatabase(List<Long> partyUsers_id, Long partyParty_id){
        DBConnectionProvider dbConnectionProvider = new PostgresqlConnectionProvider();
        Participation tempParticipation = new Participation();
        participations_ids = new ArrayList<>();
        try{
            participationRepository = new ParticipationRepository(dbConnectionProvider.getConnection());
        }catch (SQLException e){
            e.printStackTrace();
        }
        int indexOfNumberOfThingsUserHasList = 0;
        for(Long user_id : partyUsers_id){
            Participation participation = new Participation(partyParty_id, user_id);
            try{
                tempParticipation = participationRepository.create(participation);
            }catch (SQLException e){
                e.printStackTrace();
            }
            for(int i=0; i<numberOfThingsUserHas.get(indexOfNumberOfThingsUserHasList); i++){
                participations_ids.add(tempParticipation.getId());
            }
            indexOfNumberOfThingsUserHasList++;
        }
    }

    private void createThingsInDatabase(List<ThingView> partyListOfThingsUserHas, List<Long> partyPartcipations_id){
        DBConnectionProvider dbConnectionProvider = new PostgresqlConnectionProvider();
        Map<ThingView, Long> mapOfThingsAndParticipations_ids = new LinkedHashMap<>();
        for(int i=0; i<partyPartcipations_id.size(); i++)
        {
            mapOfThingsAndParticipations_ids.put(partyListOfThingsUserHas.get(i), partyPartcipations_id.get(i));
        }
        System.out.println();
        try{
            thingRepository = new ThingRepository(dbConnectionProvider.getConnection());
        }catch(SQLException e){
            e.printStackTrace();
        }
        for(Map.Entry<ThingView, Long> entry: mapOfThingsAndParticipations_ids.entrySet()){
            Thing thing = new Thing(entry.getKey().toString(), entry.getValue());
            try{
                thingRepository.create(thing);
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }
}
