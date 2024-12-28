package Manager;
import common.DBLogger;
import java.sql.Connection;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;


public class maintenanceInterface {
    private Connection con;
    GridPane maintenanceLayout = new GridPane();
    private String username;

    public maintenanceInterface(Connection con, String username){
        this.con=con;
        this.username=username;
    }

    public void initializeComponents() {
        //Maintenance Layout
        maintenanceLayout.setPadding(new Insets(10));
        maintenanceLayout.setHgap(10);
        maintenanceLayout.setVgap(10);

        //controls - buttons
        Button schedule = new Button("Schedule Maintenance");
        Button records = new Button("Maintenance Records");

        //adding controls to the layout
        maintenanceLayout.add(schedule,0,0);
        maintenanceLayout.add(records,0,1);

        //Align buttons in the center
        GridPane.setHalignment(schedule,javafx.geometry.HPos.CENTER);
        GridPane.setHalignment(records,javafx.geometry.HPos.CENTER );   

        //event handlers
        schedule.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent event) {
                maintenanceLayout.getChildren().clear();
                scheduleMaintenance scheduleMaintenance = new scheduleMaintenance(con,maintenanceLayout,username);
                scheduleMaintenance.initializeComponents();
                DBLogger.log("DEBUG","MgrInterface",username+" accessed Schedule Maintenance Interface",username);
            }
        });

        records.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event){
                maintenanceLayout.getChildren().clear();
                maintenanceRecords records = new maintenanceRecords(con,maintenanceLayout,username);
                records.initializeComponents();
                DBLogger.log("DEBUG","MgrInterface",username+" accessed View Maintenance Records Interface",username);
            }
        });
    }
}
