package com.hse.lab4;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import com.hse.lab4.MoneyLevel;
import javafx.scene.control.ListView;
import javafx.collections.FXCollections;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;

/**
 * Контроллер главного экрана игры "Кто хочет стать миллионером".
 * 
 * Управляет отображением логотипа, вопроса, вариантов ответов и таблицы
 * вознаграждений.
 */
public class PrimaryController implements Initializable {

    @FXML
    private StackPane imagePane;

    @FXML
    private ImageView imgLogo;
    @FXML
    private ListView<MoneyLevel> lvMoney;

    /**
     * Переключается на вторичный экран.
     * 
     * @throws IOException если не удается загрузить FXML файл
     */
    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }

    /**
     * Инициализирует таблицу вознаграждений.
     * 
     * Заполняет список уровней в убывающем порядке (от максимальной суммы к
     * минимальной).
     * Устанавливает кастомный рендерер ячеек для правого выравнивания текста.
     */
    private void initLevel() {
        if (lvMoney != null) {
            lvMoney.setItems(FXCollections.observableArrayList(MoneyLevel.descending()));
            lvMoney.setCellFactory(list -> new javafx.scene.control.ListCell<>() {
                @Override
                protected void updateItem(MoneyLevel item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.toString());
                        setStyle("-fx-alignment: CENTER-RIGHT; -fx-padding: 4 8 4 8;");
                    }
                }
            });
            this.selectMoney(MoneyLevel.LEVEL01);
        }
    }

    /**
     * Инициализирует контроллер при загрузке сцены.
     * 
     * Привязывает размер изображения к размеру контейнера и инициализирует таблицу
     * вознаграждений.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> {
            if (imgLogo != null && imagePane != null) {
                imgLogo.fitWidthProperty().bind(imagePane.widthProperty());
                this.initLevel();
            }
        });
    }

    /**
     * Выбирает уровень вознаграждения в списке и прокручивает список к нему.
     * 
     * @param level уровень вознаграждения для выбора
     */
    public void selectMoney(MoneyLevel level) {
        if (lvMoney == null || level == null)
            return;
        Platform.runLater(() -> {
            int idx = lvMoney.getItems().indexOf(level);
            if (idx >= 0) {
                lvMoney.getSelectionModel().select(idx);
                lvMoney.scrollTo(idx);
            }
        });
    }
}
