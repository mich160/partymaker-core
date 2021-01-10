package view.planning;

import view.planning.components.JDateTimePicker;
import view.planning.modelview.ContributionView;
import view.planning.modelview.GuestView;

import javax.swing.*;
import java.awt.*;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.List;

public class EventPlanningWindow extends JFrame implements EventPlanning {
    private enum Column {
        FIRST(0.15),
        SECOND(0.15),
        THIRD(0.7);

        private final double weight;

        Column(double weight) {
            this.weight = weight;
        }
    }

    private enum Row {
        FIRST(0.04),
        SECOND(0.835),
        THIRD(0.03),
        FOURTH(0.03),
        FIFTH(0.035);

        private final double weight;

        Row(double weight) {
            this.weight = weight;
        }
    }

    private final JTextField eventName;
    private final JDateTimePicker dateTimePicker;
    private final DefaultListModel<String> guestListModel;
    private final JList<String> guestList;
    private final JList<String> contributionList;
    private final JTextArea summaryText;
    private Runnable onSave;

    private final LinkedHashMap<String, DefaultListModel<String>> dataMap = createDataMap();

    public EventPlanningWindow() throws HeadlessException {
        super("Baula Event Planning");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 605, 300);
        setResizable(false);
        setLayout(new GridBagLayout());

        eventName = createEventNameTextField();
        add(eventName, createGridBagConstraints(Column.FIRST.weight, Row.FIRST.weight, 0, 0));
        dateTimePicker = createJDateTimePicker();
        add(dateTimePicker, createGridBagConstraints(Column.SECOND.weight, Row.FIRST.weight, 1, 0));
        JLabel summaryTitle = createSummaryTitleJLabel();
        add(summaryTitle, createGridBagConstraints(Column.THIRD.weight, Row.FIRST.weight, 2, 0));

        guestListModel = createGuestListModel();
        guestList = createGuestList();
        contributionList = createContributionList();

        JScrollPane scrollBarGuestList = createScrollBarGuestList(guestList);
        add(scrollBarGuestList, createGridBagConstraints(Column.FIRST.weight, Row.SECOND.weight, 0, 1));

        setOnGuestListSelectedListener();

        JScrollPane scrollBarContributionList = createScrollContributionList(contributionList);
        add(scrollBarContributionList, createGridBagConstraints(Column.SECOND.weight, Row.SECOND.weight, 1, 1));
        summaryText = createSummaryText();
        JScrollPane scrollBarSummaryText = createScrollBarSummaryText(summaryText);
        GridBagConstraints scrollBarConstraints = createGridBagConstraints(Column.THIRD.weight, 0, 2, 1);
        scrollBarConstraints.gridheight = 3;
        add(scrollBarSummaryText, scrollBarConstraints);
        printDataMap(dataMap);

        JButton addGuestButton = createAddGuestButton();
        add(addGuestButton, createGridBagConstraints(Column.FIRST.weight, Row.THIRD.weight, 0, 2));
        setOnAddGuestButtonListener(addGuestButton);

        JButton addContributionButton = createAddContributionButton();
        add(addContributionButton, createGridBagConstraints(Column.SECOND.weight, Row.THIRD.weight, 1, 2));
        setOnAddContributionButtonListener(addContributionButton);

        JButton deleteGuestButton = createDeleteGuestButton();
        add(deleteGuestButton, createGridBagConstraints(Column.FIRST.weight, Row.FOURTH.weight, 0, 3));
        setOnDeleteGuestButtonListener(deleteGuestButton);

        JButton deleteContributionButton = createDeleteContributionButton();
        add(deleteContributionButton, createGridBagConstraints(Column.SECOND.weight, Row.FOURTH.weight, 1, 3));
        setOnDeleteContributionButtonListener(deleteContributionButton);

        JButton eventSaveButton = createSaveEventButton();
        GridBagConstraints eventSaveButtonConstraints = createGridBagConstraints(0, Row.FIFTH.weight, 0, 4);
        eventSaveButtonConstraints.gridwidth = 3;
        add(eventSaveButton, eventSaveButtonConstraints);
        eventSaveButton.addActionListener(e -> onSave.run());
        setVisible(true);
    }

    @Override
    public String getEventName() {
        return eventName.getText();
    }

    @Override
    public LocalDateTime getEventDate() {
        Date chosenDate = dateTimePicker.getDate();
        LocalDateTime eventDateTime = LocalDateTime.ofInstant(chosenDate.toInstant(), ZoneId.systemDefault());
        return eventDateTime;
    }

    @Override
    public List<GuestView> getGuests() {
        List<GuestView> guestViews = new ArrayList<>();
        for (Map.Entry<String, DefaultListModel<String>> entry : dataMap.entrySet()) {
            List<ContributionView> listOfContributionViews = new ArrayList<>();
            List<Object> listOfContributionViewsAsObjects = Arrays.asList(entry.getValue().toArray());

            for (Object contributionViewAsObject : listOfContributionViewsAsObjects) {
                ContributionView contributionView = new ContributionView(contributionViewAsObject.toString());
                listOfContributionViews.add(contributionView);
            }
            GuestView tempGuestView = new GuestView(entry.getKey(), listOfContributionViews);
            guestViews.add(tempGuestView);
        }
        return guestViews;
    }

    @Override
    public void setOnSaveEvent(Runnable action) {
        this.onSave = action;
    }

    private void setOnDeleteContributionButtonListener(JButton deleteContributionButton) {
        deleteContributionButton.addActionListener(e -> {
            var selModel = contributionList.getSelectionModel();
            int index = selModel.getMinSelectionIndex();
            if (index >= 0) {
                DefaultListModel<String> tempModel = dataMap.get(guestList.getSelectedValue());
                tempModel.remove(index);
                dataMap.put(guestList.getSelectedValue(), tempModel);
                if (index != 0) {
                    contributionList.setSelectedIndex(index - 1);
                } else {
                    contributionList.setSelectedIndex(0);
                }
                printDataMap(dataMap);
            }
        });
    }

    private void setOnDeleteGuestButtonListener(JButton deleteGuestButton) {
        deleteGuestButton.addActionListener(e -> {
            var selModel = guestList.getSelectionModel();
            int index = selModel.getMinSelectionIndex();
            if (index >= 0) {
                String key = guestList.getSelectedValue();
                DefaultListModel<String> tempModel = dataMap.get(guestList.getSelectedValue());
                dataMap.remove(key);
                guestListModel.remove(index);
                if (index != 0) {
                    guestList.setSelectedIndex(index - 1);
                } else {
                    guestList.setSelectedIndex(0);
                    tempModel.removeAllElements();
                }
                printDataMap(dataMap);
            }
        });
    }

    private void setOnAddContributionButtonListener(JButton addContributionButton) {
        addContributionButton.addActionListener(e -> {
            var text = JOptionPane.showInputDialog("Add a new contribution");
            String item;

            if (text != null) {
                item = text.trim();
            } else {
                return;
            }

            if (!item.isEmpty()) {
                if (!dataMap.isEmpty()) {
                    DefaultListModel<String> tempModel = dataMap.get(guestList.getSelectedValue());
                    tempModel.addElement(item);
                    dataMap.put(guestList.getSelectedValue(), tempModel);
                    contributionList.setModel(tempModel);
                    contributionList.setSelectedIndex(tempModel.getSize() - 1);
                    printDataMap(dataMap);
                } else {
                    JLabel errorContributionWithoutGuest = new JLabel("Add some guest first!");
                    errorContributionWithoutGuest.setHorizontalAlignment(SwingConstants.LEFT);
                    JOptionPane.showMessageDialog(null, errorContributionWithoutGuest);
                }
            }
        });
    }

    private void setOnAddGuestButtonListener(JButton addGuestButton) {
        addGuestButton.addActionListener(e -> {
            var text = JOptionPane.showInputDialog("Add a new guest");
            String item;
            int size = guestListModel.getSize();

            if (text != null) {
                item = text.trim();
            } else {
                return;
            }

            if (!item.isEmpty()) {
                if (!(dataMap.containsKey(item))) {
                    dataMap.put(item, new DefaultListModel<>());
                    guestListModel.addElement(item);
                    guestList.setSelectedIndex(size);
                    printDataMap(dataMap);
                } else {
                    JLabel errorGuest = new JLabel("Such guest already exists!");
                    errorGuest.setHorizontalAlignment(SwingConstants.LEFT);
                    JOptionPane.showMessageDialog(null, errorGuest);
                }

            }
        });
    }

    private void setOnGuestListSelectedListener() {
        guestList.addListSelectionListener(e -> {
                    if (!e.getValueIsAdjusting()) {
                        if (guestList.getSelectedIndex() == -1) {
                            return;
                        } else
                            contributionList.setModel(dataMap.get(guestList.getSelectedValue()));
                        contributionList.setSelectedIndex(0);
                    }
                }
        );
    }

    private void printDataMap(LinkedHashMap<String, DefaultListModel<String>> dataMap) {
        String dataToPrint = formatDataMap(dataMap);
        summaryText.setText(dataToPrint);
        summaryText.setCaretPosition(0);
    }

    private String formatDataMap(LinkedHashMap<String, DefaultListModel<String>> dataMap) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry entry : dataMap.entrySet()) {
            stringBuilder.append(entry.getKey());
            stringBuilder.append(":");
            stringBuilder.append("\n");
            stringBuilder.append(entry.getValue());
            stringBuilder.append("\n\n");
        }
        return stringBuilder.toString().replaceAll("[\\[\\]]", "");
    }

    private LinkedHashMap<String, DefaultListModel<String>> createDataMap() {
        return new LinkedHashMap<>();
    }

    private GridBagConstraints createGridBagConstraints(double weightx, double weighty, int gridx, int gridy) {
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = weightx;
        gridBagConstraints.weighty = weighty;
        gridBagConstraints.gridx = gridx;
        gridBagConstraints.gridy = gridy;
        return gridBagConstraints;
    }

    private JTextField createEventNameTextField() {
        JTextField eventNameTextField = new JTextField("IMBRA :D");
        eventNameTextField.setHorizontalAlignment(JTextField.CENTER);
        return eventNameTextField;
    }

    private JDateTimePicker createJDateTimePicker() {
        Date date = new Date();
        JDateTimePicker dateTimePicker = new JDateTimePicker();
        dateTimePicker.setFormats(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM));
        dateTimePicker.setTimeFormat(DateFormat.getTimeInstance(DateFormat.MEDIUM));
        dateTimePicker.setDate(date);
        return dateTimePicker;
    }

    private JLabel createSummaryTitleJLabel() {
        JLabel summaryTitleJLabel = new JLabel("Summary");
        summaryTitleJLabel.setHorizontalAlignment(SwingConstants.CENTER);
        return summaryTitleJLabel;
    }

    private DefaultListModel<String> createGuestListModel() {
        return new DefaultListModel<>();
    }

    private JList<String> createGuestList() {
        for (Map.Entry<String, DefaultListModel<String>> entry : dataMap.entrySet()) {
            guestListModel.addElement(entry.getKey());
        }
        JList<String> guestList = new JList<>(guestListModel);
        guestList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        guestList.setLayoutOrientation(JList.VERTICAL);
        guestList.setSelectedIndex(0);
        guestList.setVisibleRowCount(-1);
        return guestList;
    }

    private JScrollPane createScrollBarGuestList(JList<String> jlist) {
        return new JScrollPane(jlist);
    }

    private JList<String> createContributionList() {
        JList<String> contributionList = new JList<>();
        contributionList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        contributionList.setLayoutOrientation(JList.VERTICAL);
        contributionList.setVisibleRowCount(-1);
        return contributionList;
    }

    private JScrollPane createScrollContributionList(JList<String> jlist) {
        return new JScrollPane(jlist);
    }

    private JTextArea createSummaryText() {
        JTextArea jTextArea = new JTextArea();
        jTextArea.setEditable(false);
        jTextArea.setLineWrap(true);
        return jTextArea;
    }

    private JScrollPane createScrollBarSummaryText(JTextArea jTextArea) {
        return new JScrollPane(jTextArea);
    }

    private JButton createAddGuestButton() {
        return new JButton("Add guest");
    }

    private JButton createAddContributionButton() {
        return new JButton("Add contribution");
    }

    private JButton createDeleteGuestButton() {
        return new JButton("Delete guest");
    }

    private JButton createDeleteContributionButton() {
        return new JButton("Delete contribution");
    }

    private JButton createSaveEventButton() {
        return new JButton("SAVE EVENT");
    }
}