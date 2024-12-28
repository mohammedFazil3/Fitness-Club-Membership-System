package Manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class logRecordsInterface {
    private Connection con;
    private String monthValue;
    private String loggerValue;
    private String severityValue;
    private String usernameValue;
    private String username;

    public logRecordsInterface(Connection con, String monthValue, String loggerValue, String severityValue, String usernameValue, String username) {
        this.con = con;
        this.monthValue = monthValue;
        this.loggerValue = loggerValue;
        this.severityValue = severityValue;
        this.usernameValue = usernameValue;
        this.username=username;
    }

    public void initializeComponents(GridPane contentPane) {
        contentPane.getChildren().clear(); // Clear existing components
        contentPane.getColumnConstraints().clear(); // Clear constraints
        ColumnConstraints cc = new ColumnConstraints();
        cc.setHgrow(Priority.ALWAYS); // Allow the column to grow
        contentPane.getColumnConstraints().add(cc);

        TableView<LogRecord> table = new TableView<>();
        table.setEditable(false);
        createTableColumns(table);

        table.setItems(getLogs());
        VBox vbox = new VBox(table);
        vbox.setFillWidth(true);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        contentPane.add(vbox, 0, 0,4,1);

        //Buttons
        Button backButton = new Button("Back");
        Button exitButton = new Button("Exit");
        HBox buttonsLayout = new HBox();
        buttonsLayout.setSpacing(10);
        buttonsLayout.getChildren().addAll(backButton,exitButton);

        //Adding Buttons to the layout
        contentPane.add(buttonsLayout,0,1,2,1);

        //eventHandlers
        exitButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event){
                contentPane.getChildren().clear();
            }
        });

        backButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event){
                contentPane.getChildren().clear();
                MonitorLogsInterface logs = new MonitorLogsInterface(con,username);
                logs.initializeComponents(contentPane);
            }
        });
        
    }

    private void createTableColumns(TableView<LogRecord> table) {
        TableColumn<LogRecord, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setPrefWidth(50);

        TableColumn<LogRecord, String> dateColumn = new TableColumn<>("Event Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("eventDate"));
        dateColumn.setPrefWidth(150);

        TableColumn<LogRecord, String> levelColumn = new TableColumn<>("Level");
        levelColumn.setCellValueFactory(new PropertyValueFactory<>("level"));
        levelColumn.setPrefWidth(100);

        TableColumn<LogRecord, String> loggerColumn = new TableColumn<>("Logger");
        loggerColumn.setCellValueFactory(new PropertyValueFactory<>("logger"));
        loggerColumn.setPrefWidth(100);

        TableColumn<LogRecord, String> messageColumn = new TableColumn<>("Message");
        messageColumn.setCellValueFactory(new PropertyValueFactory<>("message"));
        messageColumn.setPrefWidth(250);

        TableColumn<LogRecord, String> usernameColumn = new TableColumn<>("Username");
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        usernameColumn.setPrefWidth(100);

        table.getColumns().addAll(idColumn, dateColumn, levelColumn, loggerColumn, messageColumn, usernameColumn);
    }

    private ObservableList<LogRecord> getLogs() {
        ObservableList<LogRecord> logs = FXCollections.observableArrayList();
        String baseQuery = "SELECT id, event_date, level, logger, message, username FROM logging WHERE 1=1";
        
        if (!"Any".equals(monthValue)) {
            baseQuery += " AND MONTHNAME(event_date) = ?";
        }
        if (!"Any".equals(loggerValue)) {
            baseQuery += " AND logger = ?";
        }
        if (!"Any".equals(severityValue)) {
            baseQuery += " AND level = ?";
        }
        if (!"Any".equals(usernameValue)) {
            baseQuery += " AND username = ?";
        }

        try (PreparedStatement stmt = con.prepareStatement(baseQuery)) {
            int paramIndex = 1;
            if (!"Any".equals(monthValue)) stmt.setString(paramIndex++, monthValue);
            if (!"Any".equals(loggerValue)) stmt.setString(paramIndex++, loggerValue);
            if (!"Any".equals(severityValue)) stmt.setString(paramIndex++, severityValue);
            if (!"Any".equals(usernameValue)) stmt.setString(paramIndex++, usernameValue);
            
            ResultSet rs = stmt.executeQuery();
            common.DBLogger.log("INFO","MgrInterface",username + " accessed Logging table for SELECT successfully!!",username);
            while (rs.next()) {
                logs.add(new LogRecord(rs.getString("id"), rs.getString("event_date"), rs.getString("level"),
                                       rs.getString("logger"), rs.getString("message"), rs.getString("username")));
            }
        } catch (SQLException e) {
            common.DBLogger.log("ERROR","MgrInterface","Database Connection Unsuccessful!!",username);
        }
        return logs;
    }
}