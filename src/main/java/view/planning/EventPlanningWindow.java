package view.planning;

import view.planning.components.JDateTimePicker;
import view.planning.modelview.ThingView;
import view.planning.modelview.UserView;

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
    private final DefaultListModel<String> userListModel;
    private final JList<String> userList;
    private final JList<String> thingList;
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

        userListModel = createUserListModel();
        userList = createUserList();
        thingList = createThingList();

        JScrollPane scrollBarUserList = createScrollBarUserList(userList);
        add(scrollBarUserList, createGridBagConstraints(Column.FIRST.weight, Row.SECOND.weight, 0, 1));

        setOnUserListSelectedListener();

        JScrollPane scrollBarThingList = createScrollThingList(thingList);
        add(scrollBarThingList, createGridBagConstraints(Column.SECOND.weight, Row.SECOND.weight, 1, 1));
        summaryText = createSummaryText();
        JScrollPane scrollBarSummaryText = createScrollBarSummaryText(summaryText);
        GridBagConstraints scrollBarConstraints = createGridBagConstraints(Column.THIRD.weight, 0, 2, 1);
        scrollBarConstraints.gridheight = 3;
        add(scrollBarSummaryText, scrollBarConstraints);
        printDataMap(dataMap);

        JButton addUserButton = createAddUserButton();
        add(addUserButton, createGridBagConstraints(Column.FIRST.weight, Row.THIRD.weight, 0, 2));
        setOnAddUserButtonListener(addUserButton);

        JButton addThingButton = createAddThingButton();
        add(addThingButton, createGridBagConstraints(Column.SECOND.weight, Row.THIRD.weight, 1, 2));
        setOnAddThingButtonListener(addThingButton);

        JButton deleteUserButton = createDeleteUserButton();
        add(deleteUserButton, createGridBagConstraints(Column.FIRST.weight, Row.FOURTH.weight, 0, 3));
        setOnDeleteUserButtonListener(deleteUserButton);

        JButton deleteThingButton = createDeleteThingButton();
        add(deleteThingButton, createGridBagConstraints(Column.SECOND.weight, Row.FOURTH.weight, 1, 3));
        setOnDeleteThingButtonListener(deleteThingButton);

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
        Date tempDate = dateTimePicker.getDate();
        LocalDateTime eventDateTime = LocalDateTime.ofInstant(tempDate.toInstant(), ZoneId.systemDefault());
        return eventDateTime;
    }

    @Override
    public List<UserView> getUsers() {
        List<UserView> listOfUsers = new ArrayList<>();
        for(Map.Entry<String, DefaultListModel<String>> entry: dataMap.entrySet()) {
            UserView tempUserView = new UserView(entry.getKey(), (List<ThingView>)(Object) Arrays.asList(entry.getValue().toArray()));
            listOfUsers.add(tempUserView);
        }
        return listOfUsers;
    }

    @Override
    public void setOnSaveEvent(Runnable action) {
        this.onSave = action;
    }

    private void setOnDeleteThingButtonListener(JButton deleteThingButton) {
        deleteThingButton.addActionListener(e -> {
            var selModel = thingList.getSelectionModel();
            int index = selModel.getMinSelectionIndex();
            if (index >= 0) {
                DefaultListModel<String> tempModel = dataMap.get(userList.getSelectedValue());
                tempModel.remove(index);
                dataMap.put(userList.getSelectedValue(), tempModel);
                if (index != 0) {
                    thingList.setSelectedIndex(index - 1);
                } else {
                    thingList.setSelectedIndex(0);
                }
                printDataMap(dataMap);
            }
        });
    }

    private void setOnDeleteUserButtonListener(JButton deleteUserButton) {
        deleteUserButton.addActionListener(e -> {
            var selModel = userList.getSelectionModel();
            int index = selModel.getMinSelectionIndex();
            if (index >= 0) {
                String key = userList.getSelectedValue();
                DefaultListModel<String> tempModel = dataMap.get(userList.getSelectedValue());
                dataMap.remove(key);
                userListModel.remove(index);
                if (index != 0) {
                    userList.setSelectedIndex(index - 1);
                } else {
                    userList.setSelectedIndex(0);
                    tempModel.removeAllElements();
                }
                printDataMap(dataMap);
            }
        });
    }

    private void setOnAddThingButtonListener(JButton addThingButton) {
        addThingButton.addActionListener(e -> {
            var text = JOptionPane.showInputDialog("Add a new thing");
            String item;

            if (text != null) {
                item = text.trim();
            } else {
                return;
            }

            if (!item.isEmpty()) {
                if (!dataMap.isEmpty()) {
                    DefaultListModel<String> tempModel = dataMap.get(userList.getSelectedValue());
                    tempModel.addElement(item);
                    dataMap.put(userList.getSelectedValue(), tempModel);
                    thingList.setModel(tempModel);
                    thingList.setSelectedIndex(tempModel.getSize() - 1);
                    printDataMap(dataMap);
                } else {
                    JLabel errorThingWithoutUser = new JLabel("Add some user first!");
                    errorThingWithoutUser.setHorizontalAlignment(SwingConstants.LEFT);
                    JOptionPane.showMessageDialog(null, errorThingWithoutUser);
                }
            }
        });
    }

    private void setOnAddUserButtonListener(JButton addUserButton) {
        addUserButton.addActionListener(e -> {
            var text = JOptionPane.showInputDialog("Add a new user");
            String item;
            int size = userListModel.getSize();

            if (text != null) {
                item = text.trim();
            } else {
                return;
            }

            if (!item.isEmpty()) {
                if (!(dataMap.containsKey(item))) {
                    dataMap.put(item, new DefaultListModel<>());
                    userListModel.addElement(item);
                    userList.setSelectedIndex(size);
                    printDataMap(dataMap);
                } else {
                    JLabel errorUser = new JLabel("Such user already exists!");
                    errorUser.setHorizontalAlignment(SwingConstants.LEFT);
                    JOptionPane.showMessageDialog(null, errorUser);
                }

            }
        });
    }

    private void setOnUserListSelectedListener() {
        userList.addListSelectionListener(e -> {
                    if (!e.getValueIsAdjusting()) {
                        if (userList.getSelectedIndex() == -1) {
                            return;
                        } else
                            thingList.setModel(dataMap.get(userList.getSelectedValue()));
                        thingList.setSelectedIndex(0);
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

    private DefaultListModel<String> createUserListModel() {
        return new DefaultListModel<>();
    }

    private JList<String> createUserList() {
        for (Map.Entry<String, DefaultListModel<String>> entry : dataMap.entrySet()) {
            userListModel.addElement(entry.getKey());
        }
        JList<String> userList = new JList<>(userListModel);
        userList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        userList.setLayoutOrientation(JList.VERTICAL);
        userList.setSelectedIndex(0);
        userList.setVisibleRowCount(-1);
        return userList;
    }

    private JScrollPane createScrollBarUserList(JList<String> jlist) {
        return new JScrollPane(jlist);
    }

    private JList<String> createThingList() {
        JList<String> thingList = new JList<>();
        thingList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        thingList.setLayoutOrientation(JList.VERTICAL);
        thingList.setVisibleRowCount(-1);
        return thingList;
    }

    private JScrollPane createScrollThingList(JList<String> jlist) {
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

    private JButton createAddUserButton() {
        return new JButton("Add user");
    }

    private JButton createAddThingButton() {
        return new JButton("Add new thing");
    }

    private JButton createDeleteUserButton() {
        return new JButton("Delete user");
    }

    private JButton createDeleteThingButton() {
        return new JButton("Delete thing");
    }

    private JButton createSaveEventButton() {
        return new JButton("SAVE EVENT");
    }
}