package Manager;
import java.sql.*;
import common.DBLogger;
import javafx.event.*;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public class ManageClass {
    private Connection con;
    private String username;
    GridPane manageClassLayout = new GridPane();

    public ManageClass(Connection con, String username){
        this.con=con;
        this.username=username;
    }

    public void initializeComponents() {
        //Maintenance Layout
        manageClassLayout.setPadding(new Insets(10));
        manageClassLayout.setHgap(10);
        manageClassLayout.setVgap(10);

        //controls - buttons
        Button addClassButton = new Button("Add a Class");
        Button removeClassButton = new Button("Remove a Class");

        //adding controls to the layout
        manageClassLayout.add(addClassButton,0,0);
        manageClassLayout.add(removeClassButton,0,1);

        //Align buttons in the center
        GridPane.setHalignment(addClassButton,javafx.geometry.HPos.CENTER);
        GridPane.setHalignment(removeClassButton,javafx.geometry.HPos.CENTER );   

        //event handlers
        addClassButton.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent event) {
                manageClassLayout.getChildren().clear();
                AddClass addClass = new AddClass(con,manageClassLayout,username);
                addClass.initializeComponents();
                DBLogger.log("DEBUG","ManageClass",username+" entered add class interface.",username);
            }
        });

        removeClassButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event){
                manageClassLayout.getChildren().clear();
                RemoveClass removeClass = new RemoveClass(con,manageClassLayout,username);
                removeClass.initializeComponents();
                DBLogger.log("DEBUG","ManageClass",username+" entered remove class interface.",username);
            }
        });
    }
}
