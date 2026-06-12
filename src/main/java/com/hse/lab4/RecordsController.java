package com.hse.lab4;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.hse.lab4.data.DBRecordRepository;
import com.hse.lab4.data.GameRecord;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;

public class RecordsController implements Initializable {
    @FXML
    private ListView<String> lvRecords;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            this.loadRecords();
        });
    }

    private void loadRecords() {
        if (lvRecords == null) {
            return;
        }

        try {
            DBRecordRepository repository = new DBRecordRepository();
            List<GameRecord> records = repository.getTopRecords(100);
            lvRecords.setItems(FXCollections.observableArrayList(
                    records.stream()
                            .map(record -> record.toString())
                            .collect(Collectors.toList())));
        } catch (SQLException | IllegalStateException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка загрузки");
            alert.setHeaderText("Не удалось загрузить таблицу рекордов.");
            alert.showAndWait();
        }
    }

    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }
}