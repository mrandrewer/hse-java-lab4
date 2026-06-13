package com.hse.lab4;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadLocalRandom;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;

import com.hse.lab4.data.AIQuestionProvider;
import com.hse.lab4.data.DBQuestionRepository;
import com.hse.lab4.data.DBRecordRepository;
import com.hse.lab4.data.IQuestionProvider;
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
    @FXML
    private Button btnHelpMistake;
    @FXML
    private Button btnHelpReplaceQuestion;

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
    private BooleanProperty helpMistakeDisabled = new SimpleBooleanProperty(false);
    private BooleanProperty helpReplaceQuestionDisabled = new SimpleBooleanProperty(false);

    private Boolean allowMistake = false;

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
                imgLogo.fitHeightProperty().bind(imagePane.heightProperty());
                this.initLevelUI();
                this.initHelpUI();
                this.initQuestionUI();
                this.initDBGame();
            }
        });
    }

    /**
     * Инициализирует таблицу вознаграждений
     */
    private void initLevelUI() {
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
    private void initHelpUI() {
        btnHelpFiftyFifty.disableProperty().bind(help50Disabled);
        btnHelpFriend.disableProperty().bind(helpFriendDisabled);
        btnHelpViewer.disableProperty().bind(helpViewersDisabled);
        btnHelpMistake.disableProperty().bind(helpMistakeDisabled);
        btnHelpReplaceQuestion.disableProperty().bind(helpReplaceQuestionDisabled);
    }

    /**
     * Инициализирует вопрос и варианты ответов на экране.
     */
    private void initQuestionUI() {
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
     * Сбрасывает игру на начало
     */
    private void resetGameUI() {
        help50Disabled.set(false);
        helpFriendDisabled.set(false);
        helpViewersDisabled.set(false);
        helpMistakeDisabled.set(false);
        helpReplaceQuestionDisabled.set(false);
        setLevelUI();
    }

    /**
     * Обновляет данные уровня
     */
    private void setLevelUI() {
        var q = game.getCurrentQuestion();
        if (q != null) {
            question.set(q.getText());
            answer1.set("A. " + q.getAnswers()[0]);
            answer2.set("B. " + q.getAnswers()[1]);
            answer3.set("C. " + q.getAnswers()[2]);
            answer4.set("D. " + q.getAnswers()[3]);
            answer1Disabled.set(false);
            answer2Disabled.set(false);
            answer3Disabled.set(false);
            answer4Disabled.set(false);
        }
        var level = game.getCurrentLevel();
        setCurrentLevel(level);
    }

    /**
     * Выбирает уровень вознаграждения в списке и прокручивает список к нему.
     * 
     * @param level уровень вознаграждения для выбора
     */
    private void setCurrentLevel(MoneyLevel level) {
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
     * Создание репозитория вопросов из текстового файла
     * 
     * @return инициализированный репозиторий с вопросами
     */
    private TxtQuestionRepository createTxtRepository() {
        var window = imagePane.getScene().getWindow();
        if (window == null) {
            return null;
        }

        FileChooser fileChooser = new FileChooser();
        String workingDirPath = System.getProperty("user.dir");
        File workingDirectory = new File(workingDirPath);
        fileChooser.setInitialDirectory(workingDirectory);
        fileChooser.setTitle("Выберите файл вопросов");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text files", "*.txt"));
        File selectedFile = fileChooser.showOpenDialog(window);
        if (selectedFile == null) {
            return null;
        }
        try {
            TxtQuestionRepository repository = new TxtQuestionRepository();
            repository.load(selectedFile.getAbsolutePath());
            if (repository.getAllQuestions().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка загрузки");
                alert.setHeaderText("Не удалось прочитать файл воспросов.");
                alert.showAndWait();
                return null;
            }
            return repository;
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка загрузки");
            alert.setHeaderText("Не удалось прочитать файл воспросов.");
            alert.showAndWait();
            return null;
        }
    }

    /**
     * Создание репозитория вопросов из локальной БД
     * 
     * @return инициализированный репозиторий с вопросами
     */
    private DBQuestionRepository createDBRepository() {
        try {
            return new DBQuestionRepository();
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка загрузки");
            alert.setHeaderText("Не удалось инициализировать базу вопросов. Переазгрузите игру.");
            alert.showAndWait();
        }
        return null;
    }

    /**
     * Создание поставщика вопросов из ИИ
     * 
     * @return инициализированный поставщик вопросов
     */
    private AIQuestionProvider createAIProvider() {
        var key = System.getenv("MILLIONAIRE_API_KEY");
        if (key == null || key.isBlank()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка подключения");
            alert.setHeaderText("Для использования ИИ установите API ключ deepseek в переменной MILLIONAIRE_API_KEY.");
            alert.showAndWait();
            return null;
        }
        return new AIQuestionProvider(
                "https://api.deepseek.com/v1",
                key,
                "deepseek-chat");
    }

    /**
     * Создание репозитория результатов игры.
     * 
     * @return инициализированный репозиторий результатов
     */
    private DBRecordRepository createRecordRepository() {
        try {
            return new DBRecordRepository();
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка загрузки");
            alert.setHeaderText("Не удалось инициализировать таблицу результатов.");
            alert.showAndWait();
        }
        return null;
    }

    /**
     * Инициализирует игру
     */
    private void initGame(IQuestionProvider questionProvider) {
        var repository = new TxtQuestionRepository();
        repository.load("Вопросы.txt");
        this.game = new Game(questionProvider);
        this.game.newGame();
        resetGameUI();
    }

    /**
     * Инициализирует игру из БД
     */
    private void initDBGame() {
        var repository = createDBRepository();
        if (repository != null) {
            initGame(repository);
        }
    }

    /**
     * Инициализирует игру из файла
     */
    private void initTxtGame() {
        var repository = createTxtRepository();
        if (repository == null) {
            initDBGame();
        }
        initGame(repository);
    }

    /**
     * Инициализирует игру с ИИ
     */
    private void initAIGame() {
        var repository = createAIProvider();
        if (repository != null) {
            initGame(repository);
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
        var answer = getButtonAnswer(button);
        if (game.checkAnswer(answer)) {
            if (game.nextLevel()) {
                allowMistake = false;
                setLevelUI();
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Win");
                alert.setHeaderText("Вы выиграли, забирайте свои 3 миллиона рублей и начинайте сначала!");
                alert.showAndWait();
                game.newGame();
                resetGameUI();
            }
        } else {
            if (allowMistake) {
                // Сбрасываем флаг и отключаем кнопку с ошибкой
                allowMistake = false;
                disableAnswerMapper[answer].set(true);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Подстраховка");
                alert.setHeaderText("Ответ неверный, у вас есть еще одна попытка на этот вопрос.");
                alert.showAndWait();
                return;
            } else {
                var correctAnswer = game.getCurrentQuestion().getCorrectAnswer();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Game Over");
                alert.setHeaderText("Вы - самое слабое звено, начинайте сначала!");
                alert.setContentText("Правильный ответ: " + getAnswerLabel(correctAnswer));
                alert.showAndWait();
                game.newGame();
                resetGameUI();
            }
        }
    }

    /**
     * Возвращает текст ответа с префиксом буквы.
     * 
     * @param answerIndex индекс ответа
     * @return текст ответа
     */
    private String getAnswerLabel(int answerIndex) {
        var currentQuestion = game == null ? null : game.getCurrentQuestion();
        if (currentQuestion == null || answerIndex < 0 || answerIndex >= currentQuestion.getAnswers().length) {
            return "";
        }

        char answerLetter = (char) ('A' + answerIndex);
        return answerLetter + ". " + currentQuestion.getAnswers()[answerIndex];
    }

    /**
     * Обработка клика по подсказке 50/50
     */
    @FXML
    private void handleFiftyFiftyClick() {
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

    /**
     * Обработка клика по подсказке Помощь зала
     */
    @FXML
    private void handleHelpViewerClick() {
        if (game == null || game.getCurrentQuestion() == null) {
            return;
        }

        int correctAnswer = game.getCurrentQuestion().getCorrectAnswer();
        List<Integer> wrongAnswers = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            if (i == correctAnswer) {
                continue;
            }
            // Учтем, что некоторые опции могут быть отключены
            if (disableAnswerMapper[i].get()) {
                continue;
            }
            wrongAnswers.add(i);
        }

        // Из неправильных выбираем одну произвольную
        var deceptiveWrongOption = wrongAnswers.get(ThreadLocalRandom.current().nextInt(wrongAnswers.size()));
        wrongAnswers.remove((Integer) deceptiveWrongOption);

        // Шанс правильного голосования 50/50
        boolean correctHasMostVotes = ThreadLocalRandom.current().nextBoolean();
        int total = 100;
        int topVote, secondVote, thirdVote;
        if (!wrongAnswers.isEmpty()) {
            topVote = ThreadLocalRandom.current().nextInt(33, 55);
            secondVote = ThreadLocalRandom.current().nextInt(20, topVote);
            thirdVote = ThreadLocalRandom.current().nextInt(10, total - topVote - secondVote);

        } else {
            topVote = ThreadLocalRandom.current().nextInt(51, 99);
            secondVote = total - topVote;
            thirdVote = 0;
        }

        var votes = new HashMap<Integer, Integer>();
        if (correctHasMostVotes) {
            votes.put(correctAnswer, topVote);
            votes.put(deceptiveWrongOption, secondVote);
        } else {
            votes.put(deceptiveWrongOption, topVote);
            votes.put(correctAnswer, secondVote);
        }
        // Остальные заполняем чтобы добить 100%
        if (!wrongAnswers.isEmpty()) {
            votes.put(wrongAnswers.get(0), thirdVote);
            votes.put(wrongAnswers.get(1), total - topVote - secondVote - thirdVote);
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            builder.append(getAnswerLabel(i))
                    .append(Objects.requireNonNullElse(votes.get(i), 0))
                    .append("%\n");
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Помощь зала");
        alert.setHeaderText("Результаты голосования зрителей:");
        alert.setContentText(builder.toString());
        alert.showAndWait();

        helpViewersDisabled.set(true);
    }

    /**
     * Обработка клика по подсказке Звонок другу.
     */
    @FXML
    private void handleHelpFriendClick() {
        if (game == null || game.getCurrentQuestion() == null) {
            return;
        }

        int correctAnswer = game.getCurrentQuestion().getCorrectAnswer();
        int suggestedAnswer = correctAnswer;

        // Наш друг угадывает с вероятностью 75%
        if (ThreadLocalRandom.current().nextDouble() >= 0.75d) {
            List<Integer> wrongAnswers = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                if (i != correctAnswer) {
                    wrongAnswers.add(i);
                }
            }
            suggestedAnswer = wrongAnswers.get(ThreadLocalRandom.current().nextInt(wrongAnswers.size()));
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Звонок другу");
        alert.setHeaderText("Друг думает, что правильный ответ:");
        alert.setContentText(getAnswerLabel(suggestedAnswer));
        alert.showAndWait();

        helpFriendDisabled.set(true);
    }

    /**
     * Обработка клика по подсказке "одна ошибка".
     */
    @FXML
    private void handleHelpMistakeClick() {
        if (game == null || game.getCurrentQuestion() == null) {
            return;
        }

        helpMistakeDisabled.set(true);
        allowMistake = true;

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Подстраховка");
        alert.setHeaderText("Теперь одна неправильная попытка на этот вопрос не приведет к поражению.");
        alert.showAndWait();
    }

    /**
     * Обработка клика по подсказке "замена вопроса".
     */
    @FXML
    private void handleHelpReplaceQuestionClick() {
        if (game == null || game.getCurrentQuestion() == null) {
            return;
        }

        helpReplaceQuestionDisabled.set(true);
        game.repalceQuestion();
        setLevelUI();
    }

    /**
     * Обработка клика по подсказке Звонок другу.
     */
    @FXML
    private void handleTakeMoneyClick() {
        if (game == null || game.getCurrentLevel() == null) {
            return;
        }
        var level = game.getCurrentLevel();
        if (level.getLevelNumber() < 2) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Денег нет");
            alert.setHeaderText("Для того чтобы забрать деньги нужно ответить хотя бы на один вопрос");
            alert.showAndWait();
        }
        // Мы имем только деньги за предыдущий вопрос, так что нужно сделать минус 1
        var realLevel = MoneyLevel.fromLevel(level.getLevelNumber() - 1);

        var repository = createRecordRepository();
        if (repository == null) {
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Сохранение результата");
        dialog.setHeaderText("Вы заработали " + realLevel.toString());
        dialog.setContentText("Введите имя:");
        dialog.getEditor().setText("");
        var result = dialog.showAndWait();
        if (result.isEmpty()) {
            return;
        }

        String playerName = result.get().trim();
        if (playerName.isEmpty()) {
            playerName = "Без имени";
        }

        repository.saveRecord(playerName, realLevel.getAmount());
        game.newGame();
        resetGameUI();
    }

    /**
     * Загружает вопросы из файла и инициализирует игру
     */
    @FXML
    private void handleUseTxtClick() {
        initTxtGame();
    }

    /**
     * Загружает вопросы из БД и инициализирует игру
     */
    @FXML
    private void handleUseDBClick() {
        initDBGame();
    }

    /**
     * Загружает вопросы из ИИ и инициализирует игру
     */
    @FXML
    private void handleUseAIClick() {
        initAIGame();
    }

    /**
     * Импортирует вопросы из txt-файла в SQLite базу.
     */
    @FXML
    private void handleImportClick() {
        try {
            var txtRepository = createTxtRepository();
            if (txtRepository == null) {
                return;
            }
            var dbQuestionRepository = createDBRepository();
            if (dbQuestionRepository == null) {
                return;
            }

            dbQuestionRepository.importQuestions(txtRepository.getAllQuestions());
            initGame(dbQuestionRepository);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Импорт завершен");
            alert.setHeaderText("Импорт завершен");
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка импорта");
            alert.setHeaderText(null);
            alert.setContentText("Не удалось выполнить импорт вопросов");
            alert.showAndWait();
            System.err.println("Ошибка импорта вопросов в SQLite: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRecordsClick() throws IOException {
        App.showModal("records", "Таблица рекордов");
    }
}
