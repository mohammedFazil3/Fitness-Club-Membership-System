package Trainer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import common.DBLogger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;

public class TrackProgress {
    private Connection con;
    private GridPane layout;
    private ComboBox<Integer> memberIdComboBox;
    private ComboBox<Integer> progressIdComboBox;
    private TextArea progressDetailTextArea;
    private TextArea progressMetricTextArea;
    private String username;

    public TrackProgress(Connection con, GridPane maintenanceLayout,String username) {
        this.con = con;
        this.username=username;
        this.layout = maintenanceLayout;
    }

    public void initializeComponents() {
        layout.getChildren().clear(); // Clear existing content

        // Member ID Dropdown
        Label memberIdLabel = new Label("Select Member ID:");
        memberIdComboBox = new ComboBox<>();
        memberIdComboBox.setPromptText("Select Member ID");
        populateMemberIds(); // Populate member IDs

        // Progress ID Dropdown
        Label progressIdLabel = new Label("Select Progress ID:");
        progressIdComboBox = new ComboBox<>();
        progressIdComboBox.setPromptText("Select Progress ID");
        memberIdComboBox.setOnAction(event -> populateProgressIds()); // Update progress IDs based on member ID selection

        // Progress Detail and Metric Text Areas
        Label progressDetailLabel = new Label("Progress Detail:");
        progressDetailTextArea = new TextArea();
        progressDetailTextArea.setPrefRowCount(3); // Set preferred row count
        progressDetailTextArea.setEditable(false);
        progressDetailTextArea.setPrefWidth(200);

        Label progressMetricLabel = new Label("Progress Metric:");
        progressMetricTextArea = new TextArea();
        progressMetricTextArea.setPrefRowCount(1); // Set preferred row count
        progressMetricTextArea.setEditable(false);
        progressMetricTextArea.setPrefWidth(200);

        // Search Button
        Button searchButton = new Button("Search");
        searchButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                searchProgressForMember();
                DBLogger.log("INFO","TrackProgress",username+" searched for an entry to track progress.",username);
            }
        });

        // Add components to the layout
        layout.add(memberIdLabel, 0, 0);
        layout.add(memberIdComboBox, 1, 0);
        layout.add(progressIdLabel, 0, 1);
        layout.add(progressIdComboBox, 1, 1);
        layout.add(progressDetailLabel, 0, 2);
        layout.add(progressDetailTextArea, 1, 2);
        layout.add(progressMetricLabel, 0, 3);
        layout.add(progressMetricTextArea, 1, 3);
        layout.add(searchButton, 0, 4);
    }

    private void populateMemberIds() {
        String query = "SELECT DISTINCT MemberID FROM progress_tracking WHERE TrainerID = ?";
        try (PreparedStatement statement = con.prepareStatement(query)) {
            // Set TrainerID (you need to replace 'trainerId' with the actual TrainerID)
            int trainerId = getTrainerID(username);
            statement.setInt(1, trainerId);            
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                memberIdComboBox.getItems().add(rs.getInt("MemberID"));
            }
            if(rs.next()){
                DBLogger.log("INFO","TrackProgress","memberID populated in memberID combobox.",username);
            }
        } catch (SQLException e) {
            DBLogger.log("ERROR","TrackProgress","Failed to populate memberID combobox.",username);
            e.printStackTrace();
        }
    }

    private int getTrainerID(String username) {
        int trainerID = -1;
        String query = "SELECT TrainerID FROM trainers WHERE username = ?";
        try (PreparedStatement statement = con.prepareStatement(query)) {
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                DBLogger.log("INFO","TrackProgress","Successfully fetched trainerID from trainers table.",username);
                trainerID = rs.getInt("TrainerID");
            }
        } catch (SQLException e) {
            DBLogger.log("ERROR","TrackProgress","Failed to fetch trainerID from trainers table.",username);
            e.printStackTrace();
        }
        return trainerID;
    }

    private void populateProgressIds() {
        progressIdComboBox.getItems().clear(); // Clear existing items

        if (memberIdComboBox.getValue() != null) {
            int memberId = memberIdComboBox.getValue();
            String query = "SELECT ProgressID FROM progress_tracking WHERE MemberID = ?";
            try (PreparedStatement statement = con.prepareStatement(query)) {
                statement.setInt(1, memberId);

                ResultSet rs = statement.executeQuery();
                while (rs.next()) {
                    progressIdComboBox.getItems().add(rs.getInt("ProgressID"));
                }
                if(rs.next()){
                    DBLogger.log("INFO","TrackProgress","progressID populated in progressID combobox.",username);
                }
            } catch (SQLException e) {
                DBLogger.log("ERROR","TrackProgress","Failed to populate progressID combobox.",username);
                e.printStackTrace();
            }
        }
    }

    private void searchProgressForMember() {
        if (progressIdComboBox.getValue() != null){
            int progressId = progressIdComboBox.getValue(); // Get selected Progress ID

            String query = "SELECT ProgressDetails, ProgressMetric FROM progress_tracking WHERE ProgressID = ?";
            try (PreparedStatement statement = con.prepareStatement(query)) {
                statement.setInt(1, progressId);

                ResultSet rs = statement.executeQuery();
                if (rs.next()) {
                    // Display progress details in text areas
                    progressDetailTextArea.setText(rs.getString("ProgressDetails"));
                    progressMetricTextArea.setText(String.valueOf(rs.getDouble("ProgressMetric")));
                } else {
                    progressDetailTextArea.setText("");
                    progressMetricTextArea.setText("");
                }
            } catch (SQLException e) {
                DBLogger.log("ERROR","TrackProgress","Failed to fetch details of progress from progress_tracking table.",username);
                e.printStackTrace();
            }
        }else{
            showAlert("Error","Please Fill in All the Fields.");
        }
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
