package Manager;
import common.DBLogger;
import common.DBUtils;
import common.LoginInterface;
import Trainer.traInterface;
import Receptionist.RecInterface;


import java.sql.Connection;
import java.sql.PreparedStatement;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MgrInterface {
    private Scene Manager;    
    private Stage stage;
    private String username;
    private Connection con;
    private PreparedStatement statement ;

    public MgrInterface(Stage stage,String username,Connection con){
        this.stage = stage;
        this.username = username;
        this.con = con;
    }

    public void initializeComponents() {
        GridPane manLayout = new GridPane();
        manLayout.setPadding(new Insets(10));
        manLayout.setHgap(10);
        manLayout.setVgap(10);

        //navigation bar on left - layout
        VBox navBar = new VBox();
        navBar.setSpacing(10);
        navBar.setPadding(new Insets(10));

        //separator
        Separator separator = new Separator();
        separator.setOrientation(Orientation.VERTICAL);
        GridPane.setVgrow(separator, javafx.scene.layout.Priority.ALWAYS); // to extend the line till the end

        // content area on the right - layout
        GridPane contentPane = new GridPane();
        contentPane.setPadding(new Insets(10));
        contentPane.setHgap(10);
        contentPane.setVgap(10);

        //controls
        Label welcomeLabel = new Label("Welcome "+username+"!! Select one of the options in the navigation bar to continue");
        Button home = new Button("Home");
        Button recButton = new Button("Switch to Receptionist Interface");
        Button traButton = new Button("Switch to Trainer Interface");
        Button manageClassButton = new Button("Manage Classes");
        Button scheduleMaintenance = new Button("Schedule Facility Maintenance");
        Button monitorLogs = new Button("Monitor Logs");
        Button manageUsers = new Button("Manage Users");
        Button logout = new Button("Logout");


        //adding controls(button) to the navigation bar
        navBar.getChildren().addAll(home,recButton,traButton,manageClassButton,scheduleMaintenance,monitorLogs,manageUsers,logout);
        //adding content to content area
        contentPane.add(welcomeLabel,0,0);
        //adding navigation bar to the left
        manLayout.add(navBar, 0, 0);
        //separating using a separator
        manLayout.add(separator,1,0);
        //adding content to the right
        manLayout.add(contentPane, 2, 0);
        
        //eventHandlers
        home.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent event) {
                contentPane.getChildren().clear();
                contentPane.add(welcomeLabel,0,0);
            }
        });
        recButton.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event){
                RecInterface rec = new RecInterface(stage, username, con,false);
                DBLogger.log("DEBUG","MgrInterface",username+" accessed Receptionist Interface",username);
                contentPane.getChildren().clear();
                rec.initializeComponents();
                contentPane.add(rec.recLayout,0,0);
            }
        });
        traButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event){
                traInterface trainer = new traInterface(stage, username, con,false);
                DBLogger.log("DEBUG","MgrInterface",username+" accessed Trainer Interface",username);
                contentPane.getChildren().clear();
                trainer.initializeComponents();
                contentPane.add(trainer.traLayout,0,0);
            }
        });
        scheduleMaintenance.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event){
                maintenanceInterface maintenance = new maintenanceInterface(con,username);
                DBLogger.log("DEBUG","MgrInterface",username+" accessed Maintenance Interface",username);
                contentPane.getChildren().clear();
                maintenance.initializeComponents();
                contentPane.add(maintenance.maintenanceLayout,0,0);
            }
        });
        manageClassButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event){
                contentPane.setAlignment(Pos.TOP_CENTER);
                ManageClass manageClass = new ManageClass(con,username);
                DBLogger.log("DEBUG","MgrInterface",username+" accessed Manage Class Interface",username);
                contentPane.getChildren().clear();
                manageClass.initializeComponents();
                contentPane.add(manageClass.manageClassLayout,0,0);
            }
        });
        logout.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event){
                LoginInterface login = new LoginInterface(stage);
                DBUtils.closeConnection(con,statement);
                DBLogger.log("INFO","MgrInterface",username+" Logged Out Successfully!!",username);
                login.initializeComponents();
            }
        });
        monitorLogs.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event){
                MonitorLogsInterface monitor = new MonitorLogsInterface(con,username);
                contentPane.getChildren().clear();
                monitor.initializeComponents(contentPane);
                DBLogger.log("DEBUG","MgrInterface",username+" accessed Monitor Logs Interface",username);
            }
        }); 
        manageUsers.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent event){
                ManageUserInterface manage = new ManageUserInterface(con,username);
                contentPane.getChildren().clear();
                manage.initializeComponents(contentPane);
                DBLogger.log("DEBUG","MgrInterface",username+" accessed Manage Users Interface",username);
            }
        });       

        Manager = new Scene(manLayout, 1200, 500);
        stage.setTitle("Manager Interface");
        stage.setScene(Manager);
        stage.show();
    }
}