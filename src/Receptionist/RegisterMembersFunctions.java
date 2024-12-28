package Receptionist;

import common.DBLogger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

import javafx.scene.control.Alert;

public class RegisterMembersFunctions {
    private Connection con;
    private String username;

    public RegisterMembersFunctions(Connection con,String username) {
        this.con = con;
        this.username=username;
    }
    
    public boolean validateInputs(String name, String contact, String type) {
        // Validate email format
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{3}$";
        Pattern pattern = Pattern.compile(emailRegex);
        DBLogger.log("INFO","RegisterMembersFunctions","Validated registration interface inputs",username);
        return !name.isBlank() && !contact.isBlank() && type != null && pattern.matcher(contact).matches();
    }

    public int fetchMemberID() throws SQLException {
        String membQuery = "SELECT AUTO_INCREMENT AS NEXT_INT FROM information_schema.tables WHERE table_name = ? AND table_schema= ?;";
        try (PreparedStatement membStatement = con.prepareStatement(membQuery)) {
            membStatement.setString(1,"members" );
            membStatement.setString(2,"project_ssd" );
            try (ResultSet rs = membStatement.executeQuery()) {
                if (rs.next()) {
                    DBLogger.log("INFO","RegisterMembersFunctions","feteched next memberID from members table.",username);
                    return rs.getInt("NEXT_INT");
                }
            }
        }
        DBLogger.log("ERROR","RegisterMembersFunctions","failed to fetch next memberID from members table.",username);
        return -1;
    }
    public String getMembershipType(int memberID) throws SQLException {
        String membershipType = "";
        try {
            String query = "SELECT MembershipType FROM members WHERE MemberID = ?";
            PreparedStatement statement = con.prepareStatement(query);
            statement.setInt(1, memberID);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                DBLogger.log("INFO","RegisterMembersFunctions","feteched next membership type from members table.",username);
                membershipType = rs.getString("MembershipType");
            }

            rs.close();
            statement.close();
        } catch (SQLException e) {
            DBLogger.log("ERROR","RegisterMembersFunctions","failed to fetch membershipType from members table.",username);
            e.printStackTrace();
        }
        return membershipType;
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
