package Receptionist;
import common.DBLogger;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.control.TextField;

public class RegisterMembers {
    public GridPane layout;
    private Connection con;
    private RegisterMembersFunctions registerFunctions;
    private String username;

    public RegisterMembers(Connection con,String username){
        layout = new GridPane();
        this.con = con;
        this.username=username;
        this.registerFunctions=new RegisterMembersFunctions(con,username);
        addComponents();
    }

    private void addComponents() {
        layout.setVgap(10);
        layout.setHgap(10);

        Label nameLabel = new Label("Name:");
        TextField nameField = new TextField();

        Label contactLabel = new Label("Contact Email:");
        TextField contactField = new TextField();

        Label typeLabel = new Label("Membership Type:");
        ComboBox<String> typesItems = new ComboBox<>();
        typesItems.getItems().addAll("Yearly", "Monthly", "Quarterly");

        Label dateLabel = new Label("Registration Date:");
        TextField dateField = new TextField();
        dateField.setEditable(false);
        dateField.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        Button registerButton = new Button("Register Member");
        registerButton.setOnAction(e -> {
            try {
                registerMember(nameField.getText(), contactField.getText(),
                        typesItems.getValue(), dateField.getText());
            } catch (SQLException e1) {
                DBLogger.log("ERROR","RegisterMembers","Failed to register member(SQL excpetion).",username);
                e1.printStackTrace();
            }
        });
        layout.add(nameLabel, 0, 0);
        layout.add(nameField, 1, 0);
        layout.add(contactLabel, 0, 1);
        layout.add(contactField, 1, 1);
        layout.add(typeLabel, 0, 2);
        layout.add(typesItems, 1, 2);
        layout.add(dateLabel, 0, 3);
        layout.add(dateField, 1, 3);
        layout.add(registerButton, 0, 4);
    }

    private void registerMember(String name, String contact, String type, String regDate) throws SQLException {
        // Sanitize and validate inputs
        if (registerFunctions.validateInputs(name, contact, type)) {
            // Calculate expiry date based on membership type
            LocalDate registrationDate = LocalDate.parse(regDate);
            LocalDate expiryDate;
            if (type.equals("Yearly")) {
                expiryDate = registrationDate.plusYears(1);
            } else if (type.equals("Monthly")) {
                expiryDate = registrationDate.plusMonths(1);
            } else {
                expiryDate = registrationDate.plusMonths(3);
            }
            // Fetch memberID
            int memberID = registerFunctions.fetchMemberID();
            // Initialize billing interface
            Billing billing = new Billing(con,"reg",name,contact,regDate,expiryDate, memberID, -1, 0,username,type);
            billing.initInterface();
            DBLogger.log("INFO","RegisterMembers","Initiated Billing Interface.",username);
        } else {
            DBLogger.log("ERROR","RegisterMembers","Invalid Input entered at register member interface.",username);
            registerFunctions.showAlert("Invalid Input", "Please enter valid information.");
        }
    }
}

