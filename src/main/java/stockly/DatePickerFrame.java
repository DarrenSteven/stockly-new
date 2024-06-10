package stockly;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DatePickerFrame extends Application {
    private DatePicker datePicker;

    @Override
    public void start(Stage primaryStage) {
        // Initialize DatePicker
        datePicker = new DatePicker();

        // Event handler for date selection
        datePicker.setOnAction(event -> {
            System.out.println("Selected date: " + datePicker.getValue());
        });

        // Create a layout and add the DatePicker
        VBox vbox = new VBox(datePicker);

        // Create a Scene and add the layout
        Scene scene = new Scene(vbox, 300, 200);

        // Configure and show the Stage
        primaryStage.setTitle("DatePicker Frame");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public DatePicker getDatePicker() {
        return datePicker;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
