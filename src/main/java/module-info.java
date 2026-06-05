module com.hse.lab4 {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.hse.lab4 to javafx.fxml;
    exports com.hse.lab4;
}
