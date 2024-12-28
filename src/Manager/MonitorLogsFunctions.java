package Manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;

public class MonitorLogsFunctions {
    private Connection con;
    private String username;

    public MonitorLogsFunctions(Connection con, String username) {
        this.con = con;
        this.username=username;
    }

    public void getMonthItems(ComboBox<String> monthCombo) {
        monthCombo.getItems().clear();
        String query = "SELECT DISTINCT MONTHNAME(event_date) AS month FROM logging";
        populateComboBox(monthCombo, query, new String[]{});
        monthCombo.getItems().add("Any");
    }

    public void getSeverityItems(ComboBox<String> severityCombo, String monthValue, String loggerValue) {
        if (CheckEmpty(new String[]{monthValue, loggerValue})) {
            showAlert("Form Error", "Please ensure all previous fields are selected before choosing this option.");
            return;
        }
        
        severityCombo.getItems().clear();
        String query = "SELECT DISTINCT level FROM logging WHERE 1=1 " +
            (!"Any".equals(monthValue) ? " AND MONTHNAME(event_date) = ? " : " AND MONTHNAME(event_date) IN (SELECT DISTINCT MONTHNAME(event_date) FROM logging)") +
            (!"Any".equals(loggerValue) ? " AND logger = ? " : " AND logger IN (SELECT DISTINCT logger FROM logging)");

        severityCombo.getItems().add("Any");
        populateComboBox(severityCombo, query, new String[]{monthValue, loggerValue});
    }

    public void getLoggerItems(ComboBox<String> loggerCombo, String monthValue) {
        if (CheckEmpty(new String[]{monthValue})) {
            showAlert("Form Error", "Please select the month before choosing a logger.");
            return;
        }

        loggerCombo.getItems().clear();
        String query = "SELECT DISTINCT logger FROM logging WHERE 1=1 " +
            (!"Any".equals(monthValue) ? " AND MONTHNAME(event_date) = ? " : " AND MONTHNAME(event_date) IN (SELECT DISTINCT MONTHNAME(event_date) FROM logging)");

        loggerCombo.getItems().add("Any");
        populateComboBox(loggerCombo, query, new String[]{monthValue});
    }

    public void getUserItems(ComboBox<String> usernameCombo, String monthValue, String loggerValue, String severityValue) {
        if (CheckEmpty(new String[]{monthValue, loggerValue, severityValue})) {
            showAlert("Form Error", "Please select all previous options before choosing a username.");
            return;
        }

        usernameCombo.getItems().clear();
        String query = "SELECT DISTINCT username FROM logging WHERE 1=1 " +
            (!"Any".equals(monthValue) ? " AND MONTHNAME(event_date) = ? " : " AND MONTHNAME(event_date) IN (SELECT DISTINCT MONTHNAME(event_date) FROM logging)") +
            (!"Any".equals(loggerValue) ? " AND logger = ? " : " AND logger IN (SELECT DISTINCT logger FROM logging)") +
            (!"Any".equals(severityValue) ? " AND level = ? " : " AND level IN (SELECT DISTINCT level FROM logging)");

        usernameCombo.getItems().add("Any");
        populateComboBox(usernameCombo, query, new String[]{monthValue, loggerValue, severityValue});
    }

    private boolean CheckEmpty(String[] setArray) {
        for (String value : setArray) {
            if (value == null) {
                return true;
            }
        }
        return false;
    }

    void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void populateComboBox(ComboBox<String> comboBox, String query, String[] params) {
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            int index = 1;
            for (String param : params) {
                if (param != null && !"Any".equals(param)) {
                    pstmt.setString(index++, param);
                }
            }
            ResultSet rs = pstmt.executeQuery();
            common.DBLogger.log("INFO","MgrInterface",username + " accessed Logging table for SELECT successfully!!",username);
            while (rs.next()) {
                comboBox.getItems().add(rs.getString(1));
            }
        } catch (SQLException e) {
            common.DBLogger.log("ERROR","MgrInterface","Database Connection Unsuccessful!!",username);
        }
    }
}
