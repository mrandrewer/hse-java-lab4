package com.hse.lab4;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadLocalRandom;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.control.ListView;
import com.hse.lab4.data.TxtQuestionRepository;
import com.hse.lab4.engine.Game;
import com.hse.lab4.engine.MoneyLevel;

import javafx.beans.property.StringProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
    @FXML
    private Button btnHelpFiftyFifty;
    @FXML
    private Button btnHelpFriend;
    @FXML
    private Button btnHelpViewer;

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
    private BooleanProperty answer1Disabled = new SimpleBooleanProperty(false);
    private BooleanProperty answer2Disabled = new SimpleBooleanProperty(false);
    private BooleanProperty answer3Disabled = new SimpleBooleanProperty(false);
    private BooleanProperty answer4Disabled = new SimpleBooleanProperty(false);
    private BooleanProperty help50Disabled = new SimpleBooleanProperty(false);
    private BooleanProperty helpFriendDisabled = new SimpleBooleanProperty(false);
    private BooleanProperty helpViewersDisabled = new SimpleBooleanProperty(false);

    // Сопоставление номера ответа и свойства для его отключения
    private BooleanProperty[] disableAnswerMapper = {
            answer1Disabled,
            answer2Disabled,
            answer3Disabled,
            answer4Disabled
    };

    /**
     * Инициализирует контроллер при загрузке сцены.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> {
            if (imgLogo != null && imagePane != null) {
                imgLogo.fitWidthProperty().bind(imagePane.widthProperty());
                this.initLevel();
                this.initHelp();
                this.initQuestion();
                this.initGame();
            }
        });
    }

    /**
     * Инициализирует таблицу вознаграждений
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
     * Инициализирует кнопки подсказок
     */
    private void initHelp() {
        btnHelpFiftyFifty.disableProperty().bind(help50Disabled);
        btnHelpFriend.disableProperty().bind(helpFriendDisabled);
        btnHelpViewer.disableProperty().bind(helpViewersDisabled);
    }

    /**
     * Инициализирует вопрос и варианты ответов на экране.
     */
    private void initQuestion() {
        lblQuestion.textProperty().bind(question);
        btnAnswer1.textProperty().bind(answer1);
        btnAnswer1.disableProperty().bind(answer1Disabled);
        btnAnswer2.textProperty().bind(answer2);
        btnAnswer2.disableProperty().bind(answer2Disabled);
        btnAnswer3.textProperty().bind(answer3);
        btnAnswer3.disableProperty().bind(answer3Disabled);
        btnAnswer4.textProperty().bind(answer4);
        btnAnswer4.disableProperty().bind(answer4Disabled);
    }

    /**
     * Инициализирует игру
     */
    private void initGame() {
        var repository = new TxtQuestionRepository();
        repository.load("Вопросы.txt");
        this.game = new Game(repository);
        this.game.newGame();
        resetGame();
    }

    /**
     * Сбрасывает игру на начало
     */
    private void resetGame() {
        help50Disabled.set(false);
        helpFriendDisabled.set(false);
        helpViewersDisabled.set(false);
        setLevelUI();
    }

    /**
     * Обновляет данные уровня
     */
    private void setLevelUI() {
        var q = game.getCurrentQuestion();
        if (q != null) {
            question.set(q.getText());
            answer1.set(q.getAnswers()[0]);
            answer2.set(q.getAnswers()[1]);
            answer3.set(q.getAnswers()[2]);
            answer4.set(q.getAnswers()[3]);
            answer1Disabled.set(false);
            answer2Disabled.set(false);
            answer3Disabled.set(false);
            answer4Disabled.set(false);
        }
        var level = game.getCurrentLevel();
        selectMoney(level);
    }

    /**
     * Выбирает уровень вознаграждения в списке и прокручивает список к нему.
     * 
     * @param level уровень вознаграждения для выбора
     */
    private void selectMoney(MoneyLevel level) {
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

    /**
     * Получает номер ответа по кнопке ответа
     * 
     * @param button Кнопка ответа
     * @return номер ответа
     */
    private int getButtonAnswer(Button button) {
        if (button == null) {
            return -1;
        }
        Object userData = button.getUserData();
        try {
            return Integer.parseInt(String.valueOf(userData));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Обработка клика по кнопке ответа
     */
    @FXML
    private void handleAnswerClick(ActionEvent event) {
        if (game == null || event == null || !(event.getSource() instanceof Button button)) {
            return;
        }

        if (game.checkAnswer(getButtonAnswer(button))) {
            if (game.nextLevel()) {
                setLevelUI();
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Win");
                alert.setHeaderText("Вы выиграли, забирайте свои 3 миллиона рублей и начинайте сначала!");
                alert.showAndWait();
                game.newGame();
                resetGame();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText("Вы - самое слабое звено, начинайте сначала!");
            alert.showAndWait();
            game.newGame();
            resetGame();
        }
    }

    /**
     * Обработка клика по подсказке 50/50
     */
    @FXML
    private void handleFiftyFiftyClick(ActionEvent event) {
        if (game == null || game.getCurrentQuestion() == null) {
            return;
        }

        int correctAnswer = game.getCurrentQuestion().getCorrectAnswer();
        List<Integer> wrongAnswers = new ArrayList<>(List.of(0, 1, 2, 3));
        wrongAnswers.remove((Integer) correctAnswer);
        // Из неправильных оставляем доступной одну произвольную
        wrongAnswers.remove(ThreadLocalRandom.current().nextInt(wrongAnswers.size()));
        for (int answer : wrongAnswers) {
            disableAnswerMapper[answer].set(true);
        }
        help50Disabled.set(true);
    }
}
