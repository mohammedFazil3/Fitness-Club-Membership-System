package Manager;
import java.sql.*;
import javafx.event.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import common.DBLogger;

public class AddClass {
    private Connection con;
    private GridPane layout;
    private HBox buttonsLayout;
    private AddClassFunctions addClassFunctions;
    private String username;

    public AddClass(Connection con, GridPane maintenanceLayout,String username) {
        this.con = con;
        this.layout = maintenanceLayout;
        this.username=username;
        this.addClassFunctions = new AddClassFunctions(con,username);
    }

    public void initializeComponents() {
        // labels
        Label classNameLabel = new Label("Enter Class Name:");
        Label facilityLabel = new Label("Choose Facility");
        Label trainerLabel = new Label("Choose Trainer:");
        Label weekdayLabel = new Label("Choose Day");
        Label timeLabel = new Label("Choose Time");
        Label maxCapLabel = new Label("Enter Maximum Capacity:");

        // text fields
        TextField classNameTextfield = new TextField();
        TextField maxCapacityTextField = new TextField();

        // Choose weekday from ComboBox/Select Form
        ComboBox<String> weekdayComboBox = new ComboBox<>();
        weekdayComboBox.setPromptText("");
        weekdayComboBox.getItems().addAll("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday");

        // Choose time from ComboBox/Select Form
        ComboBox<String> timeComboBox = new ComboBox<>();
        timeComboBox.setPromptText("");
        timeComboBox.getItems().addAll("09:00:00", "10:00:00", "11:00:00", "12:00:00", "13:00:00", "14:00:00", "15:00:00", "16:00:00", "17:00:00", "18:00:00", "19:00:00", "20:00:00", "21:00:00", "22:00:00");

        // Buttons
        Button addButton = new Button("Add Class");
        Button exitButton = new Button("Exit");
        buttonsLayout = new HBox();
        buttonsLayout.setSpacing(10);
        buttonsLayout.getChildren().addAll(addButton, exitButton);

        // Add components to the layout
        layout.add(classNameLabel, 0, 0);
        layout.add(facilityLabel, 0, 1);
        layout.add(trainerLabel, 0, 2);
        layout.add(weekdayLabel, 0, 3);
        layout.add(timeLabel, 0, 4);
        layout.add(maxCapLabel, 0, 5);

        // Facility ComboBox (Populate from gymfacilities table)
        ComboBox<String> facilityComboBox = new ComboBox<>();
        facilityComboBox.setPromptText("");
        // Populate facilityComboBox from database
        addClassFunctions.populateFacilities(facilityComboBox);

        // Trainer ComboBox (Populate from trainers table)
        ComboBox<String> trainerComboBox = new ComboBox<>();
        trainerComboBox.setPromptText("");

        // Set event handler for facility selection change
        facilityComboBox.setOnAction(event -> {
            String selectedFacility = facilityComboBox.getValue();
            if (selectedFacility != null && !selectedFacility.isEmpty()) {
                int facilityID = addClassFunctions.getFacilityID(selectedFacility);
                // Clear existing items in trainerComboBox
                trainerComboBox.getItems().clear();
                // Populate trainers based on facility ID
                addClassFunctions.populateTrainers(trainerComboBox, facilityID);
            }
        });     

        layout.add(classNameTextfield, 1, 0);
        layout.add(facilityComboBox, 1, 1);
        layout.add(trainerComboBox, 1, 2);
        layout.add(weekdayComboBox, 1, 3);
        layout.add(timeComboBox, 1, 4);
        layout.add(maxCapacityTextField, 1, 5);
        layout.add(buttonsLayout, 0, 6, 2, 1); // spans the buttons across two columns

        // Event handlers
        addButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event)throws NumberFormatException {
                String className = classNameTextfield.getText();
                String trainerName = trainerComboBox.getValue();
                String facilityName = facilityComboBox.getValue();
                String weekday = weekdayComboBox.getValue();
                String timeStr = timeComboBox.getValue();
                int maxCapacity;
                // Convert time string to Time object
                Time time = Time.valueOf(timeStr);
                try {
                    maxCapacity = Integer.parseInt(maxCapacityTextField.getText());
                } catch (NumberFormatException e) {
                    DBLogger.log("ERROR","AddClass","Invalid Input at add class interface.",username);
                    addClassFunctions.showAlert("Input Error", "Enter valid credentials.");
                    return; // Stop further execution
                }

                if (!addClassFunctions.checkFormEmpty(className, trainerName, facilityName, weekday, timeStr, maxCapacity)) {
                    if(!addClassFunctions.checkExistingClass(weekday, time)){
                        try {
                            if (addClassFunctions.insertClass(className, weekday, time, trainerName, facilityName, maxCapacity)) {
                                layout.getChildren().clear();
                                ManageClass manageClass = new ManageClass(con,username);
                                manageClass.initializeComponents();
                                layout.add(manageClass.manageClassLayout, 0, 0);
                                addClassFunctions.showConfrm("Success", "Class added successfully!");
                                DBLogger.log("INFO","AddClass","New class added into classes table.",username);
                            }
                        } catch (SQLException e) {
                            DBLogger.log("ERROR","AddClass","Failed to add new class into classes table.",username);
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        exitButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                layout.getChildren().clear();
                ManageClass manageClass = new ManageClass(con,username);
                manageClass.initializeComponents();
                layout.add(manageClass.manageClassLayout, 0, 0);
                DBLogger.log("DEBUG","AddClass",username+" exited add class interface.",username);
            }
        });
    }
}
