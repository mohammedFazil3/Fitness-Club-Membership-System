package common;
import Manager.MgrInterface;
import Receptionist.RecInterface;
import Trainer.traInterface;

import java.sql.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.text.*;

public class LoginInterface {
    private Scene loginScene;
    private TextField usernameField = new TextField();
    private PasswordField passwordField = new PasswordField();
    private Stage stage;

    public LoginInterface(Stage primaryStage) {
        this.stage = primaryStage;
    }

    public void initializeComponents() {
        //Defining the login layout:
        GridPane loginLayout = new GridPane();
        loginLayout.setPadding(new Insets(10));
        loginLayout.setHgap(10);
        loginLayout.setVgap(10);
        loginLayout.setAlignment(Pos.CENTER);

        //controls
        Label welcomeLabel = new Label("Welcome to Fitness Management System!! Please login to proceed.");
        Label usernameLabel = new Label("Username:");
        Label passwordLabel = new Label("Password:");
        Button loginButton = new Button("Login");

        Font font = Font.font("Arial", FontWeight.BOLD, 14);
        welcomeLabel.setFont(font);
        welcomeLabel.setAlignment(Pos.CENTER);
        GridPane.setHalignment(loginButton, HPos.CENTER);

        //eventHandler for login button
        loginButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                authenticate();
            }
        });

        //adding the control to the layout
        loginLayout.add(welcomeLabel, 1, 0);
        loginLayout.add(usernameLabel, 0, 1);
        loginLayout.add(usernameField, 1, 1);
        loginLayout.add(passwordLabel, 0, 2);
        loginLayout.add(passwordField, 1, 2);
        loginLayout.add(loginButton, 1, 3);

        //adding the layout to the login Scene
        loginScene = new Scene(loginLayout, 600, 200);

        stage.setTitle("User Login");
        //adding the scene to the stage
        stage.setScene(loginScene);
        stage.show();
        
    }

    private void authenticate() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        Connection con = DBUtils.establishConnection();
        String query = "SELECT * FROM users WHERE username = ?;";
        try {
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                if (rs.getString("locked")=="TRUE") {
                    showAlert("Account Locked", "Your account has been locked due to multiple invalid login attempts.");
                    DBLogger.log("WARN","LoginInterface",username+" tried to access his locked account!!",username);
                    return; 
                }
                String salt = rs.getString("salt");
                String originalPassHash =  rs.getString("password");
                hashing hashing = new hashing(password);
                String hash = hashing.generateHash(salt);
                if (hash.equals(originalPassHash)) {
                    query = "UPDATE users SET attempts = 0 WHERE username = ?";
                    statement = con.prepareStatement(query);
                    statement.setString(1, username);
                    statement.executeUpdate();

                    String role = rs.getString("role");
                    DBUtils.closeConnection(con, statement);
                    con = DBUtils.setUser(role);
                    showConfrm("Authentication Successful", "Welcome " + username);
                    navigateToRoleInterface(role, username, con);
                } else {
                    int attempts = rs.getInt("attempts") + 1;
                    if (attempts > 10) {
                        query = "UPDATE users SET locked = 'TRUE' WHERE username = ?";
                        showAlert("Account Locked", "Your account has been locked due to multiple invalid login attempts.");
                        DBLogger.log("WARN", "LoginInterface", username+"'s account locked after 10th attempt.", username);
                    } else {
                        query = "UPDATE users SET attempts = ? WHERE username = ?";
                        showAlert("Invalid Attempt", "Invalid password. " + (10 - attempts + 1) + " attempts left before your account is locked.");
                        DBLogger.log("WARN", "LoginInterface", "Invalid login attempt no"+String.valueOf(attempts) +" for " + username, username);
                    }
                    statement = con.prepareStatement(query);
                    if (attempts > 10) {
                        statement.setString(1, username);
                    } else {
                        statement.setInt(1, attempts);
                        statement.setString(2, username);
                    }
                    statement.executeUpdate();
                }
            } else {
                showAlert("Authentication Failed", "Invalid username or password.");
                DBLogger.log("WARN", "LoginInterface", "Invalid login attempt with invalid username", null);
            }
        } catch (Exception e) {
            showAlert("Database Error", "Failed to connect to the database.");
            DBLogger.log("ERROR", "LoginInterface", "Database Connection Unsuccessful!!", null);
        }
    }

    private void navigateToRoleInterface(String role, String username, Connection con) {
        if (role.equals("receptionist")) {
            RecInterface recInterface = new RecInterface(stage, username, con);
            recInterface.initializeComponents();
            DBLogger.log("DEBUG", "LoginInterface", "User " + username + " accessed the Receptionist Interface.", username);
        } else if (role.equals("manager")) {
            MgrInterface mgrInterface = new MgrInterface(stage, username, con);
            mgrInterface.initializeComponents();
            DBLogger.log("DEBUG", "LoginInterface", "User " + username + " accessed the Manager Interface.", username);
        } else if (role.equals("trainer")) {
            traInterface traInterface = new traInterface(stage, username, con);
            traInterface.initializeComponents();
            DBLogger.log("DEBUG", "LoginInterface", "User " + username + " accessed the Trainer Interface.", username);
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showConfrm(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}