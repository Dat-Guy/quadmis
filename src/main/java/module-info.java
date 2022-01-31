module com.datguy.quadmis {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.datguy.quadmis to javafx.fxml;
    exports com.datguy.quadmis;
}