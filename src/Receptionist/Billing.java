package Receptionist;
import common.DBLogger;


import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.geometry.*;
import java.sql.*;
import java.time.LocalDate;

public class Billing {
    private Scene scene;
    public Stage stage;
    private Connection con;
    private String billType;
    private String name;
    private String contact;
    private String regDate;
    private LocalDate expiryDate;
    private int memberID;
    private int classID;
    private int facilityID;
    private String memberType;
    private BillingFunctions billingFunctions;
    private AfterBilling afterBilling;
    private PlanFunctions planFunctions;
    private String username;
    public Boolean bool=false;

    Billing(Connection con,String billType,String name,String contact,String regDate,LocalDate expiryDate, int memberID, int classID, int facilityID,String username,String memberType) throws SQLException {
        this.con = con;
        this.name=name;
        this.contact=contact;
        this.regDate=regDate;
        this.expiryDate=expiryDate;
        this.memberID = memberID;
        this.classID = classID;
        this.facilityID = facilityID;
        this.billType=billType;
        this.username=username;
        this.billingFunctions = new BillingFunctions(con,username);
        this.memberType=memberType;
        this.afterBilling = new AfterBilling(con,name,contact,regDate,expiryDate,memberID, classID, facilityID,memberType,username);
        this.planFunctions = new  PlanFunctions(con,username);
    }

    public void initInterface() throws SQLException {
        stage = new Stage();
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Label sceneTitle = new Label("Billing Details");
        sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(sceneTitle, 0, 0, 2, 1);

        Label billingDateLabel = new Label("Billing Date:");
        grid.add(billingDateLabel, 0, 1);

        DatePicker billingDatePicker = new DatePicker();
        billingDatePicker.setValue(LocalDate.now());
        billingDatePicker.setDisable(true); // Read-only
        grid.add(billingDatePicker, 1, 1);

        Label amountLabel = new Label("Amount:");
        grid.add(amountLabel, 0, 2);

        TextField amountField = new TextField();
        double amount = billingFunctions.fetchAmount(facilityID, memberID,classID);
        if(amount!=0.0){
            amountField.setText(String.format("%.2f", amount));
        }else{ //Registration or renewal bill amount
            facilityID=0;
            amount=50.0;//Registration Amount
            amountField.setText(String.format("%.2f", amount));          
        }
        amountField.setEditable(false); // Read-only
        grid.add(amountField, 1, 2);

        Label paymentMethodLabel = new Label("Payment Method:");
        grid.add(paymentMethodLabel, 0, 3);

        ToggleGroup paymentMethodGroup = new ToggleGroup();
        RadioButton cashRadioButton = new RadioButton("Cash");
        cashRadioButton.setToggleGroup(paymentMethodGroup);
        cashRadioButton.setSelected(true); // Default selection
        RadioButton cardRadioButton = new RadioButton("Card");
        cardRadioButton.setToggleGroup(paymentMethodGroup);
        grid.add(cashRadioButton, 1, 3);
        grid.add(cardRadioButton, 2, 3);

        Label additionalInfoLabel = new Label("Additional Info:");
        grid.add(additionalInfoLabel, 0, 4);

        TextArea additionalInfoTextArea = new TextArea();
        grid.add(additionalInfoTextArea, 1, 4, 2, 1);

        Button confirmPaymentButton = new Button("Confirm Payment");
        Button cancelButton = new Button("Cancel");
        HBox buttonsBox = new HBox(10);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.getChildren().addAll(confirmPaymentButton, cancelButton);
        grid.add(buttonsBox, 0, 6, 3, 1);

        confirmPaymentButton.setOnAction(event -> {
            String paymentMethod = ((RadioButton) paymentMethodGroup.getSelectedToggle()).getText();
            String additionalInfo = additionalInfoTextArea.getText().trim();
            if(billType=="reg"){
                try {
                    afterBilling.regAfterBilling();
                } catch (SQLException e) {
                    e.printStackTrace();
                    DBLogger.log("ERROR","Billing","Failed to insert billing into billing table.",username);
                }
            }
            if(planFunctions.validateMemberID(memberID)){// Insert into billing table if memberID exist(member should exist)
                try {
                    String billingQuery = "INSERT INTO billing (MemberID, BillingDate, Amount, PaymentMethod, AdditionalInfo, FacilityID) " + "VALUES (?, ?, ?, ?, ?, ?)";
                    PreparedStatement billingStatement = con.prepareStatement(billingQuery);
                    billingStatement.setInt(1, memberID);
                    billingStatement.setDate(2, java.sql.Date.valueOf(LocalDate.now()));
                    billingStatement.setDouble(3, Double.parseDouble(amountField.getText()));
                    billingStatement.setString(4, paymentMethod);
                    billingStatement.setString(5, additionalInfo);
                    if(facilityID==0){
                        billingStatement.setNull(6, Types.VARCHAR); // Set facilityID to NULL
                    }else{
                        billingStatement.setInt(6, facilityID);
                    }
                    int rs=billingStatement.executeUpdate();
                    if (rs==1) {
                        DBLogger.log("INFO","Billing","Billing inserted into billing table.",username);
                    }
                    billingFunctions.showAlert("Success", "Billing details inserted successfully.");
                    bool=true;
                    billingStatement.close();
                    stage.close();
                } catch (SQLException | NumberFormatException e) {
                    DBLogger.log("ERROR","Billing","Failed to insert billing into billing table.",username);
                    billingFunctions.showAlert("Error", "Failed to insert billing details.");
                    e.printStackTrace();
                }
            }else{
                DBLogger.log("ERROR","Billing","Failed to insert billing into billing table(Invalid MemberID).",username);
                billingFunctions.showAlert("Error", "Failed to insert billing details.");
            }
            
            if (facilityID==0) {
                if(bool){
                    try {
                        afterBilling.renewAfterBilling();
                    } catch (SQLException e) {
                        e.printStackTrace();
                        DBLogger.log("ERROR","Billing","Failed to insert billing into billing table.",username);
                    }
                }else{
                    billingFunctions.showAlert("Renewal Failed", "Member not Renewed.");
                }
            }else{
                if(bool){
                    try {
                        afterBilling.planAfterBilling();
                    } catch (SQLException e) {
                        DBLogger.log("ERROR","Billing","Failed to insert billing into billing table.",username);
                        e.printStackTrace();
                    }
                }else{
                    billingFunctions.showAlert("Plan Assignment Failed", "Plan Has Not been Assigned.");
                }
            }
        });

        cancelButton.setOnAction(event -> {
            stage.close();
        });

        scene = new Scene(grid, 400, 300);
        stage.setTitle("Billing");
        stage.setScene(scene);
        stage.show();
    }
}