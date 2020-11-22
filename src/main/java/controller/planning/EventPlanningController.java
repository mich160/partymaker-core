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
        Pair<ArrayList> data = createGuestsInDatabase(guests);
        ArrayList<Long> participationsIDs = createParticipationsInDatabase(data.guestsIDs, partyID, data.numberOfContributionsGuestHas);
        createContributionsInDatabase(data.listOfContributionsGuestHas, participationsIDs);
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

    class Pair<ArrayList> {
        private final ArrayList guestsIDs;
        private final ArrayList listOfContributionsGuestHas;
        private final ArrayList numberOfContributionsGuestHas;

        private Pair(ArrayList guestsIDs, ArrayList listOfContributionsGuestHas, ArrayList numberOfContributionsGuestHas) {
            this.guestsIDs = guestsIDs;
            this.listOfContributionsGuestHas = listOfContributionsGuestHas;
            this.numberOfContributionsGuestHas = numberOfContributionsGuestHas;
        }

        public ArrayList guestsIDs() {
            return guestsIDs;
        }

        public ArrayList listOfContributionsGuestHas() {
            return listOfContributionsGuestHas;
        }

        public ArrayList numberOfContributionsGuestHas() {
            return numberOfContributionsGuestHas;
        }
    }

    private Pair<ArrayList> createGuestsInDatabase(List<GuestView> guests) {
        ArrayList guestsIDs = new ArrayList<>();
        ArrayList listOfContributionsGuestHas = new ArrayList<>();
        ArrayList numberOfContributionsGuestHas = new ArrayList<>();
        Guest guest;
        try {
            for (GuestView guestFromList : guests) {
                guest = guestRepository.create(new Guest(guestFromList.getName()));
                guestsIDs.add(guest.getId());
                for (ContributionView contribution : guestFromList.getContributions()) {
                    listOfContributionsGuestHas.add(contribution);
                }
                numberOfContributionsGuestHas.add((long) guestFromList.getContributions().size());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new Pair<ArrayList>(guestsIDs, listOfContributionsGuestHas, numberOfContributionsGuestHas);
    }

    private ArrayList<Long> createParticipationsInDatabase(List<Long> guestsIDs, Long partyID, ArrayList<Long> numberOfContributionsGuestHas) {
        Participation participation;
        ArrayList participationsIDs = new ArrayList<>();
        try {
            int indexOfNumberOfContributionsGuestHasList = 0;
            for (Long guest_id : guestsIDs) {
                Participation participationToSave = new Participation(partyID, guest_id);

                participation = participationRepository.create(participationToSave);

                for (int i = 0; i < numberOfContributionsGuestHas.get(indexOfNumberOfContributionsGuestHasList); i++) {
                    participationsIDs.add(participation.getId());
                }
                indexOfNumberOfContributionsGuestHasList++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return participationsIDs;
    }

    private void createContributionsInDatabase(List<ContributionView> listOfContributionsGuestHas, List<Long> participationsIDs) {
        try {
            for (int i = 0; i < participationsIDs.size(); i++) {
                Contribution contribution = new Contribution(listOfContributionsGuestHas.get(i).toString(), participationsIDs.get(i));
                contributionRepository.create(contribution);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
