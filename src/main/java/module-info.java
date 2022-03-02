module com.datguy.quadmis {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires org.jetbrains.annotations;


    opens com.datguy.quadmis to javafx.fxml;
    exports com.datguy.quadmis;
    exports com.datguy.quadmis.data;
    opens com.datguy.quadmis.data to javafx.fxml;
    exports com.datguy.quadmis.middlemen;
    opens com.datguy.quadmis.middlemen to javafx.fxml;
    exports com.datguy.quadmis.application;
    opens com.datguy.quadmis.application to javafx.fxml;
}