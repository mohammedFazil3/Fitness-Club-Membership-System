package Manager;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.sql.Connection;

import common.DBLogger;

public class maintenanceRecords {
    private Connection con;
    private GridPane layout;
    private HBox buttonsLayout;
    private String username;

    public maintenanceRecords(Connection con, GridPane maintenanceLayout, String username) {
        this.con=con;
        this.layout=maintenanceLayout;
        this.username=username;
    }

    public void initializeComponents(){
        //labels
        Label facility = new Label("Choose facility:");
        Label area = new Label("Choose Maintenance Area:");
        Label month = new Label("Choose Month");

        // Choose Facility ComboBox/Select Form
        ComboBox<String> facilityComboBox = new ComboBox<>();
        facilityComboBox.setPromptText("");
        scheduleMaintenanceFunctions functions = new scheduleMaintenanceFunctions(con,username);
        facilityComboBox.setOnMouseClicked(event -> {
            functions.facilityGetItems(facilityComboBox);
        });

        // Choose Maintenance Area ComboBox/Select Form
        ComboBox<String> areaComboBox = new ComboBox<>();
        areaComboBox.setPromptText("");
        areaComboBox.setOnMouseClicked(event ->{
            functions.areaGetItems(facilityComboBox.getValue(), areaComboBox);
        });

        // Choose Month ComboBox/Select Form
        ComboBox<String> monthComboBox = new ComboBox<>();
        monthComboBox.setPromptText("");
        monthComboBox.setOnMouseClicked(event ->{
            functions.monthGetItems(facilityComboBox.getValue(), areaComboBox.getValue(),monthComboBox);
        });

        //Buttons
        Button recordsButton = new Button("View Records");
        Button exitButton = new Button("Exit");
        buttonsLayout = new HBox();
        buttonsLayout.setSpacing(10);
        buttonsLayout.getChildren().addAll(recordsButton,exitButton);

        // Add components to the layout
        layout.add(facility,0,0);
        layout.add(area,0,1);
        layout.add(month,0,2);
        layout.add(facilityComboBox, 1, 0);
        layout.add(areaComboBox, 1, 1);
        layout.add(monthComboBox,1,2);
        layout.add(buttonsLayout, 0, 3,2,1); // spans the button across two columns

        //eventHandlers
        recordsButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event){
                recordsFunctions rFunctions = new recordsFunctions(con,username);
                if(!rFunctions.checkFormEmpty(facilityComboBox.getValue(),areaComboBox.getValue(),monthComboBox.getValue())){
                    layout.getChildren().clear();
                    recordsInterface recordsInterface = new recordsInterface(con, facilityComboBox.getValue(),areaComboBox.getValue(),monthComboBox.getValue(),username);
                    recordsInterface.initializeComponents(layout);
                    common.DBLogger.log("INFO","MgrInterface",username+" accessed View Records Interface!!",username);
                }
            }
        });
        exitButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event){
                layout.getChildren().clear();
                maintenanceInterface maintenance = new maintenanceInterface(con,username);
                maintenance.initializeComponents();
                layout.add(maintenance.maintenanceLayout,0,0);
            }
        });  

    }
}
