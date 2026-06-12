module com.hse.lab4 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires ormlite.jdbc;
    requires org.xerial.sqlitejdbc;

    opens com.hse.lab4 to javafx.fxml;
    opens com.hse.lab4.data to ormlite.core;

    exports com.hse.lab4;
}
