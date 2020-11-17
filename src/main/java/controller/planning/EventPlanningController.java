package controller.planning;

import db.DBConnectionProvider;
import db.DBInitializer;
import db.PostgresqlConnectionProvider;
import model.Guest;
import model.Participation;
import model.Party;
import model.Contribution;
import repository.ParticipationRepository;
import repository.PartyRepository;
import repository.ContributionRepository;
import repository.GuestRepository;
import view.planning.EventPlanning;
import view.planning.EventPlanningWindow;
import view.planning.modelview.ContributionView;
import view.planning.modelview.GuestView;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class EventPlanningController {
    private EventPlanning eventPlanningWindow;
    private DBInitializer tablesInitializer;
    private PartyRepository partyRepository;
    private GuestRepository guestRepository;
    private ParticipationRepository participationRepository;
    private ContributionRepository contributionRepository;

    public EventPlanningController() {
        eventPlanningWindow = new EventPlanningWindow();
        eventPlanningWindow.setOnSaveEvent(this::saveEventInDatabase);
        DBConnectionProvider dbConnectionProvider = new PostgresqlConnectionProvider();

        try {
            this.tablesInitializer = new DBInitializer(dbConnectionProvider.getConnection());
            tablesInitializer.createTables();
            this.partyRepository = new PartyRepository(dbConnectionProvider.getConnection());
            this.guestRepository = new GuestRepository(dbConnectionProvider.getConnection());
            this.participationRepository = new ParticipationRepository(dbConnectionProvider.getConnection());
            this.contributionRepository = new ContributionRepository(dbConnectionProvider.getConnection());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveEventInDatabase() {
        String eventName = eventPlanningWindow.getEventName();
        LocalDateTime eventDate = eventPlanningWindow.getEventDate();
        List<GuestView> guests = eventPlanningWindow.getGuests();

        Long partyID = createPartyInDatabase(eventName, eventDate);
        List<Guest> guestsInDb = createGuestsInDataBase(guests);
        List<Participation> participationsInDb = createParticipationsInDb(partyID, guestsInDb);
        createContributionsInDb(guests, guestsInDb, participationsInDb);
    }

    private void createContributionsInDb(List<GuestView> guests, List<Guest> guestsInDb, List<Participation> participationsInDb) {
        Map<String, Long> guestNameToId = guestsInDb.stream()
                .collect(Collectors.toMap(Guest::getName, Guest::getId));
        Map<Long, Long> guestIdToParticipationId = participationsInDb.stream()
                .collect(Collectors.toMap(Participation::getGuestID, Participation::getId));
        try {
            for (GuestView guest : guests) {
                for (ContributionView contribution : guest.getContributions()) {
                    Long guestId = guestNameToId.get(guest.getName());
                    Long participationId = guestIdToParticipationId.get(guestId);
                    contributionRepository.create(new Contribution(contribution.getName(), participationId));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private List<Participation> createParticipationsInDb(Long partyID, List<Guest> guestsInDb) {
        List<Participation> participationsInDb = new ArrayList<>(guestsInDb.size());
        try {
            for (Guest guest : guestsInDb) {
                participationsInDb.add(participationRepository.create(new Participation(partyID, guest.getId())));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return participationsInDb;
    }

    private List<Guest> createGuestsInDataBase(List<GuestView> guests) {
        List<Guest> savedGuests = guests.stream()
                .map(guestView -> new Guest(guestView.getName()))
                .collect(Collectors.toUnmodifiableList());
        List<Guest> guestsInDb = new ArrayList<>(savedGuests.size());
        try {
            for (Guest guest : savedGuests) {
                guestsInDb.add(guestRepository.create(guest));
            }
        } catch (SQLException ex) {
            ex.printStackTrace(); //TODO replace with proper handling
        }
        return guestsInDb;
    }

    private Long createPartyInDatabase(String eventName, LocalDateTime eventDate) {
        Party party = new Party();
        try {
            party = partyRepository.create(new Party(eventName, eventDate));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return party.getId();
    }
}
