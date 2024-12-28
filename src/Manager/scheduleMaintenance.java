package Manager;

import java.sql.Connection;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class scheduleMaintenance {
    private Connection con;
    private GridPane layout;
    private HBox buttonsLayout;
    private String username;

    public scheduleMaintenance(Connection con,GridPane maintenanceLayout, String username){
        this.username=username;
        this.con = con;
        this.layout = maintenanceLayout;
    }

    public void initializeComponents(){
        //labels
        Label facility = new Label("Choose facility:");
        Label area = new Label("Choose Maintenance Area:");

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

        //Buttons
        Button scheduleButton = new Button("Schedule");
        Button exitButton = new Button("Exit");
        buttonsLayout = new HBox();
        buttonsLayout.setSpacing(10);
        buttonsLayout.getChildren().addAll(scheduleButton,exitButton);

        // Add components to the layout
        layout.add(facility,0,0);
        layout.add(area,0,1);
        layout.add(facilityComboBox, 1, 0);
        layout.add(areaComboBox, 1, 1);
        layout.add(buttonsLayout, 0, 2,2,1); // spans the button across two columns

        //eventhandlers
        scheduleButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event){
                if(!functions.checkFormEmpty(facilityComboBox.getValue(),areaComboBox.getValue())){
                    if(functions.scheduleMaintenance(facilityComboBox.getValue(),areaComboBox.getValue())){
                        layout.getChildren().clear();
                        maintenanceInterface maintenance = new maintenanceInterface(con,username);
                        maintenance.initializeComponents();
                        layout.add(maintenance.maintenanceLayout,0,0);
                    }
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
