package Receptionist;
import common.DBLogger;
import javafx.scene.control.ComboBox;
import java.sql.*;

public class PlanFunctions {
    private BillingFunctions billingFunctions;
    private RegisterMembersFunctions registerFunctions;
    private Connection con;
    private String username;

    PlanFunctions(Connection con,String username){
        this.con=con;
        this.billingFunctions=new BillingFunctions(con,username);
        this.registerFunctions=new RegisterMembersFunctions(con, username);
        this.username=username;
    }

    public void assignPlan(int memberId, String plan, String facility, String trainer) throws SQLException{
        // Validate MemberID
        if (validateMemberID(memberId)&&facility!=null) {
            String selectedFacility = facility;
            int classId;
            if(plan==null){
                classId = 0;
            }else{
                classId = getClassID(plan);
            }
            
            int facilityId = getFacilityID(selectedFacility);
        
            // Check for existing entry
            if (checkExistingEntry(memberId, classId, facilityId)) {
                // Display error message for duplicate entry
                billingFunctions.showAlert("Duplicate Entry", "This member is already assigned to the selected plan or facility.");
            } else {
                // Proceed with assignment
                try {
                    // Member ID is valid, proceed with assigning the plan
                    // Insert into member_plan table
                    int facilityID = getFacilityID(facility);
                    String memberType = registerFunctions.getMembershipType(memberId);
                    if (facilityID !=- 1) {
                        // Execute the insertion query
                        Billing billing = new Billing(con,"plan", trainer, trainer, trainer, null, memberId,classId,facilityID,username,memberType);
                        billing.initInterface();
                        DBLogger.log("INFO","PlanFunctions","Initiated Billing Interface.",username);
                    } else {
                        billingFunctions.showAlert("Not Found", "The selected Input does not exist.");
                    }
                } catch (SQLException e1) {
                    e1.printStackTrace();
                    DBLogger.log("ERROR","PlanFunctions","Failed to initiate Billing Interface.",username);
                }
            }
            
        } else {
            // Member ID is invalid, show an error message or take appropriate action
            DBLogger.log("ERROR","PlanFunctions","Invalid Input entered at assign plan interface.",username);
            billingFunctions.showAlert("Invalid Input", "The entered Input is Invalid.");
        }
    }

    public boolean checkExistingEntry(int memberId, int classId, int facilityId) {
        try {
            String query = "SELECT * FROM member_plan WHERE member_id = ? AND class_id = ? AND facilityID = ?";
            PreparedStatement statement = con.prepareStatement(query);
            statement.setInt(1, memberId);
            statement.setInt(2, classId);
            statement.setInt(3, facilityId);
            ResultSet rs = statement.executeQuery();
            if(rs.next()){
                DBLogger.log("INFO","PlanFunctions","The plan assignment aldready exists.",username);
                return true;// If a row is returned, it means there is an existing entry
            }else{
                DBLogger.log("INFO","PlanFunctions","The plan assignment does not exist.",username);
                return false;
            }           
        } catch (SQLException e) {
            e.printStackTrace();
            DBLogger.log("ERROR","PlanFunctions","Failed to check for existing entries in member_plan table.",username);
            return true; // Consider as existing entry to avoid assignment
        }
    }

    public boolean validateMemberID(int memberId) {
        try {
            String query = "SELECT MemberID FROM members WHERE MemberID = ?";
            PreparedStatement statement = con.prepareStatement(query);
            statement.setInt(1, memberId);
            ResultSet rs = statement.executeQuery();
            
            // If a row is returned, the member ID is valid
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            DBLogger.log("ERROR","PlanFunctions","Failed to validate memberID from members table.",username);
            return false; // Error occurred, consider the ID as invalid
        }
    }

    public void populateFacilities(ComboBox<String> facilityComboBox) {
        String query = "SELECT FacilityName FROM gymfacilities WHERE Complimentary='NO'";
        try (PreparedStatement statement = con.prepareStatement(query);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                String facilityName = rs.getString("FacilityName");
                facilityComboBox.getItems().add(facilityName);
            }
            if(rs.next()){
                DBLogger.log("INFO","PlanFunctions","FacilityName populated in facility combobox.",username);
            }
        } catch (SQLException e) {
            DBLogger.log("ERROR","PlanFunctions","Failed to populate facility combobox.",username);
            e.printStackTrace();
        }
    }

    public void populateClasses(ComboBox<String> planComboBox,int facID){
        try {
            String query = "SELECT ClassName FROM classes WHERE facilityID=?";
            PreparedStatement statement = con.prepareStatement(query);
            statement.setInt(1, facID);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                String className = rs.getString("ClassName");
                planComboBox.getItems().add(className);
            }
            if(rs.next()){
                DBLogger.log("INFO","PlanFunctions","ClassName populated in classes combobox.",username);
            }

            // Close the ResultSet and PreparedStatement
            rs.close();
            statement.close();
        } catch (SQLException e) {
            DBLogger.log("ERROR","PlanFunctions","Failed to populate classes combobox.",username);
            e.printStackTrace();
        }
    }

    // Get the class ID based on the class name
    public int getClassID(String className) {
        try {
            String query = "SELECT ClassID FROM classes WHERE ClassName = ?";
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, className);
            ResultSet rs = statement.executeQuery();
            
            // If a row is returned, return the class ID
            if (rs.next()) {
                DBLogger.log("INFO","PlanFunctions","ClassID fetched from classes table.",username);
                return rs.getInt("ClassID");
            } else {
                return -1; // Class not found
            }
        } catch (SQLException e) {
            DBLogger.log("ERROR","PlanFunctions","Failed to fetch classID from classes table.",username);
            e.printStackTrace();
            return -1; // Error occurred
        }
    }

    public int getFacilityID(String facilityName) {
        try {
            String query = "SELECT FacilityID FROM gymfacilities WHERE FacilityName = ?";
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, facilityName);
            ResultSet rs = statement.executeQuery();
            
            // If a row is returned, return the Facility ID
            if (rs.next()) {
                DBLogger.log("INFO","PlanFunctions","FacilityID fecthed from gymfacilites table.",username);
                return rs.getInt("FacilityID");
            } else {
                return -1; // Facility not found
            }
        } catch (SQLException e) {
            DBLogger.log("ERROR","PlanFunctions","Failed to fetch FacilityID from gymfacilites table.",username);
            e.printStackTrace();
            return -1; // Error occurred
        }
    }

    public int getMaxMemberID() {
        int maxMemberID = 0;

        try {
            String query = "SELECT MAX(MemberID) AS MaxID FROM members";
            PreparedStatement statement = con.prepareStatement(query);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                DBLogger.log("INFO","PlanFunctions","Maximum memberID fecthed from members table.",username);
                maxMemberID = rs.getInt("MaxID");
            }

            rs.close();
            statement.close();
        } catch (SQLException e) {
            DBLogger.log("ERROR","PlanFunctions","Failed to fetch Maximum memberID from members table.",username);
            e.printStackTrace();
        }

        return maxMemberID;
    }
}