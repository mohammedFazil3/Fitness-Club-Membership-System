package Manager;
import java.sql.Connection;

import javafx.scene.control.Alert;

public class recordsFunctions {
    private Connection con;
    private String username;
    recordsFunctions(Connection con, String username){
        this.con = con;
        this.username=username;
    }
    boolean checkFormEmpty(String selectedFacility,String selectedArea, String selectedMonth){
        if (selectedFacility==null||selectedArea==null||selectedMonth==null){
            showAlert("Form Error","All fields must be selected!!");
            return true;
        }
        return false;
    }
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
