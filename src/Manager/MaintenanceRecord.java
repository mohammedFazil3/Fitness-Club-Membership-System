package Manager;
import java.sql.Date;

public class MaintenanceRecord {
    private int recordID;
    private String facilityName;
    private Date maintenanceDate;
    private String description;

    public MaintenanceRecord(int recordID, String facilityName, Date maintenanceDate, String description) {
        this.recordID = recordID;
        this.facilityName = facilityName;
        this.maintenanceDate = maintenanceDate;
        this.description = description;
    }
    
    public int getRecordID() {
        return recordID;
    }

    public void setRecordID(int recordID) {
        this.recordID = recordID;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public Date getMaintenanceDate() {
        return maintenanceDate;
    }

    public void setMaintenanceDate(Date maintenanceDate) {
        this.maintenanceDate = maintenanceDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}