package Manager;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import javafx.event.EventHandler;
import java.sql.*;

public class MonitorLogsInterface {
    private Connection con;
    private HBox buttonsLayout;
    private String username;

    public MonitorLogsInterface(Connection con, String username) {
        this.con = con;
        this.username=username;
    }

    public void initializeComponents(GridPane contentPane) {
        contentPane.getChildren().clear();
        MonitorLogsFunctions functions = new MonitorLogsFunctions(con,username);

        Label monthLabel = new Label("Enter Month:");
        ComboBox<String> monthCombo = new ComboBox<>();
        monthCombo.setOnMouseClicked(event -> {
            functions.getMonthItems(monthCombo);
        });

        Label loggerLabel = new Label("Choose Logger/Area (Class):");
        ComboBox<String> loggerCombo = new ComboBox<>();
        loggerCombo.setOnMouseClicked(event ->{
            functions.getLoggerItems(loggerCombo,monthCombo.getValue());
        });

        Label severityLabel = new Label("Choose Severity Level:");
        ComboBox<String> severityCombo = new ComboBox<>();
        severityCombo.setOnMouseClicked(event ->{
            functions.getSeverityItems(severityCombo, monthCombo.getValue(),loggerCombo.getValue());
        });


        Label usernameLabel = new Label("Choose Username:");
        ComboBox<String> usernameCombo = new ComboBox<>();
        usernameCombo.setOnMouseClicked(event ->{
            functions.getUserItems(usernameCombo, monthCombo.getValue(),loggerCombo.getValue(),severityCombo.getValue());
        });
        
        //Buttons
        Button viewLogsButton = new Button("View Logs");
        Button exitButton = new Button("Exit");
        buttonsLayout = new HBox();
        buttonsLayout.setSpacing(10);
        buttonsLayout.getChildren().addAll(viewLogsButton,exitButton);

        // Add components to the grid
        contentPane.add(monthLabel, 0, 0);
        contentPane.add(monthCombo, 1, 0);
        contentPane.add(loggerLabel, 0, 1);
        contentPane.add(loggerCombo, 1, 1);        
        contentPane.add(severityLabel, 0, 2);
        contentPane.add(severityCombo, 1, 2);
        contentPane.add(usernameLabel, 0, 3);
        contentPane.add(usernameCombo, 1, 3);
        contentPane.add(buttonsLayout,0,4,4,1);

        //eventHandlers
        exitButton.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent event){
                contentPane.getChildren().clear();

            }
        });
                  
        viewLogsButton.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent event){
                if(monthCombo.getValue()==null||severityCombo.getValue()==null||loggerCombo.getValue()==null||usernameCombo.getValue()==null){
                    functions.showAlert("Form Error", "Choose all the options!!");
                    return;
                }
                logRecordsInterface records = new logRecordsInterface(con, monthCombo.getValue(),loggerCombo.getValue(),severityCombo.getValue(),usernameCombo.getValue(),username);
                records.initializeComponents(contentPane);
                common.DBLogger.log("INFO","MgrInterface",username+" accessed log records interface.",username);
            }
        });
    }
}
