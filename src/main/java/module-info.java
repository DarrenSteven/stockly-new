module stockly {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;

    opens stockly to javafx.fxml;
    exports stockly;
}
