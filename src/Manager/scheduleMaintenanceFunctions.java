package Manager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;

public class scheduleMaintenanceFunctions {
    private Connection con;
    private String username;

    public scheduleMaintenanceFunctions(Connection con, String username) {
        this.con = con;
        this.username = username;
    }

    public void facilityGetItems(ComboBox<String> facilityComboBox) {
        String query = "SELECT FacilityName FROM `gymfacilities`;";
        fillComboBox(facilityComboBox, query, "FacilityName");
    }

    public void areaGetItems(String selectedFacility, ComboBox<String> areaComboBox) {
        if (selectedFacility == null) {
            showAlert("Form Error", "Choose a Facility first");
            return;
        }

        String facilityId = getSingleItemFromDB("SELECT FacilityID FROM `gymfacilities` WHERE FacilityName = ?", selectedFacility);
        if (facilityId != null) {
            String query = "SELECT Description FROM `maintenance_descriptions` WHERE FacilityID = ?";
            fillComboBox(areaComboBox, query, "Description", facilityId);
        }
    }

    public void monthGetItems(String selectedFacility, String selectedArea, ComboBox<String> monthComboBox) {
        if (selectedFacility == null || selectedArea == null) {
            showAlert("Form Error", "All fields must be selected!");
            return;
        }

        String facilityId = getSingleItemFromDB("SELECT FacilityID FROM `gymfacilities` WHERE FacilityName = ?", selectedFacility);
        String descriptionId = getSingleItemFromDB("SELECT DescriptionID FROM maintenance_descriptions WHERE Description = ?", selectedArea);
        if (facilityId != null && descriptionId != null) {
            String query = "SELECT DISTINCT MONTHNAME(MaintenanceDate) AS MonthName FROM maintenance_records WHERE FacilityID=? AND DescriptionID=? ORDER BY MONTH(MaintenanceDate);";
            fillComboBox(monthComboBox, query, "MonthName", facilityId, descriptionId);
        }
    }

    private void fillComboBox(ComboBox<String> comboBox, String query, String columnLabel, String... params) {
        try (PreparedStatement statement = con.prepareStatement(query)) {
            for (int i = 0; i < params.length; i++) {
                statement.setString(i + 1, params[i]);
            }
            try (ResultSet rs = statement.executeQuery()) {
                common.DBLogger.log("INFO","MgrInterface",username+" accessed maintenance_records table",username);
                comboBox.getItems().clear();
                while (rs.next()) {
                    comboBox.getItems().add(rs.getString(columnLabel));
                }
            }
        } catch (Exception e) {
            showAlert("Database Error", "Failed to connect to the database.");
            common.DBLogger.log("ERROR","LoginInterface","Database Connection Unsuccessful!!",username);
        }
    }

    private String getSingleItemFromDB(String query, String parameter) {
        try (PreparedStatement statement = con.prepareStatement(query)) {
            statement.setString(1, parameter);
            try (ResultSet rs = statement.executeQuery()) {
                if(parameter.contains("facil")){
                    common.DBLogger.log("INFO","MgrInterface",username+" accessed gymfacilities table",username);
                }else{
                    common.DBLogger.log("INFO","MgrInterface",username+" accessed maintenance_descriptions table",username);
                }
                if (rs.next()) {
                    return rs.getString(1);
                }
            }
        } catch (Exception e) {
            showAlert("Database Error", "Failed to connect to the database.");
            common.DBLogger.log("ERROR","MgrInterface","Database Connection Unsuccessful!!",username);
        }
        return null;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public boolean checkFormEmpty(String selectedFacility, String selectedArea) {
        if (selectedFacility == null || selectedArea == null) {
            showAlert("Form Error", "All fields must be selected!");
            return true;
        }
        return false;
    }
    public void showConfrm(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    public boolean scheduleMaintenance(String facility, String area) {
        boolean success = false;
        try {
            String query = "SELECT MAX(maintenance_records.MaintenanceDate) AS LatestMaintenanceDate " +
                        "FROM maintenance_records INNER JOIN gymfacilities ON gymfacilities.FacilityID = maintenance_records.FacilityID " +
                        "INNER JOIN maintenance_descriptions ON maintenance_descriptions.DescriptionID = maintenance_records.DescriptionID " +
                        "WHERE Description = ? AND FacilityName = ? GROUP BY gymfacilities.FacilityID, maintenance_descriptions.DescriptionID;";
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, area);
            statement.setString(2, facility);
            ResultSet rs = statement.executeQuery();
            common.DBLogger.log("INFO","MgrInterface",username+" accessed maintenance_records table",username);

            
            if (rs.next()) {
                String date = rs.getString("LatestMaintenanceDate");
                LocalDate latestMaintenanceDate = LocalDate.parse(date);
                LocalDate currentDate = LocalDate.now();
                if (latestMaintenanceDate.isEqual(currentDate)) {
                    showAlert("Duplicate Maintenance", "You already have maintenance on this facility on this area scheduled today!!");
                } else {
                    String facilityId = getSingleItemFromDB("SELECT FacilityID FROM gymfacilities WHERE FacilityName=?", facility);
                    String descriptionId = getSingleItemFromDB("SELECT DescriptionID FROM maintenance_descriptions WHERE Description=?", area);

                    if (facilityId != null && descriptionId != null) {
                        query = "INSERT INTO `maintenance_records`(`FacilityID`, `DescriptionID`) VALUES (?,?);";
                        statement = con.prepareStatement(query);
                        statement.setString(1, facilityId);
                        statement.setString(2, descriptionId);
                        statement.executeUpdate();
                        showConfrm("Successful", "Maintenance scheduled successfully!!");
                        success = true;
                    }
                }
            } else {
                String facilityId = getSingleItemFromDB("SELECT FacilityID FROM gymfacilities WHERE FacilityName=?", facility);
                String descriptionId = getSingleItemFromDB("SELECT DescriptionID FROM maintenance_descriptions WHERE Description=?", area);

                if (facilityId != null && descriptionId != null) {
                    query = "INSERT INTO `maintenance_records`(`FacilityID`, `DescriptionID`) VALUES (?,?);";
                    statement = con.prepareStatement(query);
                    statement.setString(1, facilityId);
                    statement.setString(2, descriptionId);
                    statement.executeUpdate();
                    common.DBLogger.log("INFO","MgrInterface",username+" inserted a record to maintenance_records table",username);
                    success = true;
                }
            }
        } catch (Exception e) {
            showAlert("Database Error", "Failed to connect to the database.");
            common.DBLogger.log("ERROR","MgrInterface","Database Connection Unsuccessful!!",username);
        }
        return success;
    }
}