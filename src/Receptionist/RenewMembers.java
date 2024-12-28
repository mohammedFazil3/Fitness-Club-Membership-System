package Receptionist;
import common.DBLogger;

import java.sql.*;
import java.time.LocalDate;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class RenewMembers {
    public GridPane layout;
    private Connection con;
    public LocalDate expiryDate;
    private PlanFunctions planFunctions;
    private String username;

    RenewMembers(Connection con,String username){
        layout = new GridPane();
        this.con = con;
        this.username=username;
        this.planFunctions=new PlanFunctions(con,username);
        addComponents();
    }

    private void addComponents(){
        layout.setVgap(10); // Vertical gap between rows
        layout.setHgap(10); // Horizontal gap between columns

        Label memberIdLabel = new Label("Member ID:");
        int maxID=planFunctions.getMaxMemberID();
        Spinner<Integer> memberIdField = new Spinner<>();
        memberIdField.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, maxID));
        memberIdField.setEditable(true); //Allow manual input

        Label typeLabel = new Label("Membership Type:");
        ComboBox<String> typeComboBox = new ComboBox<>();
        typeComboBox.getItems().addAll("Yearly", "Monthly", "Quarterly");

        Button renewButton = new Button("Renew");
        renewButton.setOnAction(e -> {
            try {
                renewMembership(memberIdField.getValue(), typeComboBox.getValue());
            } catch (SQLException e1) {
                DBLogger.log("ERROR","RenewMembers","failed to renew member(SQL exception).",username);
                e1.printStackTrace();
            }
        });

        // Add rows to the layout with gaps
        layout.add(memberIdLabel,0,0);
        layout.add(memberIdField,1,0);
        layout.add(typeLabel,0,1);
        layout.add(typeComboBox,1,1);
        layout.add(renewButton,0,2);
    }

    private void renewMembership(int memberId, String type) throws SQLException {
        if (validateInputs(memberId,type)) {
            // Calculate expiry date based on current date and membership type
            LocalDate currentDate = LocalDate.now();
            switch (type) {
                case "Yearly":
                    expiryDate = currentDate.plusYears(1);
                    break;
                case "Monthly":
                    expiryDate = currentDate.plusMonths(1);
                    break;
                case "Quarterly":
                    expiryDate = currentDate.plusMonths(3);
                    break;
                default:
                    showAlert("Invalid Membership Type", "Please select a valid membership type.");
                    return;
            }

            Billing billing = new Billing(con,"ren","","","",expiryDate,memberId,-1,0,username,type);
            billing.initInterface();    
            DBLogger.log("INFO","RenewMembers","Initiated Billing Interface.",username);        
        }else{
            DBLogger.log("ERROR","RenewMembers","Invalid Input entered at renew member interface.",username);
            showAlert("Invalid Input", "Please enter valid information.");
        }
    }

    private boolean validateInputs(int memberId, String membershipType) {
        if (membershipType != null && !membershipType.isEmpty()) {
            try {
                // Check if the member ID exists in the database and matches the provided membership type
                String query = "SELECT MemberID FROM members WHERE MemberID = ?";
                PreparedStatement statement = con.prepareStatement(query);
                statement.setInt(1, memberId);
                ResultSet rs = statement.executeQuery();
                
                // If a row is returned, the member ID and membership type combination is valid
                if(rs.next()){
                    DBLogger.log("INFO","RenewMembers","Validated renew interface inputs",username);
                    return true;
                }else{
                    return false;
                }
            } catch (SQLException e) {
                DBLogger.log("ERROR","RenewMembers","Failed to fetch memberID from members table.",username);
                e.printStackTrace();
                return false; // Error occurred, consider the ID as invalid
            }
        } else {
            return false; // Member ID or membership type is empty
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
