package Trainer;
import common.DBUtils;
import common.LoginInterface;
import common.DBLogger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class traInterface {
    private Scene Trainer;    
    private Stage stage;
    private String username;
    private Connection con;
    private PreparedStatement statement;
    boolean setStage = true;
    public GridPane traLayout = new GridPane();
    ManageProgress manageProgress;
    ViewClass viewClass;

    public traInterface(Stage stage,String username,Connection con){
        this.stage = stage;
        this.username = username;
        this.con = con;
    }

    public traInterface(Stage stage,String username,Connection con,boolean setStage){
        this.stage = stage;
        this.username = username;
        this.con = con;
        this.setStage=false;
    }

    public void initializeComponents() {
        //Trainer Layout
        traLayout.setPadding(new Insets(10));
        traLayout.setHgap(10);
        traLayout.setVgap(10);

        //navigation bar on left - layout
        VBox navBar = new VBox();
        navBar.setSpacing(10);
        navBar.setPadding(new Insets(10));
                
        // content area on the right - layout
        GridPane contentPane = new GridPane();
        contentPane.setPadding(new Insets(10));
        contentPane.setHgap(10);
        contentPane.setVgap(10);
        
        //separator
        Separator separator = new Separator();
        separator.setOrientation(Orientation.VERTICAL);
        GridPane.setVgrow(separator, javafx.scene.layout.Priority.ALWAYS);

        //controls
        Label welcomeLabel = new Label("Welcome "+username+"!! Select one of the options in the navigation bar to continue");
        Button viewClassButton = new Button("View My Classes");
        Button trackProgressButton = new Button("Track Progress");
        Button logout = new Button("Logout");
        Button home = new Button("Home");
        ComboBox<String> trainerCombobox = new ComboBox<>();
        trainerCombobox.setPromptText("Select Trainer");
        populateTrainers(trainerCombobox);
        trainerCombobox.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event){
                contentPane.getChildren().clear();
                if(!trainerCombobox.getItems().isEmpty()){
                    viewClassButton.setDisable(false);
                    trackProgressButton.setDisable(false);
                }else{
                    viewClassButton.setDisable(true);
                    trackProgressButton.setDisable(true);
                }
            }
        });
        //eventhandlers
        home.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent event) {
                contentPane.getChildren().clear();
                contentPane.add(welcomeLabel,0,0);
            }
        });

        viewClassButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event){
                contentPane.setAlignment(Pos.TOP_CENTER);
                if(!setStage){
                    viewClass = new ViewClass(con,getUsernameFromTrainerName(trainerCombobox.getValue()));
                }else{
                    viewClass = new ViewClass(con, username);
                }
                contentPane.getChildren().clear();
                contentPane.add(viewClass.layout,0,0);
                DBLogger.log("DEBUG","traInterface",username+" entered view class interface.",username);
            }
        });
        
        
        trackProgressButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event){
                contentPane.setAlignment(Pos.TOP_CENTER);
                if(!setStage){
                    manageProgress = new ManageProgress(con,getUsernameFromTrainerName(trainerCombobox.getValue()));
                }else{
                    manageProgress = new ManageProgress(con,username); 
                }
                contentPane.getChildren().clear();
                manageProgress.initializeComponents();
                contentPane.add(manageProgress.manageProgressLayout,0,0);
                DBLogger.log("DEBUG","traInterface",username+" entered manage class interface.",username);
            }
        });
        
        logout.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event){
                LoginInterface login = new LoginInterface(stage);
                DBUtils.closeConnection(con,statement);
                login.initializeComponents();
                DBLogger.log("DEBUG","traInterface",username+" logged out of trainer interface.",username);
            }
        });

        //adding controls to the nav bar
        navBar.getChildren().addAll(home,viewClassButton,trackProgressButton,logout);

        //adding content to content area
        contentPane.add(welcomeLabel,0,0);

        //adding navigation bar to the left
        traLayout.add(navBar, 0, 0);

        //separating using a separator
        traLayout.add(separator,1,0);

        //adding content to the right
        traLayout.add(contentPane, 2, 0);

        if(setStage){
            Trainer = new Scene(traLayout, 700, 500);
            stage.setTitle("Trainer Interface");
            stage.setScene(Trainer);
            stage.show();
            DBLogger.log("DEBUG","traInterface",username+" entered Trainer interface.",username);
        }else{
            navBar.getChildren().remove(logout);
            navBar.getChildren().remove(home);
            navBar.getChildren().add(trainerCombobox);
            viewClassButton.setDisable(true);
            trackProgressButton.setDisable(true);
        }        
    }
    private void populateTrainers(ComboBox<String> trainerCombobox) {
        String query = "SELECT Name FROM trainers";
        try (PreparedStatement statement = con.prepareStatement(query)) {
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String trainerName=(rs.getString("Name"));
                trainerCombobox.getItems().add(trainerName);
            }
            if(rs.next()){
                DBLogger.log("INFO","traInterface","trainer Name populated in trainers combobox.",username);
            }
        } catch (SQLException e) {
            DBLogger.log("ERROR","traInterface","Failed to populate trainers combobox.",username);
            e.printStackTrace();
        }
    }
    private String getUsernameFromTrainerName(String name) {
        String uname="";
        String query = "SELECT username FROM trainers WHERE Name = ?";
        try (PreparedStatement statement = con.prepareStatement(query)) {
            statement.setString(1, name);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                DBLogger.log("INFO","traInterface","Successfully fetched username from trainers table.",username);
                uname = rs.getString("username");
            }
        } catch (SQLException e) {
            DBLogger.log("ERROR","traInterface","Failed fetch username from trainers table.",username);
            e.printStackTrace();
        }
        return uname;
    }
}