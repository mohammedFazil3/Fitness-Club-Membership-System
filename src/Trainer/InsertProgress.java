package Trainer;
import java.sql.*;
import javafx.event.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import common.DBLogger;

public class InsertProgress {
    private Connection con;
    private GridPane layout;
    private HBox buttonsLayout;
    private String username;

    public InsertProgress(Connection con, GridPane manageProgressLayout,String username) {
        this.con = con;
        this.username = username;
        this.layout = manageProgressLayout;
    }

    public void initializeComponents() {
        Label memberIdLabel = new Label("Select Member ID:");
        ComboBox<Integer> memberIdComboBox = new ComboBox<>();
        memberIdComboBox.setPromptText("");

        // Populate memberIdComboBox with member IDs assigned to the trainer
        populateMemberIds(memberIdComboBox);

        Label progressDetailLabel = new Label("Progress Details:");
        TextArea progressDetailTextArea = new TextArea();
        progressDetailTextArea.setWrapText(true);
        progressDetailTextArea.setPrefWidth(200);

        Label progressMetricLabel = new Label("Progress Metric (1-100):");
        TextField progressMetricField = new TextField();
        progressMetricField.setPrefWidth(200);

        Button submitButton = new Button("Submit");
        Button backButton = new Button("Back");
        buttonsLayout = new HBox();
        buttonsLayout.setSpacing(10);
        buttonsLayout.getChildren().addAll(submitButton, backButton);

        // Add components to the layout
        layout.add(memberIdLabel, 0, 0);
        layout.add(memberIdComboBox, 1, 0);
        layout.add(progressDetailLabel, 0, 1);
        layout.add(progressDetailTextArea, 1, 1);
        layout.add(progressMetricLabel, 0, 2);
        layout.add(progressMetricField, 1, 2);
        layout.add(buttonsLayout, 0, 3, 2, 1);

        // Event handler for submit button
        submitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String progressDetail = progressDetailTextArea.getText();
                String progressMetricStr = progressMetricField.getText();

                if (memberIdComboBox.getValue() == null || progressDetail.isEmpty() || progressMetricStr.isEmpty()) {
                    showAlert("Empty Fields", "Please fill in all fields.");
                    return;
                }

                try {
                    int memberId = memberIdComboBox.getValue();
                    double progressMetric = Double.parseDouble(progressMetricStr);
                    
                    if (progressMetric < 1 || progressMetric > 100) {
                        showAlert("Invalid Progress Metric", "Progress Metric must be between 1 and 100.");
                        return;
                    }

                    insertProgress(memberId, progressDetail, progressMetric);
                } catch (NumberFormatException e) {
                    DBLogger.log("ERROR","InsertProgress","Invalid inputs at insert progress interface.",username);
                    showAlert("Invalid Input", "Please enter a valid numeric value for Progress Metric.");
                }
                DBLogger.log("INFO","InsertProgress",username+" submitted an entry to insert into progress_tracking table.",username);
            }
        });

        // Event handler for back button
        backButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // Go back to the main progress management interface
                layout.getChildren().clear();
                ManageProgress manageProgress = new ManageProgress(con,username);
                manageProgress.initializeComponents();
                layout.add(manageProgress.manageProgressLayout, 0, 0);
                DBLogger.log("DEBUG","InsertProgress",username+" exited insert progress interface.",username);
            }
        });
    }

    private void populateMemberIds(ComboBox<Integer> memberIdComboBox) {
        String query = "SELECT DISTINCT MemberID FROM progress_tracking WHERE TrainerID = ?";
        try (PreparedStatement statement = con.prepareStatement(query)) {
            // Set the trainer ID dynamically
            int trainerId = getTrainerIdFromUsername(username); // Assuming you have a method to get the trainer ID from the username
            statement.setInt(1, trainerId);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                int memberId = rs.getInt("MemberID");
                memberIdComboBox.getItems().add(memberId);
            }
            if(rs.next()){
                DBLogger.log("INFO","InsertProgress","memberID populated in memberID combobox.",username);
            }
        } catch (SQLException e) {
            DBLogger.log("ERROR","InsertProgress","Failed to populate memberID combobox.",username);
            e.printStackTrace();
        }
    }

    private void insertProgress(int memberId, String progressDetail, double progressMetric) {
        String insertQuery = "INSERT INTO progress_tracking (MemberID, TrainerID, ClassID, ProgressDate, ProgressDetails, ProgressMetric) VALUES (?, ?, ?, CURDATE(), ?, ?)";
        try (PreparedStatement statement = con.prepareStatement(insertQuery)) {
            // Assuming TrainerID and ClassID are already known
            int trainerId = getTrainerIdFromUsername(username); // Assuming you have a method to get the trainer ID from the username
            int classId = getClassIdForTrainer(trainerId); // Assuming you have a method to get the ClassID for the trainer

            if (trainerId != -1) {
                statement.setInt(1, memberId);
                statement.setInt(2, trainerId);
                statement.setInt(3, classId);
                statement.setString(4, progressDetail);
                statement.setDouble(5, progressMetric);

                statement.executeUpdate();
                DBLogger.log("INFO","InsertProgress","Progress inserted into progress_tracking table.",username);
                showAlert("Success", "Progress inserted successfully.");
                layout.getChildren().clear();
                ManageProgress manageProgress = new ManageProgress(con,username);
                manageProgress.initializeComponents();
                layout.add(manageProgress.manageProgressLayout, 0, 0);
                DBLogger.log("DEBUG","InsertProgress",username+" exited insert progress interface.",username);
            } else {
                showAlert("Error", "You are not a Trainer!");
            }
        } catch (SQLException e) {
            DBLogger.log("ERROR","InsertProgress","Failed to insert progress into progress_tracking table.",username);
            e.printStackTrace();
            showAlert("Error", "Failed to insert progress. Please try again.");
        }
    }

    private int getTrainerIdFromUsername(String username) {
        int trainerID = -1;
        String query = "SELECT TrainerID FROM trainers WHERE username = ?";
        try (PreparedStatement statement = con.prepareStatement(query)) {
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                DBLogger.log("INFO","InsertProgress","fetched trainerID from trainers table.",username);
                trainerID = rs.getInt("TrainerID");
            }
        } catch (SQLException e) {
            DBLogger.log("ERROR","InsertProgress","Failed to fetch trainerID from trainers table.",username);
            e.printStackTrace();
        }
        return trainerID;
    }

    private int getClassIdForTrainer(int trainerId) {
        int classId = -1; // Default value if ClassID is not found
        try {
            String query = "SELECT ClassID FROM classes WHERE TrainerID = ?";
            PreparedStatement statement = con.prepareStatement(query);
            statement.setInt(1, trainerId);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                DBLogger.log("INFO","InsertProgress","fetched classID from classes table.",username);
                classId = rs.getInt("ClassID");
            }

            rs.close();
            statement.close();
        } catch (SQLException e) {
            DBLogger.log("ERROR","InsertProgress","Failed to fetch classID from classes table.",username);
            e.printStackTrace();
        }

        return classId;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
