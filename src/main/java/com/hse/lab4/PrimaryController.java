package com.hse.lab4;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.control.ListView;
import com.hse.lab4.data.TxtQuestionRepository;
import com.hse.lab4.engine.Game;
import com.hse.lab4.engine.MoneyLevel;

import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;

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
    @FXML
    private Label lblQuestion;
    @FXML
    private Button btnAnswer1;
    @FXML
    private Button btnAnswer2;
    @FXML
    private Button btnAnswer3;
    @FXML
    private Button btnAnswer4;

    /**
     * Переключается на вторичный экран.
     * 
     * @throws IOException если не удается загрузить FXML файл
     */
    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }

    private Game game;

    private StringProperty question = new SimpleStringProperty("Текст вопроса?");
    private StringProperty answer1 = new SimpleStringProperty("Ответ 1");
    private StringProperty answer2 = new SimpleStringProperty("Ответ 2");
    private StringProperty answer3 = new SimpleStringProperty("Ответ 3");
    private StringProperty answer4 = new SimpleStringProperty("Ответ 4");

    private void initGame() {
        var repository = new TxtQuestionRepository();
        repository.load("Вопросы.txt");
        this.game = new Game(repository);
        this.game.newGame();
        setLevelUI();
    }

    private void setLevelUI() {
        var q = game.getCurrentQuestion();
        if (q != null) {
            question.set(q.getText());
            answer1.set(q.getAnswers()[0]);
            answer2.set(q.getAnswers()[1]);
            answer3.set(q.getAnswers()[2]);
            answer4.set(q.getAnswers()[3]);
        }
        var level = game.getCurrentLevel();
        selectMoney(level);
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
        }
    }

    /**
     * Инициализирует вопрос и варианты ответов на экране.
     * Устанавливает привязки между свойствами вопроса/ответов и текстом
     * соответствующих элементов управления.
     */
    private void initQuestion() {
        lblQuestion.textProperty().bind(question);
        btnAnswer1.textProperty().bind(answer1);
        btnAnswer2.textProperty().bind(answer2);
        btnAnswer3.textProperty().bind(answer3);
        btnAnswer4.textProperty().bind(answer4);
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
                this.initQuestion();
                this.initGame();
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
