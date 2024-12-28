package Trainer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import common.DBLogger;

public class ViewClass {
    public GridPane layout;
    private Connection con;
    private String username;

    public ViewClass(Connection con, String username) {
        this.con = con;
        this.username = username;
        layout = new GridPane();
        initializeComponents();
    }

    public void initializeComponents() {
        layout.setHgap(10);
        layout.setVgap(10);

        // Retrieve trainerID based on username from users table
        int trainerID = getTrainerID(username);
        if (trainerID != -1) {
            // Fetch class schedule from classes table based on trainerID
            fetchClassSchedule(trainerID);
        } else {
            layout.getChildren().add(new Label("You are not a Trainer!(No classes)"));
        }
    }

    private int getTrainerID(String username) {
        int trainerID = -1;
        String query = "SELECT TrainerID FROM trainers WHERE username = ?";
        try (PreparedStatement statement = con.prepareStatement(query)) {
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                DBLogger.log("INFO","ViewClass","Successfully fetched trainerID from trainers table.",username);
                trainerID = rs.getInt("TrainerID");
            }
        } catch (SQLException e) {
            DBLogger.log("ERROR","ViewClass","Failed to fetch trainerID from trainers table.",username);
            e.printStackTrace();
        }
        return trainerID;
    }

    private void fetchClassSchedule(int trainerID) {
        // Fetch class schedule from classes table based on trainerID
        String query = "SELECT Weekday, Schedule FROM classes WHERE TrainerID = ?";
        try (PreparedStatement statement = con.prepareStatement(query)) {
            statement.setInt(1, trainerID);
            ResultSet rs = statement.executeQuery();
            Text headerText = new Text("Class Schedule");
            headerText.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            layout.add(headerText, 0, 0);

            GridPane timetable = new GridPane();
            timetable.setHgap(10);
            timetable.setVgap(5);

            int row = 1;
            while (rs.next()) {
                String weekday = rs.getString("Weekday");
                String schedule = rs.getString("Schedule");

                // Create labels for weekday and schedule
                Label weekdayLabel = new Label(weekday);
                Label scheduleLabel = new Label(schedule);

                // Apply styles to labels
                weekdayLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
                scheduleLabel.setFont(Font.font("Arial", 14));

                // Add labels to the timetable grid
                timetable.add(weekdayLabel, 0, row);
                timetable.add(scheduleLabel, 1, row);

                row++;
            }
            if (rs.next()) {
                DBLogger.log("INFO","ViewClass","Successfully fetched class details from classes table.",username);
            }
            // Add the timetable to the main layout
            layout.add(timetable, 0, row);
        } catch (SQLException e) {
            DBLogger.log("ERROR","ViewClass","Failed to fetch class details from classes table.",username);
            e.printStackTrace();
        }
    }
}