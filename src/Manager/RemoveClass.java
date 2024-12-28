package Manager;
import common.DBLogger;
import java.sql.Connection;
import java.sql.SQLException;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class RemoveClass {
    private Connection con;
    private GridPane layout;
    private HBox buttonsLayout;
    private RemoveClassFunctions removeClassFunctions;
    private String username;

    public RemoveClass(Connection con, GridPane maintenanceLayout,String username) {
        this.con = con;
        this.layout = maintenanceLayout;
        this.username=username;
        this.removeClassFunctions = new RemoveClassFunctions(con,username);
    }

    public void initializeComponents() {
        // Label and TextField for class ID input
        Label classIDLabel = new Label("Enter Class ID:");
        TextField classIDTextField = new TextField();
        
        // Buttons
        Button searchButton = new Button("Search");
        Button deleteButton = new Button("Delete");
        Button exitButton = new Button("Exit");
        buttonsLayout = new HBox();
        buttonsLayout.setSpacing(10);
        buttonsLayout.getChildren().addAll(searchButton, deleteButton, exitButton);

        // Add components to the layout
        layout.add(classIDLabel, 0, 0);
        layout.add(classIDTextField, 1, 0);
        layout.add(buttonsLayout, 0, 1, 2, 1); // span across two columns

        // Event handlers
        searchButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                String classIDInput = classIDTextField.getText();
                if (!classIDInput.isEmpty()) {
                    try {
                        int classID = Integer.parseInt(classIDInput);
                        String[] classDetails = removeClassFunctions.getClassDetails(classID);
                        if (classDetails != null) {
                            displayClassDetails(classDetails);
                        } else {
                            showAlert("Error", "Class with ID " + classID + " not found.");
                        }
                    } catch (NumberFormatException e) {
                        DBLogger.log("ERROR","RemoveClass","Failed to search for a class(SQL excpetion).",username);
                        showAlert("Input Error", "Invalid class ID format.");
                    } catch (SQLException e) {
                        DBLogger.log("ERROR","RemoveClass","Failed to search for a class(SQL excpetion).",username);
                        showAlert("Database Error", "Failed to retrieve class details.");
                    }
                } else {
                    showAlert("Input Error", "Please enter a class ID.");
                }
                DBLogger.log("INFO","RemoveClass",username+" Searched for a class to remove.",username);
            }
        });

        deleteButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                String classIDInput = classIDTextField.getText();
                if (!classIDInput.isEmpty()) {
                    try {
                        int classID = Integer.parseInt(classIDInput);
                        if (removeClassFunctions.deleteClass(classID)) {
                            layout.getChildren().clear();
                            ManageClass manageClass = new ManageClass(con,username);
                            manageClass.initializeComponents();
                            layout.add(manageClass.manageClassLayout, 0, 0);
                            removeClassFunctions.showConfrm("Success", "Class with ID " + classID + " removed successfully!");
                        } else {
                            showAlert("Error", "Failed to remove class.");
                        }
                    } catch (NumberFormatException e) {
                        DBLogger.log("ERROR","RemoveClass","Failed to delete a class(SQL excpetion).",username);
                        showAlert("Input Error", "Invalid class ID format.");
                    } catch (SQLException e) {
                        DBLogger.log("ERROR","RemoveClass","Failed to delete a class(SQL excpetion).",username);
                        showAlert("Database Error", "Failed to remove class.");
                    }
                } else {
                    showAlert("Input Error", "Please enter a class ID.");
                }
                DBLogger.log("INFO","RemoveClass",username+" Deleted a class.",username); 
            }
        });

        exitButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                layout.getChildren().clear();
                ManageClass manageClass = new ManageClass(con,username);
                manageClass.initializeComponents();
                layout.add(manageClass.manageClassLayout, 0, 0);
                DBLogger.log("DEBUG","RemoveClass",username+" exited the remove class interface.",username);
            }
        });
    }

    private void displayClassDetails(String[] classDetails) {
        layout.getChildren().clear();
        Label classDetailsLabel = new Label("Class Details:");
        layout.add(classDetailsLabel, 0, 0);

        for (int i = 0; i < classDetails.length; i++) {
            Label detailLabel = new Label(classDetails[i]);
            layout.add(detailLabel, 0, i + 1);
        }

        layout.add(buttonsLayout, 0, classDetails.length + 1, 2, 1);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
