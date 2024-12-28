package Manager;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Alert.AlertType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class lockUserInt {
    private Connection con;
    private String username;
    private ComboBox<String> lockedUsersComboBox;
    private Button unlockButton;

    public lockUserInt(Connection con, String username) {
        this.con = con;
        this.username = username;
    }

    public void initializeComponents(GridPane contentPane) {
        List<String> lockedUsers = getLockedUsers();

        if (lockedUsers.isEmpty()) {
            contentPane.add(new Label("There are no users locked!!"), 0, 0);
        } else {
            lockedUsersComboBox = new ComboBox<>();
            lockedUsersComboBox.getItems().addAll(lockedUsers);
            contentPane.add(new Label("Locked Users:"), 0, 0);
            contentPane.add(lockedUsersComboBox, 1, 0);

            unlockButton = new Button("Unlock");
            contentPane.add(unlockButton, 1, 1);
            unlockButton.setOnAction(e -> unlockUser());
        }
    }

    private List<String> getLockedUsers() {
        List<String> users = new ArrayList<>();
        String sql = "SELECT username FROM users WHERE locked = 'TRUE'";
        try (PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                users.add(rs.getString("username"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    private void unlockUser() {
        if (lockedUsersComboBox.getValue() != null) {
            String sql = "UPDATE users SET locked = 'FALSE' WHERE username = ?";
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setString(1, lockedUsersComboBox.getValue());
                int affectedRows = stmt.executeUpdate();
                if (affectedRows > 0) {
                    Alert alert = new Alert(AlertType.INFORMATION, "User unlocked successfully!");
                    alert.showAndWait();
                } else {
                    throw new SQLException("Unlocking user failed, no rows affected.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                Alert alert = new Alert(AlertType.ERROR, "Error during unlocking: " + e.getMessage());
                alert.showAndWait();
            }
        }
    }
}
