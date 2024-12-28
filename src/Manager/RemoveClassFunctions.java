package Manager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import common.DBLogger;

import javafx.scene.control.Alert;

public class RemoveClassFunctions {
    private Connection con;
    private String username;

    public RemoveClassFunctions(Connection con,String username) {
        this.username=username;
        this.con = con;
    }

    public String[] getClassDetails(int classID) throws SQLException {
        String[] classDetails = null;
        String query = "SELECT * FROM classes WHERE ClassID = ?";
        try (PreparedStatement statement = con.prepareStatement(query)) {
            statement.setInt(1, classID);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                classDetails = new String[7]; // Assuming 7 columns in the classes table
                classDetails[0] = "Class ID: " + rs.getInt("ClassID");
                classDetails[1] = "Class Name: " + rs.getString("ClassName");
                classDetails[2] = "Weekday: " + rs.getString("Weekday");
                classDetails[3] = "Schedule: " + rs.getTime("Schedule");
                classDetails[4] = "Trainer ID: " + rs.getInt("TrainerID");
                classDetails[5] = "Maximum Capacity: " + rs.getInt("MaximumCapacity");
                classDetails[6] = "Facility ID: " + rs.getInt("FacilityID");
            }
        }
        DBLogger.log("INFO","RemoveClassFunctions","Successfully fetched class details from classes table.",username);
        return classDetails;
    }

    public boolean deleteClass(int classID) throws SQLException {
        String query = "DELETE FROM classes WHERE ClassID = ?";
        try (PreparedStatement statement = con.prepareStatement(query)) {
            statement.setInt(1, classID);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        }
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
}
