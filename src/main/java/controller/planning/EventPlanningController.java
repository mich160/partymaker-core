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

import javax.swing.*;
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
    private Long partyID;
    private List<Long> guestsIDs;
    private List<ContributionView> listOfContributionsGuestHas;
    private List<Long> numberOfContributionsGuestHas;
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
        List<GuestView> guests = eventPlanningWindow.getGuests();

        createPartyInDatabase(eventName, eventDate);
        createGuestsInDatabase(guests);
        createParticipationsInDatabase(guestsIDs, partyID);
        createContributionsInDatabase(listOfContributionsGuestHas, participationsIDs);
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

    private void createGuestsInDatabase(List<GuestView> partyGuests) {
        DBConnectionProvider postgresqlConnectionProvider = new PostgresqlConnectionProvider();
        guestsIDs = new ArrayList<>();
        listOfContributionsGuestHas = new ArrayList<>();
        numberOfContributionsGuestHas = new ArrayList<>();
        Guest guest;
        try {
            guestRepository = new GuestRepository(postgresqlConnectionProvider.getConnection());
            for (GuestView guestFromList : partyGuests) {
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
    }

    private void createParticipationsInDatabase(List<Long> partyGuests_id, Long partyParty_id) {
        DBConnectionProvider dbConnectionProvider = new PostgresqlConnectionProvider();
        Participation participation;
        participationsIDs = new ArrayList<>();
        try {
            participationRepository = new ParticipationRepository(dbConnectionProvider.getConnection());
            int indexOfNumberOfContributionsGuestHasList = 0;
            for (Long guest_id : partyGuests_id) {
                Participation participationToSave = new Participation(partyParty_id, guest_id);

                participation = participationRepository.create(participationToSave);

                for (int i = 0; i < numberOfContributionsGuestHas.get(indexOfNumberOfContributionsGuestHasList); i++) {
                    participationsIDs.add(participation.getId());
                }
                indexOfNumberOfContributionsGuestHasList++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createContributionsInDatabase(List<ContributionView> partyListOfContributionsGuestHas, List<Long> partyPartcipations_id) {
        DBConnectionProvider dbConnectionProvider = new PostgresqlConnectionProvider();
        try {
            contributionRepository = new ContributionRepository(dbConnectionProvider.getConnection());
            for (int i = 0; i < partyPartcipations_id.size(); i++) {
                Contribution contribution = new Contribution(partyListOfContributionsGuestHas.get(i).toString(), partyPartcipations_id.get(i));
                contributionRepository.create(contribution);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
