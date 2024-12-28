package Receptionist;
import common.DBLogger;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class AfterBilling {
    private Connection con;
    private String name;
    private String contact;
    private String regDate;
    private LocalDate expiryDate;
    private int memberID;
    private int classID;
    private int facilityID;
    private String memberType;
    private String username;
    private BillingFunctions billingFunctions;

    AfterBilling(Connection con,String name,String contact,String regDate,LocalDate expiryDate,int memberID,int classID,int facilityID,String memberType,String username){
        this.con = con;
        this.name=name;
        this.contact=contact;
        this.regDate=regDate;
        this.expiryDate=expiryDate;
        this.memberID = memberID;
        this.classID=classID;
        this.facilityID=facilityID;
        this.memberType=memberType;
        this.username=username;
        this.billingFunctions = new BillingFunctions(con,username);
    }

    public void regAfterBilling() throws SQLException{
        // Insert into members table
        try {
            String query = "INSERT INTO members (Name, ContactInformation, MembershipType, RegistrationDate, membership_expiry) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, name);
            statement.setString(2, contact);
            statement.setString(3, memberType);
            statement.setString(4, regDate);
            statement.setString(5, expiryDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            int rs = statement.executeUpdate();
            if (rs==1) {
                DBLogger.log("INFO","AfterBilling","Registration of a new member into members table.",username);
            } 
            billingFunctions.showConfrm("Registration Successful", "New member has been successfully registered.");
        } catch (SQLException e) {
            e.printStackTrace();
            DBLogger.log("ERROR","AfterBilling","Failed to Register a member into members table.",username);
            billingFunctions.showAlert("Database Error", "Failed to register member. Please try again.");
        }
    }

    public void renewAfterBilling() throws SQLException{
        // Update membership expiry in the database
        try {
            String updateQuery = "UPDATE members SET membership_expiry = ?, MembershipType = ? WHERE MemberID = ?";
            PreparedStatement updateStatement = con.prepareStatement(updateQuery);
            updateStatement.setString(1, expiryDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            updateStatement.setString(2, memberType);
            updateStatement.setInt(3, memberID);
            int rs=updateStatement.executeUpdate();
            if (rs>0) {
                DBLogger.log("INFO","AfterBilling","Renewal of a member in members table.",username);
            } 
            billingFunctions.showConfrm("Membership Renewed", "Membership has been successfully renewed.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            DBLogger.log("ERROR","AfterBilling","Failed to Renew a member in members table.",username);
            billingFunctions.showAlert("Database Error", "Failed to renew membership. Please try again.");
        }
    }

    public void planAfterBilling() throws SQLException{
        try {
            String query = "INSERT INTO `member_plan` (`member_id`, `class_id`, `facilityID`) VALUES (?, ?, ?)";
            PreparedStatement statement = con.prepareStatement(query);
            statement.setInt(1,(memberID));
            if (classID==0) {
                statement.setNull(2, Types.INTEGER); // Set classID to NULL
            } else {
                statement.setInt(2, classID);
            }
            statement.setInt(3, facilityID);

            int rs = statement.executeUpdate();
            if (rs==1) {
                DBLogger.log("INFO","AfterBilling","Plan Assignment of a member successful and inserted into member_plan table.",username);
            } 
            billingFunctions.showConfrm("Plan Assigned", "Plan has been successfully assigned to the member.");
        } catch (SQLException e) {
            e.printStackTrace();
            DBLogger.log("ERROR","AfterBilling","Failed to Assign plan to a  member.",username);
            billingFunctions.showAlert("Database Error", "Failed to assign plan. Please try again.");
        } catch (NumberFormatException e) {
            DBLogger.log("ERROR","AfterBilling","Failed to Assign plan to a  member.",username);
            billingFunctions.showAlert("Invalid Input", "Please enter a valid member ID.");
        } 
    }
}