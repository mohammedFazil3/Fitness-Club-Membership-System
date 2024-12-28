package Manager;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

public class recordsInterface {
    private Connection con;
    private HBox buttonsLayout;
    private String selectedFacility;
    private String selectedArea;
    private String selectedMonth;
    private String username;

    recordsInterface(Connection con,String selectedFacility,String selectedArea,String selectedMonth, String username){
        this.con = con;
        this.selectedFacility = selectedFacility;
        this.selectedArea = selectedArea;
        this.selectedMonth = selectedMonth;
        this.username=username;
    }

    public void initializeComponents(GridPane layout){
        layout.getChildren().clear(); // Clear existing components
        layout.getColumnConstraints().clear(); // Clear constraints
        ColumnConstraints cc = new ColumnConstraints();
        cc.setHgrow(Priority.ALWAYS); // Allow the column to grow
        layout.getColumnConstraints().add(cc);                

        TableView<MaintenanceRecord> table = new TableView<>();
        table.getItems().addAll(fetchMaintenanceRecords());

        TableColumn<MaintenanceRecord, Integer> recordIDCol = new TableColumn<>("RecordID");
        recordIDCol.setCellValueFactory(new PropertyValueFactory<>("recordID"));

        TableColumn<MaintenanceRecord, Integer> facilityIDCol = new TableColumn<>("FacilityName");
        facilityIDCol.setCellValueFactory(new PropertyValueFactory<>("facilityName"));

        TableColumn<MaintenanceRecord, Date> dateCol = new TableColumn<>("MaintenanceDate");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("maintenanceDate"));

        TableColumn<MaintenanceRecord, Integer> descriptionIDCol = new TableColumn<>("Description");
        descriptionIDCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        table.getColumns().addAll(recordIDCol, facilityIDCol, dateCol, descriptionIDCol);

        table.setMinWidth(600);
        table.setMaxWidth(1200);
        

        table.setPrefHeight((fetchMaintenanceRecords().size()*24+26));
        VBox vbox = new VBox(table);
        vbox.setFillWidth(true);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        layout.add(vbox, 0, 0,4,1);

        //Buttons
        Button backButton = new Button("Back");
        Button exitButton = new Button("Exit");
        buttonsLayout = new HBox();
        buttonsLayout.setSpacing(10);
        buttonsLayout.getChildren().addAll(backButton,exitButton);

        //Adding Buttons to the layout
        layout.add(buttonsLayout,0,1,2,1);

        //eventHandlers
        exitButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event){
                layout.getChildren().clear();
                maintenanceInterface maintenance = new maintenanceInterface(con,username);
                maintenance.initializeComponents();
                layout.add(maintenance.maintenanceLayout,0,0);
            }
        });

        backButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event){
                layout.getChildren().clear();
                maintenanceRecords records = new maintenanceRecords(con,layout,username);
                records.initializeComponents();
            }
        });

    }

    public List<MaintenanceRecord> fetchMaintenanceRecords() {
        List<MaintenanceRecord> records = new ArrayList<>();
        try {
            String query="SELECT RecordID,FacilityName,maintenance_records.MaintenanceDate,Description FROM maintenance_records INNER JOIN gymfacilities ON maintenance_records.FacilityID = gymfacilities.FacilityID INNER JOIN maintenance_descriptions ON maintenance_records.DescriptionID = maintenance_descriptions.DescriptionID WHERE FacilityName = ? AND Description = ? AND MONTHNAME(MaintenanceDate) = ?;";
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1,selectedFacility);
            statement.setString(2, selectedArea);
            statement.setString(3,selectedMonth);
            ResultSet rs = statement.executeQuery();
            common.DBLogger.log("INFO","MgrInterface",username+ " fetched maintenance records successfully!!",username);

            while (rs.next()) {
                int recordID = rs.getInt("RecordID");
                String facilityName = rs.getString("FacilityName");
                Date maintenanceDate = rs.getDate("MaintenanceDate");
                String description = rs.getString("Description");
                records.add(new MaintenanceRecord(recordID, facilityName, maintenanceDate, description));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }
}
