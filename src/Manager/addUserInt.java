package Manager;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.ColumnConstraints;
import java.security.SecureRandom;
import java.sql.*;
import common.hashing;

public class addUserInt {
    private Connection con;
    private String username;
    private ComboBox<String> roleComboBox;
    private TextField usernameField, userIdField;
    private PasswordField passwordField;
    private Button submitButton;

    public addUserInt(Connection con, String username) {
        this.con = con;
        this.username = username;
    }

    public void initializeComponents(GridPane contentPane) {
        // Layout setup
        contentPane.setVgap(10);
        contentPane.setHgap(10);
        ColumnConstraints column1 = new ColumnConstraints(150);
        ColumnConstraints column2 = new ColumnConstraints(200);
        contentPane.getColumnConstraints().addAll(column1, column2);

        // Username
        usernameField = new TextField();
        contentPane.add(new Label("Username:"), 0, 0);
        contentPane.add(usernameField, 1, 0);

        // Password
        passwordField = new PasswordField();
        contentPane.add(new Label("Password:"), 0, 1);
        contentPane.add(passwordField, 1, 1);

        // Role ComboBox
        roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll("manager", "receptionist", "trainer");
        contentPane.add(new Label("Role:"), 0, 2);
        contentPane.add(roleComboBox, 1, 2);

        // User ID
        userIdField = new TextField();
        contentPane.add(new Label("User ID:"), 0, 3);
        contentPane.add(userIdField, 1, 3);

        // Submit button
        submitButton = new Button("Submit");
        contentPane.add(submitButton, 1, 4);
        submitButton.setOnAction(e -> onSubmit());
    }

    private void onSubmit() {
        // Validate inputs
        if (!usernameField.getText().matches("[a-zA-Z0-9_]*")) {
            showAlert(Alert.AlertType.ERROR, "Username contains invalid characters!");
            return;
        }

        if (!isValidPassword(passwordField.getText())) {
            showAlert(Alert.AlertType.ERROR, "Password does not meet complexity requirements!");
            return;
        }

        if (roleComboBox.getValue() == null || userIdField.getText().isEmpty() ||
                usernameField.getText().isEmpty() || passwordField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "All fields are required!");
            return;
        }

        if (!isValidUserId(userIdField.getText(), roleComboBox.getValue())) {
            showAlert(Alert.AlertType.ERROR, "Invalid or already used User ID!");
            return;
        }

        try {
            // Generate hash and salt
            hashing hashGenerator = new hashing(passwordField.getText());
            String[] hashAndSalt = hashGenerator.generateHash();

            String sql = "INSERT INTO users (userID, username, password, salt, role) VALUES (?, ?, ?, ?, ?)";

            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setString(1, userIdField.getText());
                stmt.setString(2, usernameField.getText());
                stmt.setString(3, hashAndSalt[0]);
                stmt.setString(4, hashAndSalt[1]);
                stmt.setString(5, roleComboBox.getValue());

                int affectedRows = stmt.executeUpdate();
                if (affectedRows > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "User registered successfully!");
                } else {
                    throw new SQLException("Creating user failed, no rows affected.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error during registration: " + e.getMessage());
        }
    }

    private boolean isValidUserId(String userId, String role) {
        String sql = "SELECT COUNT(*) FROM users WHERE userID = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isValidPassword(String password) {
        String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
        return password.matches(passwordPattern);
    }

    private void showAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType, message);
        alert.showAndWait();
    }
}
