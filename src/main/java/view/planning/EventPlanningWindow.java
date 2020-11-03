package view.planning;

import db.DBConnectionProvider;
import db.DBInitializer;
import db.H2ConnectionProvider;
import model.Thing;
import model.User;
import org.jdatepicker.JDatePicker;
import view.exceptions.NotImplementedException;
import view.planning.components.JLocalDatePicker;
import view.planning.components.JTimeSpinner;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

public class EventPlanningWindow extends JFrame implements EventPlanning {
    private final JPanel timePlanningPanel;
    private final JLocalDatePicker eventDatePicker;
    private final JTimeSpinner eventTimeSpinner;
    private final JPanel detailsPanel;
    private final JTextField titleField;
    private final JTextArea descriptionArea;
    private final JButton guestsButton;
    private final JButton saveEventButton;

    private final JLabel currentDateTime;
    private final JTextArea summaryText;
    private final JScrollPane summaryScrollPane;
    private final JPanel summaryPanel;

    private final JLabel personNameLabel;
    private final JTextField personNameTextField;
    private final JLabel personSurnameLabel;
    private final JTextField personSurnameTextField;
    private final JLabel thingNameLabel;
    private final JTextField thingNameTextField;
    private final JButton addingButton;
    private final JPanel addingPanel;

    private final DBConnectionProvider dbConnectionProvider;
    private final Connection connection;
    private final DBInitializer dbInitializer;

    public EventPlanningWindow() throws HeadlessException, SQLException {
        super("Baula Event Planning");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 505, 300);
        setResizable(false);
        setLayout(new BorderLayout());

        eventDatePicker = createEventDatePicker();
        eventTimeSpinner = createEventTimeSpinner();
        timePlanningPanel = createTimePlanningPanel(eventDatePicker, eventTimeSpinner);
        add(timePlanningPanel, BorderLayout.WEST);

        titleField = createTitleField();
        descriptionArea = createDescriptionArea();
        guestsButton = createGuestsButtton();
        detailsPanel = createDetailsPanel(titleField, descriptionArea, guestsButton);
        //add(detailsPanel, BorderLayout.EAST);

        personNameLabel = createPersonNameLabel();
        personNameTextField = createPersonNameTextField();
        personSurnameLabel = createPersonSurnameLabel();
        personSurnameTextField = createPersonSurnameTextField();
        thingNameLabel = createThingNameLabel();
        thingNameTextField = createThingNameTextField();
        addingButton = createAddingButton();
        addingPanel = createAddingPanel(personNameLabel, personNameTextField, personSurnameLabel, personSurnameTextField,
                thingNameLabel, thingNameTextField, addingButton);
        add(addingPanel, BorderLayout.CENTER);

        currentDateTime = createCurrentDateTime();
        summaryText = createSummaryText();
        summaryScrollPane = createSummaryScrollPane();
        summaryPanel = createSummaryPanel(currentDateTime, summaryText, summaryScrollPane);
        add(summaryPanel, BorderLayout.EAST);

        saveEventButton = createSaveEventButton();
        add(saveEventButton, BorderLayout.SOUTH);

        Map<Thing, User> tempMap = new HashMap<>();

        dbConnectionProvider = createConnectionProvider();
        connection = dbConnectionProvider.getConnection();
        dbInitializer = new DBInitializer(connection);
        dbInitializer.createTables();

        saveEventButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StringBuilder str = new StringBuilder();
                for (Thing key : tempMap.keySet()) {
                    str.append(tempMap.get(key))
                            .append("= ")
                            .append(key)
                            .append("\n");
                }
                System.out.println(str.toString());
            }
        });

        addingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                User tempUser = new User();
                Thing tempThing = new Thing();

                tempUser.setName(personNameTextField.getText());
                tempUser.setSurname(personSurnameTextField.getText());
                tempThing.setName(thingNameTextField.getText());

                tempMap.put(tempThing, tempUser);

                StringBuilder str = new StringBuilder();
                for (Thing key : tempMap.keySet()) {
                    str.append(tempMap.get(key))
                            .append("= ")
                            .append(key)
                            .append("\n");
                }
                summaryText.setText(str.toString());
                personNameTextField.setText("");
                personSurnameTextField.setText("");
                thingNameTextField.setText("");
            }
        });
        setVisible(true);
    }

    private JPanel createDetailsPanel(JTextField titleField, JTextArea descriptionArea, JButton guestsButton) {
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.add(titleField);
        detailsPanel.add(descriptionArea);
        detailsPanel.add(guestsButton);
        return detailsPanel;
    }

    private JPanel createSummaryPanel(JLabel currentDate, JTextArea summaryText, JScrollPane summaryScrollPane){
        JPanel summaryPanel = new JPanel();
        summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.Y_AXIS));
        summaryPanel.add(currentDate);
        summaryScrollPane = new JScrollPane(summaryText);
        summaryPanel.add(summaryScrollPane);
        return summaryPanel;
    }

    private JPanel createAddingPanel(JLabel personNameLabel, JTextField personNameTextField,
                                     JLabel personSurnameLabel, JTextField personSurnameTextField,
                                     JLabel thingNameLabel, JTextField thingNameTextField, JButton addingButton)
    {
        JPanel addingPanel = new JPanel();
        addingPanel.setLayout(new BoxLayout(addingPanel, BoxLayout.Y_AXIS));
        addingPanel.add(personNameLabel);
        addingPanel.add(personNameTextField);
        addingPanel.add(personSurnameLabel);
        addingPanel.add(personSurnameTextField);
        addingPanel.add(thingNameLabel);
        addingPanel.add(thingNameTextField);
        addingPanel.add(addingButton);
        return addingPanel;
    }

    private JPanel createTimePlanningPanel(JDatePicker eventDatePicker, JSpinner eventTimeSpinner) {
        JPanel timePlanningPanel = new JPanel();
        timePlanningPanel.setLayout(new BoxLayout(timePlanningPanel, BoxLayout.Y_AXIS));
        timePlanningPanel.add(eventDatePicker);
        timePlanningPanel.add(eventTimeSpinner);
        return timePlanningPanel;
    }

    private JTextField createTitleField() {
        return new JTextField("tytul");
    }

    private JTextArea createDescriptionArea() {
        JTextArea descriptionArea = new JTextArea("opis");
        descriptionArea.setLineWrap(true);
        return descriptionArea;
    }

    private JButton createGuestsButtton() {
        return new JButton("Goscie");
    }

    private JLabel createCurrentDateTime() { return new JLabel(String.valueOf(getCurrentDateTime()));};

    private JTextArea createSummaryText() { return new JTextArea(); }

    private JLocalDatePicker createEventDatePicker() {
        return new JLocalDatePicker();
    }

    private JTimeSpinner createEventTimeSpinner() {
        return new JTimeSpinner();
    }

    private JButton createSaveEventButton() {
        return new JButton("Zapisz impreze");
    }

    private JLabel createPersonNameLabel() {return new JLabel("Person (Name):");}

    private JLabel createPersonSurnameLabel() {return new JLabel("Person (Surname):");}

    private JTextField createPersonNameTextField() { return new JTextField();}

    private JTextField createPersonSurnameTextField() { return new JTextField();}

    private JLabel createThingNameLabel() { return new JLabel("Thing:");}

    private JTextField createThingNameTextField() { return new JTextField();}

    private JButton createAddingButton() { return new JButton("Add");}

    private JScrollPane createSummaryScrollPane() {return new JScrollPane();}

    private H2ConnectionProvider createConnectionProvider() { return new H2ConnectionProvider();}

    @Override
    public LocalDateTime getSelectedDateTime() {
        LocalTime selectedTime = eventTimeSpinner.getSelectedTime();
        LocalDate selectedDate = eventDatePicker.getSelectedDate();
        return LocalDateTime.of(selectedDate, selectedTime);
    }

    @Override
    public void setSelectedDateTime(LocalDateTime localDateTime) {
        eventTimeSpinner.setSelectedTime(localDateTime.toLocalTime());
        eventDatePicker.setSelectedDate(localDateTime.toLocalDate());
    }

    @Override
    public String getEventTitle() {
        return titleField.getText();
    }

    @Override
    public void setEventTitle(String title) {
        titleField.setText(title);
    }

    @Override
    public String getEventDescription() {
        return descriptionArea.getText();
    }

    @Override
    public void setEventDescription(String eventDescription) {
        descriptionArea.setText(eventDescription);
    }

    @Override
    public LocalDateTime getCurrentDateTime(){ return LocalDateTime.now(); }

    @Override
    public void setOnDateSelected(Runnable onDateSelected) {
        throw new NotImplementedException();
    }

    @Override
    public void setOnEventSaved(Runnable onEventSaved) {
        throw new NotImplementedException();
    }

    @Override
    public void setOnShowGuestList(Runnable onShowGuestList) {
        throw new NotImplementedException();
    }

    @Override
    public void setOnEventPlanningClosed(Runnable onEventPlanningClosed) {
        throw new NotImplementedException();
    }
}