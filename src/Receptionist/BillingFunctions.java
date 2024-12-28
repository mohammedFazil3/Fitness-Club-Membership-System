package Receptionist;
import common.DBLogger;


import javafx.scene.control.Alert;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BillingFunctions {
    private Connection con;
    private String username;

    public BillingFunctions(Connection con,String username) {
        this.con = con;
        this.username=username;
    }

    public double fetchAmount(int facilityID, int memberID,int classID) throws SQLException {
        double amount = 0.0;
        String membershipType = getMembershipType(memberID);
        boolean hasTrainer = hasTrainer(classID);

        try {
            String query = "SELECT * FROM facility_prices WHERE FacilityID = ?";
            PreparedStatement statement = con.prepareStatement(query);
            statement.setInt(1, facilityID);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                DBLogger.log("INFO","BillingFunctions","Amount fetched from facility_prices table.",username);
                if (membershipType.equals("Yearly")) {
                    if (hasTrainer) {
                        amount = rs.getDouble("YearlyPriceWithTrainer");
                    } else {
                        amount = rs.getDouble("YearlyPriceWithoutTrainer");
                    }
                } else if (membershipType.equals("Quarterly")) {
                    if (hasTrainer) {
                        amount = rs.getDouble("QuarterlyPriceWithTrainer");
                    } else {
                        amount = rs.getDouble("QuarterlyPriceWithoutTrainer");
                    }
                } else if (membershipType.equals("Monthly")) {
                    if (hasTrainer) {
                        amount = rs.getDouble("MonthlyPriceWithTrainer");
                    } else {
                        amount = rs.getDouble("MonthlyPriceWithoutTrainer");
                    }
                }
            }
            rs.close();
            statement.close();
        } catch (SQLException e) {
            DBLogger.log("ERROR","BillingFunctions","Failed to fetch amount from facility_prices table.",username);
            e.printStackTrace();
        }
        return amount;
    }

    public String getMembershipType(int memberID) throws SQLException {
        String membershipType = "";
        try {
            String query = "SELECT MembershipType FROM members WHERE MemberID = ?";
            PreparedStatement statement = con.prepareStatement(query);
            statement.setInt(1, memberID);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                DBLogger.log("INFO","BillingFunctions","MembershipType fetched from members table.",username);
                membershipType = rs.getString("MembershipType");
            }

            rs.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            DBLogger.log("ERROR","BillingFunctions","Failed to fetch membershipType from members table.",username);
        }
        return membershipType;
    }

    public boolean hasTrainer(int classID) throws SQLException {
        boolean hasTrainer = false;
        if (classID!=-1) {
            hasTrainer = true; // Assuming trainer is there.
        }
        return hasTrainer;
    }

    public void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
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

