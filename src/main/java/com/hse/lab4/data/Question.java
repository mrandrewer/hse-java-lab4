package com.hse.lab4.data;

/**
 * Модель вопроса для викторины "Кто хочет стать миллионером".
 * Содержит текст вопроса, варианты ответов, правильный ответ и уровень
 * сложности.
 */
public class Question {

    /**
     * Текст вопроса.
     */
    private String text;

    /**
     * Массив четырёх вариантов ответа.
     */
    private String[] answers = new String[4];

    /**
     * Правильный ответ на вопрос.
     */
    private int correctAnswer;

    /**
     * Уровень сложности вопроса (0-15).
     */
    private int level;

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
        if (answers != null && answers.length == 4) {
            for (int i = 0; i < 4; i++) {
                this.answers[i] = answers[i];
            }
        }
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
        for (int i = 0; i < 4; i++) {
            this.answers[i] = s[i + 1];
        }
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
     * Возвращает массив вариантов ответов.
     *
     * @return массив четырёх вариантов ответов
     */
    public String[] getAnswers() {
        return answers;
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
     * Возвращает уровень сложности вопроса.
     *
     * @return уровень сложности (0-15)
     */
    public int getLevel() {
        return level;
    }
}
