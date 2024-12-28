package Manager;
import java.sql.*;
import common.DBLogger;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;

public class AddClassFunctions {
    private Connection con;
    private String username;

    public AddClassFunctions(Connection con,String username) {
        this.con = con;
        this.username=username;
    }       

    public void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void showConfrm(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
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
                DBLogger.log("INFO","AddClassFunctions","FacilityName populated in facility combobox.",username);
            }
        } catch (SQLException e) {
            DBLogger.log("ERROR","AddClassFunctions","Failed to populate facility combobox.",username);
            e.printStackTrace();
        }
    }
    public void populateTrainers(ComboBox<String> trainerComboBox,int facID) {
        String query = "SELECT Name FROM trainers WHERE FacilityID=?";
        try (PreparedStatement statement = con.prepareStatement(query);){
            statement.setInt(1, facID);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String trainerName = rs.getString("Name");
                trainerComboBox.getItems().add(trainerName);
            }
            if(rs.next()){
                DBLogger.log("INFO","AddClassFunctions","trainer Name populated in trainers combobox.",username);
            }
        } catch (SQLException e) {
            DBLogger.log("ERROR","AddClassFunctions","Failed to populate trainers combobox.",username);
            e.printStackTrace();
        }
    }

    public boolean checkFormEmpty(String className, String trainerName, String facilityName,String weekday, String timeStr, int maxCapacity) {
        if (className == null || className.isEmpty()) {
            showAlert("Form Error", "Enter class name!!");
            return true;
        }
        if (trainerName == null || trainerName.isEmpty()) {
            showAlert("Form Error", "Enter trainer name!!");
            return true;
        }
        if (facilityName == null || facilityName.isEmpty()) {
            showAlert("Form Error", "Select facility!!");
            return true;
        }
        if (weekday == null || weekday.isEmpty()) {
            showAlert("Form Error", "Choose weekday!!");
            return true;
        }
        if (timeStr == null || timeStr.isEmpty()) {
            showAlert("Form Error", "Choose time!!");
            return true;
        }
        if (maxCapacity <= 0) {
            showAlert("Form Error", "Enter valid maximum capacity!!");
            return true;
        }
        return false;
    }

    public boolean checkExistingClass(String weekday, Time time) {
        String query = "SELECT * FROM classes WHERE Weekday = ? AND Schedule = ?";
        try (PreparedStatement statement = con.prepareStatement(query)) {
            statement.setString(1, weekday);
            statement.setTime(2, time);
            ResultSet rs = statement.executeQuery();
            if(rs.next()){
                showAlert("Input Error", "A class aldready exists at the specified slot");
                return true;// If there's a result, means class exists
            }else{
                return false;//No existing class
            } 
        } catch (SQLException e) {
            DBLogger.log("ERROR","AddClassFunctions","Failed to check existing class.",username);
            showAlert("Database Error", "Failed to check existing class.");
            e.printStackTrace();
            return true; // Consider it existing to avoid insertion
        }
    }
    

    public boolean insertClass(String className, String weekday, Time time, String trainerName, String facilityName, int maxCapacity) throws SQLException {
        // Get the trainerID from trainer Name
        int trainerID=getTrainerID(trainerName);
        
        int facilityID = getFacilityID(facilityName);
        if (facilityID != -1) {
            // Perform the class insertion
            String query = "INSERT INTO `classes` (`ClassName`, `Weekday`, `Schedule`, `TrainerID`, `MaximumCapacity`, `CurrentEnrollment`, `FacilityID`) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement statement = con.prepareStatement(query)) {
                statement.setString(1, className);
                statement.setString(2, weekday);
                statement.setTime(3, time);
                statement.setInt(4, trainerID);
                statement.setInt(5, maxCapacity);
                statement.setInt(6, 0); // Initial enrollment set to 0
                statement.setInt(7, facilityID);
                
                int rowsAffected = statement.executeUpdate();
                if (rowsAffected > 0) {
                    showConfrm("Success", "Class scheduled successfully!");
                    return true;
                }
                else{                           
                    DBLogger.log("ERROR","AddClassFunctions","Failed to add new class into classes table.",username);
                    showAlert("Database Error", "Failed to insert class.");
                    return false;
                }
            } catch (SQLException e) {
                DBLogger.log("ERROR","AddClassFunctions","Failed to add new class into classes table.",username);
                showAlert("Database Error", "Failed to insert class.");
                e.printStackTrace();
                return false;
            }
        } else {
            DBLogger.log("ERROR","AddClassFunctions","Failed to add new class into classes table.",username);
            showAlert("Facility Error", "Selected facility not found!");
            return false;
        }
    }
    
    public int getTrainerID(String trainerName) {
        String query = "SELECT TrainerID FROM trainers WHERE Name = ?";
        try (PreparedStatement statement = con.prepareStatement(query)){
            statement.setString(1, trainerName);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                DBLogger.log("INFO","AddClassFunctions","Fetched trainerID successfully from the trainers table.",username);
                return rs.getInt("TrainerID");
            }
        } catch (SQLException e) {
            DBLogger.log("ERROR","AddClassFunctions","Failed to fetch trainerID from trainers table.",username);
            e.printStackTrace();
        }
        return -1; // Error or no trainers found
    }
    

    public int getFacilityID(String facilityName) {
        String query = "SELECT FacilityID FROM gymfacilities WHERE FacilityName = ?";
        try (PreparedStatement statement = con.prepareStatement(query)) {
            statement.setString(1, facilityName);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                DBLogger.log("INFO","AddClassFunctions","Fetched facilityID successfully from the gymfacilities table.",username);
                return rs.getInt("FacilityID");
            }
        } catch (SQLException e) {
            DBLogger.log("ERROR","AddClassFunctions","Failed to fetch facilityID from gymfacilities table.",username);
            e.printStackTrace();
        }
        return -1; // Facility not found
    }
}    
