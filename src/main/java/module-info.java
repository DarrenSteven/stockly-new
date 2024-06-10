module stockly {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires jdatepicker;

    opens stockly to javafx.fxml;
    exports stockly;
}
