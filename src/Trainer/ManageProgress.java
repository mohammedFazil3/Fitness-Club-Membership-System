package Trainer;
import java.sql.*;
import javafx.geometry.Insets;
import javafx.event.*;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import common.DBLogger;

public class ManageProgress {
    private Connection con;
    private String username;
    GridPane manageProgressLayout = new GridPane();
    InsertProgress insertProgress;
    TrackProgress trackProgress ;

    public ManageProgress(Connection con,String username){
        this.con=con;
        this.username = username;
    }

    public void initializeComponents() {
        //Management Layout
        manageProgressLayout.setPadding(new Insets(10));
        manageProgressLayout.setHgap(10);
        manageProgressLayout.setVgap(10);

        //controls - buttons
        Button insertProgressButton = new Button("Insert a Progress");
        Button trackProgressButton = new Button("Track a Progress");

        //adding controls to the layout
        manageProgressLayout.add(insertProgressButton,0,0);
        manageProgressLayout.add(trackProgressButton,0,1);

        //Align buttons in the center
        GridPane.setHalignment(insertProgressButton,javafx.geometry.HPos.CENTER);
        GridPane.setHalignment(trackProgressButton,javafx.geometry.HPos.CENTER );   

        //event handlers
        insertProgressButton.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent event) {
                manageProgressLayout.getChildren().clear();
                insertProgress = new InsertProgress(con,manageProgressLayout,username);
                insertProgress.initializeComponents();
                DBLogger.log("DEBUG","ManageProgress",username+" entered insert progress interface.",username);
            }
        });

        trackProgressButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event){
                manageProgressLayout.getChildren().clear();                
                trackProgress = new TrackProgress(con,manageProgressLayout,username);
                trackProgress.initializeComponents();
                DBLogger.log("DEBUG","ManageProgress",username+" entered track progress interface.",username);
            }
        });
    }
}
