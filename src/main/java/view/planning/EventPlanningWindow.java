package view.planning;

import org.jdatepicker.JDatePicker;
import view.exceptions.NotImplementedException;
import view.planning.components.JLocalDatePicker;
import view.planning.components.JTimeSpinner;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class EventPlanningWindow extends JFrame implements EventPlanning {
    private final JPanel timePlanningPanel;
    private final JLocalDatePicker eventDatePicker;
    private final JTimeSpinner eventTimeSpinner;
    private final JPanel detailsPanel;
    private final JTextField titleField;
    private final JTextArea descriptionArea;
    private final JButton guestsButton;
    private final JButton saveEventButton;

    public EventPlanningWindow() throws HeadlessException {
        super("Baula Event Planning");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        eventDatePicker = createEventDatePicker();
        eventTimeSpinner = createEventTimeSpinner();
        timePlanningPanel = createTimePlanningPanel(eventDatePicker, eventTimeSpinner);
        add(timePlanningPanel, BorderLayout.WEST);

        titleField = createTitleField();
        descriptionArea = createDescriptionArea();
        guestsButton = createGuestsButtton();
        detailsPanel = createDetailsPanel(titleField, descriptionArea, guestsButton);
        add(detailsPanel, BorderLayout.EAST);

        saveEventButton = createSaveEventButton();
        add(saveEventButton, BorderLayout.SOUTH);
        pack();
    }

    private JPanel createDetailsPanel(JTextField titleField, JTextArea descriptionArea, JButton guestsButton) {
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.add(titleField);
        detailsPanel.add(descriptionArea);
        detailsPanel.add(guestsButton);
        return detailsPanel;
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

    private JLocalDatePicker createEventDatePicker() {
        return new JLocalDatePicker();
    }

    private JTimeSpinner createEventTimeSpinner() {
        return new JTimeSpinner();
    }

    private JButton createSaveEventButton() {
        return new JButton("Zapisz impreze");
    }

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
