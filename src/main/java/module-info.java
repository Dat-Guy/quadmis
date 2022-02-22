module com.datguy.quadmis {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.datguy.quadmis to javafx.fxml;
    exports com.datguy.quadmis;
    exports com.datguy.quadmis.data;
    opens com.datguy.quadmis.data to javafx.fxml;
}