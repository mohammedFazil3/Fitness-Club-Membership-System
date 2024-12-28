package Manager;

import java.sql.Connection;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public class ManageUserInterface {
    private Connection con;
    private String username;
    public ManageUserInterface(Connection con, String username) {
        this.con = con;
        this.username=username;
    }

    public void initializeComponents(GridPane contentPane) {
        //buttons
        Button add = new Button("Add User");
        Button lock = new Button("Remove Lock");

        //adding controls to the layout
        contentPane.add(add,0,0);
        contentPane.add(lock,0,1);

        //Align buttons in the center
        GridPane.setHalignment(add,javafx.geometry.HPos.CENTER);
        GridPane.setHalignment(lock,javafx.geometry.HPos.CENTER );   

        //event handlers
        add.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent event) {
                contentPane.getChildren().clear();
                addUserInt addUser = new addUserInt(con,username);
                contentPane.getChildren().clear();
                addUser.initializeComponents(contentPane);
                common.DBLogger.log("DEBUG","ManageUserInterface",username+" accessed Add User Interface",username);
            }
        });

        lock.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event){
                contentPane.getChildren().clear();
                lockUserInt lockUser = new lockUserInt(con,username);
                contentPane.getChildren().clear();
                lockUser.initializeComponents(contentPane);
                common.DBLogger.log("DEBUG","ManageUserInterface",username+" accessed Remove Lock Interface",username);
            }
        });        
    }

}
