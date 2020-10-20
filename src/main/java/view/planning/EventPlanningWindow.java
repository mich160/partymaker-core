package view.planning;

import view.planning.components.JDateTimePicker;

import javax.swing.*;
import java.awt.*;
import java.text.DateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class EventPlanningWindow extends JFrame implements EventPlanning {
    private static final double FIRST_COLUMN_WEIGHT_X = 0.15;
    private static final double SECOND_COLUMN_WEIGHT_X = 0.15;
    private static final double THIRD_COLUMN_WEIGHT_X = 0.7;

    private static final double FIRST_ROW_WEIGHT_Y = 0.04;
    private static final double SECOND_ROW_WEIGHT_Y = 0.835;
    private static final double THIRD_ROW_WEIGHT_Y = 0.03;
    private static final double FOURTH_ROW_WEIGHT_Y = 0.03;
    private static final double FIFTH_ROW_WEIGHT_Y = 0.035;

    private final JTextField eventName;
    private final JDateTimePicker dateTimePicker;
    private final JScrollPane scrollBarUserList;
    private final DefaultListModel<String> userListModel;
    private final JList userList;
    private final JButton addUserButton;
    private final JButton deleteUserButton;
    private final JScrollPane scrollBarThingList;

    private final JList thingList;
    private final JButton addThingButton;
    private final JButton deleteThingButton;
    private final JButton eventSaveButton;

    private final JLabel summaryTitle;
    private final JTextArea summaryText;
    private final JScrollPane scrollBarSummaryText;

    private GridBagConstraints gridBagConstraints;

    private LinkedHashMap<String, DefaultListModel> dataMap;

    public EventPlanningWindow() throws HeadlessException {
        super("Baula Event Planning");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 605, 300);
        setResizable(false);
        setLayout(new GridBagLayout());

        dataMap = createDataMap();

        eventName = createEventNameTextField();
        add(eventName, gridBagConstraints);
        dateTimePicker = createJDateTimePicker();
        add(dateTimePicker, gridBagConstraints);
        summaryTitle = createSummaryTitleJLabel();
        add(summaryTitle, gridBagConstraints);

        userListModel = createUserListModel();
        userList = createUserList();
        scrollBarUserList = createScrollBarUserList(userList);
        add(scrollBarUserList, gridBagConstraints);

        thingList = createThingList();

        userList.addListSelectionListener(e ->{
                    if (!e.getValueIsAdjusting())
                    {
                        if(userList.getSelectedIndex() == -1){
                            return;
                        }
                        else
                        thingList.setModel(dataMap.get(userList.getSelectedValue()));
                        thingList.setSelectedIndex(0);
                    }
                }
        );

        scrollBarThingList = createScrollThingList(thingList);
        add(scrollBarThingList, gridBagConstraints);
        summaryText = createSummaryText();
        scrollBarSummaryText = createScrollBarSummaryText(summaryText);
        add(scrollBarSummaryText, gridBagConstraints);
        printDataMap(dataMap);

        addUserButton = createAddUserButton();
        add(addUserButton, gridBagConstraints);
        addUserButton.addActionListener(e ->{
            var text = JOptionPane.showInputDialog("Add a new user");
            String item;
            int size = userListModel.getSize();

            if (text != null) {
                item = text.trim();
            } else {
                return;
            }

            if (!item.isEmpty()) {
                if(!(dataMap.containsKey(item))){
                    dataMap.put(item, new DefaultListModel());
                    userListModel.addElement(item);
                    userList.setSelectedIndex(size);
                    printDataMap(dataMap);
                }
                else{
                    JLabel errorUser = new JLabel("Such user already exists!");
                    errorUser.setHorizontalAlignment(SwingConstants.LEFT);
                    JOptionPane.showMessageDialog(null, errorUser);
                }

            }
        });

        addThingButton = createAddThingButton();
        add(addThingButton, gridBagConstraints);
        addThingButton.addActionListener(e -> {
            var text = JOptionPane.showInputDialog("Add a new thing");
            String item;

            if (text != null) {
                item = text.trim();
            } else {
                return;
            }

            if (!item.isEmpty()) {
                if(!dataMap.isEmpty()){
                    DefaultListModel tempModel = dataMap.get(userList.getSelectedValue());
                    tempModel.addElement(item);
                    dataMap.put((String) userList.getSelectedValue(), tempModel);
                    thingList.setModel(tempModel);
                    thingList.setSelectedIndex(tempModel.getSize() - 1);
                    printDataMap(dataMap);
                }
                else{
                    JLabel errorThingWithoutUser = new JLabel("Add some user first!");
                    errorThingWithoutUser.setHorizontalAlignment(SwingConstants.LEFT);
                    JOptionPane.showMessageDialog(null, errorThingWithoutUser);
                }
            }
        });

        deleteUserButton = createDeleteUserButton();
        add(deleteUserButton, gridBagConstraints);
        deleteUserButton.addActionListener(e ->{
            var selModel = userList.getSelectionModel();
            int index = selModel.getMinSelectionIndex();
            if(index >= 0){
                String key = userList.getSelectedValue().toString();
                DefaultListModel tempModel = dataMap.get(userList.getSelectedValue());
                dataMap.remove(key);
                userListModel.remove(index);
                if(index != 0)
                {
                    userList.setSelectedIndex(index - 1); }
                else {
                    userList.setSelectedIndex(0);
                    tempModel.removeAllElements();
                }
                printDataMap(dataMap);
            }
        });

        deleteThingButton = createDeleteThingButton();
        add(deleteThingButton, gridBagConstraints);
        deleteThingButton.addActionListener(e -> {
            var selModel = thingList.getSelectionModel();
            int index = selModel.getMinSelectionIndex();
            if(index >= 0){
                DefaultListModel tempModel = dataMap.get(userList.getSelectedValue());
                tempModel.remove(index);
                dataMap.put((String) userList.getSelectedValue(), tempModel);
                if(index != 0)
                {
                    thingList.setSelectedIndex(index - 1); }
                else {
                    thingList.setSelectedIndex(0);
                }
                printDataMap(dataMap);
            }
        });

        eventSaveButton = createSaveEventButton();
        add(eventSaveButton, gridBagConstraints);
        eventSaveButton.addActionListener(e -> {
;        });
        setVisible(true);
    }

    private void printDataMap(LinkedHashMap<String, DefaultListModel> dataMap){
        StringBuilder tempStringBuilder = new StringBuilder();
        String stringDataMap;
        for(Map.Entry entry : dataMap.entrySet()){
            tempStringBuilder.append(entry.getKey());
            tempStringBuilder.append(":");
            tempStringBuilder.append("\n");
            tempStringBuilder.append(entry.getValue());
            tempStringBuilder.append("\n\n");
        }
        stringDataMap = tempStringBuilder.toString().replaceAll("[\\[\\]]","");
        summaryText.setText(stringDataMap);
        summaryText.setCaretPosition(0);
    }

    private LinkedHashMap<String, DefaultListModel> createDataMap(){
        dataMap = new LinkedHashMap<>();
        return dataMap;
    }

    private GridBagConstraints createGridBagConstraints(double weightx, double weighty, int gridx, int gridy ) {
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = weightx;
        gridBagConstraints.weighty = weighty;
        gridBagConstraints.gridx = gridx;
        gridBagConstraints.gridy = gridy;
        return gridBagConstraints;}

    private JTextField createEventNameTextField() {
        JTextField eventNameTextField = new JTextField("IMBRA :D");
        eventNameTextField.setHorizontalAlignment(JTextField.CENTER);
        gridBagConstraints = createGridBagConstraints(FIRST_COLUMN_WEIGHT_X, FIRST_ROW_WEIGHT_Y, 0, 0);
        return eventNameTextField;
    }

    private JDateTimePicker createJDateTimePicker(){
        Date date = new Date();
        JDateTimePicker dateTimePicker = new JDateTimePicker();
        gridBagConstraints = createGridBagConstraints(SECOND_COLUMN_WEIGHT_X, FIRST_ROW_WEIGHT_Y, 1, 0);
        dateTimePicker.setFormats( DateFormat.getDateTimeInstance( DateFormat.SHORT, DateFormat.MEDIUM ) );
        dateTimePicker.setTimeFormat( DateFormat.getTimeInstance( DateFormat.MEDIUM ) );
        dateTimePicker.setDate(date);
        return dateTimePicker;
    }

    private JLabel createSummaryTitleJLabel() {
        JLabel summaryTitleJLabel = new JLabel("Summary");
        summaryTitleJLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gridBagConstraints = createGridBagConstraints(THIRD_COLUMN_WEIGHT_X, FIRST_ROW_WEIGHT_Y, 2, 0);
        return summaryTitleJLabel;}

    private DefaultListModel createUserListModel()
    {
        DefaultListModel userListModel = new DefaultListModel();
        return userListModel;
    }

    private JList createUserList()
    {
        for(Map.Entry<String, DefaultListModel> entry: dataMap.entrySet())
        {
            userListModel.addElement(entry.getKey());
        }
        JList userList = new JList(userListModel);
        userList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        userList.setLayoutOrientation(JList.VERTICAL);
        userList.setSelectedIndex(0);
        userList.setVisibleRowCount(-1);
        return userList;
    }

    private JScrollPane createScrollBarUserList(JList jlist) {
        JScrollPane jScrollPane = new JScrollPane(jlist);
        gridBagConstraints = createGridBagConstraints(FIRST_COLUMN_WEIGHT_X, SECOND_ROW_WEIGHT_Y, 0, 1);
        return jScrollPane;
    }

    private JList createThingList() {
        JList thingList = new JList();
        thingList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        thingList.setLayoutOrientation(JList.VERTICAL);
        thingList.setVisibleRowCount(-1);
        return thingList; }

    private JScrollPane createScrollThingList(JList jlist) {
        JScrollPane jScrollPane = new JScrollPane(jlist);
        gridBagConstraints = createGridBagConstraints(SECOND_COLUMN_WEIGHT_X, SECOND_ROW_WEIGHT_Y, 1, 1);
        return jScrollPane;
    }

    private JTextArea createSummaryText() {
        JTextArea jTextArea = new JTextArea();
        jTextArea.setEditable(false);
        jTextArea.setLineWrap(true);
        return jTextArea;}

    private JScrollPane createScrollBarSummaryText(JTextArea jTextArea){
        JScrollPane jScrollPane = new JScrollPane(jTextArea);
        gridBagConstraints = createGridBagConstraints(THIRD_COLUMN_WEIGHT_X, 0, 2, 1);
        gridBagConstraints.gridheight = 3;
        return jScrollPane;
    }

    private JButton createAddUserButton() {
        JButton addUserButton = new JButton("Add user");
        gridBagConstraints = createGridBagConstraints(FIRST_COLUMN_WEIGHT_X, THIRD_ROW_WEIGHT_Y, 0, 2);
        return addUserButton;
    }

    private JButton createAddThingButton() {
        JButton addThingButton = new JButton("Add new thing");
        gridBagConstraints = createGridBagConstraints(SECOND_COLUMN_WEIGHT_X, THIRD_ROW_WEIGHT_Y, 1, 2);
        return addThingButton;
    }

    private JButton createDeleteUserButton() {
        JButton deleteUserButton = new JButton("Delete user");
        gridBagConstraints = createGridBagConstraints(FIRST_COLUMN_WEIGHT_X, FOURTH_ROW_WEIGHT_Y, 0, 3);
        return deleteUserButton;
    }

    private JButton createDeleteThingButton() {
        JButton deleteThingButton = new JButton( "Delete thing");
        gridBagConstraints = createGridBagConstraints(SECOND_COLUMN_WEIGHT_X, FOURTH_ROW_WEIGHT_Y, 1, 3);
        return deleteThingButton;
    }

    private JButton createSaveEventButton() {
        JButton eventSaveButton = new JButton("SAVE EVENT");
        gridBagConstraints = createGridBagConstraints(0, FIFTH_ROW_WEIGHT_Y, 0, 4);
        gridBagConstraints.gridwidth = 3;
        return eventSaveButton;
    }
}