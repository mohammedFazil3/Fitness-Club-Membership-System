package Receptionist;
import common.DBLogger;

import java.sql.*;
import java.util.*;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class Plan {
    public GridPane layout; // Make layout public so it can be accessed
    private Connection con;
    private PlanFunctions planFunctions;
    public String trainer;
    private String username;

    Plan(Connection con,String username) {
        layout = new GridPane();
        this.con = con;
        this.username=username;
        this.planFunctions=new PlanFunctions(con,username);
        addComponents();
    }

    private void addComponents() {
        layout.setVgap(10);
        layout.setHgap(10);

        Label memberLabel = new Label("Enter Member ID:");
        int maxID = planFunctions.getMaxMemberID();
        Spinner<Integer> memberIdField = new Spinner<>();
        memberIdField.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, maxID));
        memberIdField.setEditable(true); // Allow manual input

        Label planLabel = new Label("Select Plan:");
        ComboBox<String> planComboBox = new ComboBox<>();
        planComboBox.setPromptText("");
        
        Label FacilityLabel = new Label("Select Facility:");
        ComboBox<String> facilityComboBox = new ComboBox<>();
        // Populate facilityComboBox with facility names from database
        planFunctions.populateFacilities(facilityComboBox);

        // Set event handler for facility selection change
        facilityComboBox.setOnAction(event -> {
            String selectedFacility = facilityComboBox.getValue();
            if (selectedFacility != null && !selectedFacility.isEmpty()) {
                int facilityID = planFunctions.getFacilityID(selectedFacility);
                // Clear existing items in trainerComboBox
                planComboBox.getItems().clear();
                // Populate trainers based on facility ID
                planFunctions.populateClasses(planComboBox,facilityID);
            }
        }); 

        CheckBox trainerNeededCheckbox = new CheckBox("Trainer needed?");
        Label trainerLabel = new Label("Assigned Trainer:");
        TextField trainerField = new TextField();

        trainerField.setEditable(false); // Read-only field
        planLabel.setDisable(true); // Initially disabled
        planComboBox.setDisable(true); // Initially disabled
        trainerLabel.setDisable(true); // Initially disabled
        trainerField.setDisable(true); // Initially disabled
        
        trainerNeededCheckbox.setOnAction(event -> {
            if (trainerNeededCheckbox.isSelected()) {
                planLabel.setDisable(false); 
                planComboBox.setDisable(false); // Enable the plan dropdown
                trainerLabel.setDisable(false); 
                trainerField.setDisable(false); // Enable the trainer dropdown
            } else {
                planLabel.setDisable(true); 
                planComboBox.setDisable(true); // Disable the plan dropdown
                planComboBox.getItems().clear();
                trainerLabel.setDisable(true); 
                trainerField.setDisable(true); // Disable the trainer dropdown
                trainerField.clear();
            }
        });

        planComboBox.setOnAction(event -> {
            String selectedPlan = planComboBox.getValue();
            if (selectedPlan != null && !selectedPlan.isEmpty()) {
                // Use the selectedPlan to fetch corresponding trainers from the database
                String query ="SELECT Name from trainers WHERE Specialization = ?";
                try{
                    PreparedStatement statement=con.prepareStatement(query);
                    statement.setString(1, selectedPlan);
                    ResultSet rs = statement.executeQuery();
                    if(rs.next()){
                        DBLogger.log("INFO","Plan","Name fetched from trainers table.",username);
                        String selectedTrainer = rs.getString("Name");
                        trainerField.clear();
                        // Set the fetched trainer in the trainerTextField
                        trainerField.setText(selectedTrainer);
                    }
                } catch(Exception e){
                    e.printStackTrace();
                    DBLogger.log("ERROR","Plan","Failed to fetch Name from trainers table.",username);
                }
            }            
        });
        
        Button assignButton = new Button("Assign Plan");
        assignButton.setOnAction(e -> {
            int memberId = memberIdField.getValue();
            trainer = trainerField.getText();
            try {
                planFunctions.assignPlan(memberId, planComboBox.getValue(), facilityComboBox.getValue(), trainer );
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        });
         
        layout.add(memberLabel, 0, 0);
        layout.add(memberIdField, 1, 0);
        layout.add(FacilityLabel, 0, 1);
        layout.add(facilityComboBox, 1, 1);
        layout.add(trainerNeededCheckbox, 0, 2);
        layout.add(planLabel, 0, 3);
        layout.add(planComboBox, 1, 3);
        layout.add(trainerLabel, 0, 4);
        layout.add(trainerField, 1, 4);
        layout.add(assignButton, 0, 5);
    }
    public void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}