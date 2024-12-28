package Receptionist;
import common.DBUtils;
import common.DBLogger;
import common.LoginInterface;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.*;


public class RecInterface {
    private Scene Receptionist;
    private Stage stage;
    private String username;
    private Connection con;
    private PreparedStatement statement ;
    public GridPane recLayout = new GridPane();
    
    public RecInterface(Stage stage,String username,Connection con){
        this.stage = stage;
        this.username = username;
        this.con = con;
    }

    boolean setStage = true;
    public RecInterface(Stage stage,String username,Connection con,boolean setStage){
        this.stage = stage;
        this.username = username;
        this.con = con;
        this.setStage=false;
    }

    public void initializeComponents() {
        //Receptionist layout
        recLayout.setPadding(new Insets(10));
        recLayout.setHgap(10);
        recLayout.setVgap(10);
        
        //recLayout.setAlignment((Pos.CENTER));

        //navigation bar on left - layout
        VBox navBar = new VBox();
        navBar.setSpacing(10);
        navBar.setPadding(new Insets(10));

        // content area on the right - layout
        GridPane contentPane = new GridPane();
        contentPane.setPadding(new Insets(10));
        contentPane.setHgap(10);
        contentPane.setVgap(10);

        //controls
        Button homeButton = new Button("Home");
        Button registerButton = new Button("Register Member");
        Button renewButton = new Button("Renew Member");
        Button planButton = new Button("Assign a Plan");
        Button logout = new Button("Logout");
        Label welcomeLabel = new Label("Welcome "+username+"!! Select one of the options in the navigation bar to continue");

        //adding controls(button) to the navigation bar
        navBar.getChildren().addAll(homeButton,registerButton,renewButton,planButton,logout);

        Separator separator = new Separator();
        separator.setOrientation(Orientation.VERTICAL);
        GridPane.setVgrow(separator, javafx.scene.layout.Priority.ALWAYS);

        //adding content to content area
        contentPane.add(welcomeLabel,0,0);

        //adding navigation bar to the left
        recLayout.add(navBar, 0, 0);

        //separating using a separator
        recLayout.add(separator,1,0);

        //adding content to the right
        recLayout.add(contentPane, 2, 0);

        //eventHandlers
        homeButton.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent event) {
                contentPane.getChildren().clear();
                contentPane.add(welcomeLabel,0,0);
            }
        });

        registerButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event){
                contentPane.getChildren().clear();
                RegisterMembers registerMembers = new RegisterMembers(con,username);
                contentPane.add(registerMembers.layout,0,0);
                DBLogger.log("DEBUG","RecInterface",username+" entered Registration interface.",username);
            }
        });

        renewButton.setOnAction(new EventHandler<ActionEvent>() {
           public void handle(ActionEvent event) {
            contentPane.getChildren().clear();
            RenewMembers renewMembers = new RenewMembers(con,username);
            contentPane.add(renewMembers.layout,0,0);
            DBLogger.log("DEBUG","RecInterface",username+" entered Renew interface.",username);
           }
        });

        planButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event){
                contentPane.getChildren().clear();
                Plan plan = new Plan(con,username);
                contentPane.add(plan.layout,0,0);
                DBLogger.log("DEBUG","RecInterface",username+" entered Plan interface.",username);
            }
        });

        logout.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event){
                LoginInterface login = new LoginInterface(stage);
                DBUtils.closeConnection(con,statement);
                login.initializeComponents();
                DBLogger.log("DEBUG","RecInterface",username+" Logged out.",username);
            }
        });

        if(setStage){
            Receptionist = new Scene(recLayout, 700, 500);
            stage.setTitle("Receptionist Interface");
            stage.setScene(Receptionist);
            stage.show();
            DBLogger.log("DEBUG","RecInterface",username+" entered Receptionist interface.",username);
        }else{
            navBar.getChildren().remove(logout);
            navBar.getChildren().remove(homeButton);
        }
    }
}