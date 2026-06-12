module com.hse.lab4 {
    // JavaFX
    requires javafx.controls;
    requires javafx.fxml;

    // SQL and ORMLite
    requires java.sql;
    requires ormlite.jdbc;
    requires org.xerial.sqlitejdbc;

    // Logging (required by Spring AI)
    requires org.slf4j;

    // Jackson for JSON parsing
    requires com.fasterxml.jackson.databind;

    // OpenAI SDK (automatic module name)
    requires openai.java.client.okhttp;
    requires openai.java.core;

    // Open packages for reflection
    opens com.hse.lab4 to javafx.fxml;
    opens com.hse.lab4.data to ormlite.core, ormlite.jdbc, com.fasterxml.jackson.databind;

    exports com.hse.lab4;
}