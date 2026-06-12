package com.hse.lab4.data;

import java.util.Arrays;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Модель вопроса для викторины "Кто хочет стать миллионером".
 * Содержит текст вопроса, варианты ответов, правильный ответ и уровень
 * сложности.
 */
@DatabaseTable(tableName = "questions")
public class Question {
    /**
     * Идентификатор записи.
     */
    @DatabaseField(generatedId = true)
    private int id;

    /**
     * Текст вопроса.
     */
    @DatabaseField(canBeNull = false, index = true)
    private String text;

    /**
     * Первый вариант ответа.
     */
    @DatabaseField(canBeNull = false)
    private String answer1;

    /**
     * Второй вариант ответа.
     */
    @DatabaseField(canBeNull = false)
    private String answer2;

    /**
     * Третий вариант ответа.
     */
    @DatabaseField(canBeNull = false)
    private String answer3;

    /**
     * Четвёртый вариант ответа.
     */
    @DatabaseField(canBeNull = false)
    private String answer4;

    /**
     * Правильный ответ на вопрос.
     */
    @DatabaseField(canBeNull = false)
    private int correctAnswer;

    /**
     * Уровень сложности вопроса (0-15).
     */
    @DatabaseField(canBeNull = false, index = true)
    private int level;

    /**
     * Пустой конструктор для ORMLite.
     */
    public Question() {
    }

    /**
     * Конструктор для инициализации вопроса из отдельных полей.
     *
     * @param text          текст вопроса
     * @param answers       массив четырёх вариантов ответов
     * @param correctAnswer индекс правильного ответа (0-3)
     * @param level         уровень сложности (0-15)
     */
    public Question(String text, String[] answers, int correctAnswer, int level) {
        this.text = text;
        this.setAnswers(answers);
        this.correctAnswer = correctAnswer;
        this.level = level;
    }

    /**
     * Конструктор для инициализации вопроса из массива строк.
     * Формат массива:
     * s[0] - текст вопроса
     * s[1-4] - варианты ответов (индексы 0-3 в массиве answers)
     * s[5] - правильный ответ (индекс 0-3)
     * s[6] - уровень сложности (строка, преобразуется в int)
     *
     * @param s массив строк с данными вопроса (должен содержать ровно 7 элементов)
     */
    public Question(String[] s) {
        this.text = s[0];
        this.setAnswers(Arrays.copyOfRange(s, 1, 5));
        this.correctAnswer = Integer.parseInt(s[5]);
        this.level = Integer.parseInt(s[6]);
    }

    /**
     * Возвращает текст вопроса.
     *
     * @return текст вопроса
     */
    public String getText() {
        return text;
    }

    /**
     * Устанавливает текст вопроса
     * 
     * @param text текст вопроса
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Возвращает массив вариантов ответов.
     *
     * @return массив четырёх вариантов ответов
     */
    public String[] getAnswers() {
        return new String[] { answer1, answer2, answer3, answer4 };
    }

    /**
     * Устанавливает массив вариантов ответов
     * 
     * @param answers массив вариантов ответов
     */
    public void setAnswers(String[] answers) {
        if (answers != null && answers.length == 4) {
            this.answer1 = answers[0];
            this.answer2 = answers[1];
            this.answer3 = answers[2];
            this.answer4 = answers[3];
        } else {
            throw new IllegalArgumentException("There must be exactly 4 answers");
        }
    }

    /**
     * Возвращает индекс правильного ответа.
     *
     * @return индекс правильного ответа (0-3)
     */
    public int getCorrectAnswer() {
        return correctAnswer;
    }

    /**
     * Устанаваливает индекс правильного ответа.
     *
     * @param correctAnswer индекс правильного ответа (0-3)
     */
    public void setCorrectAnswer(int correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    /**
     * Возвращает уровень сложности вопроса.
     *
     * @return уровень сложности (0-15)
     */
    public int getLevel() {
        return level;
    }

    /**
     * Устанаваливает уровень сложности вопроса.
     *
     * @param level уровень сложности (0-15)
     */
    public void setLevel(int level) {
        this.level = level;
    }
}
