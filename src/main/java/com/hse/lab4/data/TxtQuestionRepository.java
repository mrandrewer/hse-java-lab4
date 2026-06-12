package com.hse.lab4.data;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Реализация репозитория для загрузки вопросов из текстового файла.
 * Ожидает файл в формате:
 * "вопрос\tответ1\tответ2\tответ3\tответ4\tправильный_ответ\tуровень"
 * где правильный_ответ - это индекс (1-4), который преобразуется в (0-3).
 */
public class TxtQuestionRepository implements IQuestionProvider {

    /**
     * Путь к файлу с вопросами в ресурсах.
     */
    private static final String QUESTIONS_FILE = "Вопросы.txt";

    /**
     * Генератор случайных чисел для выбора случайного вопроса.
     */
    protected Random random;

    /**
     * Список всех загруженных вопросов, сгруппированных по уровню.
     * questionsByLevel[i] содержит список вопросов уровня i (0-15).
     */
    private List<Question>[] questionsByLevel;

    /**
     * Конструктор инициализирует репозиторий и загружает вопросы из файла.
     */
    public TxtQuestionRepository() {
        this.questionsByLevel = new ArrayList[15]; // Уровни 0-15
        for (int i = 0; i < 15; i++) {
            this.questionsByLevel[i] = new ArrayList<>();
        }
        this.random = new Random();
    }

    /**
     * Загружает вопросы из текстового файла.
     */
    public void load(String filePath) {
        Path currentFolder = Paths.get("").toAbsolutePath();
        Path fullPath = Paths.get(filePath).toAbsolutePath();
        System.out.println("Текущая папка: " + currentFolder);
        System.out.println("Полный путь к файлу: " + fullPath);
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(filePath)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                parseAndAddQuestion(line);
            }
        } catch (Exception e) {
            System.err.println("Ошибка при загрузке вопросов: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Парсит строку и добавляет вопрос в репозиторий.
     *
     * @param line строка с данными вопроса
     */
    private void parseAndAddQuestion(String line) {
        if (line == null || line.trim().isEmpty()) {
            return;
        }

        String[] parts = line.split("\t");
        if (parts.length != 7) {
            System.err.println("Неверный формат строки (ожидается 7 полей): " + line);
            return;
        }

        try {
            String text = parts[0].trim();
            String[] answers = new String[4];
            answers[0] = parts[1].trim();
            answers[1] = parts[2].trim();
            answers[2] = parts[3].trim();
            answers[3] = parts[4].trim();

            // Конвертируем 1-based индекс в 0-based
            int correctAnswer = Integer.parseInt(parts[5].trim()) - 1;
            int level = Integer.parseInt(parts[6].trim());

            // Проверяем корректность
            if (correctAnswer < 0 || correctAnswer > 3) {
                System.err.println("Некорректный индекс ответа: " + (correctAnswer + 1));
                return;
            }

            if (level < 0 || level > 15) {
                System.err.println("Некорректный уровень: " + level);
                return;
            }

            Question question = new Question(text, answers, correctAnswer, level);
            addQuestion(question);
        } catch (NumberFormatException e) {
            System.err.println("Ошибка парсинга чисел в строке: " + line);
        }
    }

    /**
     * Добавляет вопрос в список вопросов соответствующего уровня.
     *
     * @param question вопрос для добавления
     */
    private void addQuestion(Question question) {
        int level = question.getLevel() - 1;
        if (level >= 0 && level < questionsByLevel.length) {
            questionsByLevel[level].add(question);
        } else {
            System.err.println("Попытка добавить вопрос с некорректным уровнем: " + level);
        }
    }

    /**
     * Возвращает случайный вопрос для указанного уровня сложности.
     *
     * @param level уровень сложности (0-15)
     * @return случайный вопрос для уровня или null, если вопросов нет
     */
    @Override
    public Question getQuestion(int level) {
        if (level < 1 || level > 15) {
            return null;
        }

        List<Question> questionsForLevel = questionsByLevel[level - 1];
        if (questionsForLevel.isEmpty()) {
            return null;
        }

        int randomIndex = random.nextInt(questionsForLevel.size());
        return questionsForLevel.get(randomIndex);
    }
}
