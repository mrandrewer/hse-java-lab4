package com.hse.lab4.engine;

import com.hse.lab4.data.IQuestionProvider;
import com.hse.lab4.data.Question;

/**
 * Класс игры "Кто хочет стать миллионером".
 * 
 * Хранит все вопросы и управляет их загрузкой и доступом.
 */
public class Game {

    /**
     * Текущий вопрос
     */
    private Question currentQuestion;

    /**
     * Текущий уровень
     */
    private MoneyLevel currentLevel;

    /**
     * Источник вопросов
     */
    private IQuestionProvider questionProvider;

    /**
     * Конструктор инициализирует источник вопросов
     * 
     * @param questionProvider источник вопросов для игры
     */
    public Game(IQuestionProvider questionProvider) {
        this.questionProvider = questionProvider;
    }

    /**
     * Возвращает текущий уровень
     */
    public MoneyLevel getCurrentLevel() {
        return currentLevel;
    }

    /**
     * Возвращает текущий вопрос.
     */
    public Question getCurrentQuestion() {
        return currentQuestion;
    }

    /**
     * Инициализирует новую игру, сбрасывая все значения к начальному состоянию.
     * Устанавливает уровень денег на LEVEL01 и получает текущий вопрос
     */
    public void newGame() {
        this.currentLevel = MoneyLevel.LEVEL01;
        this.currentQuestion = questionProvider.getQuestion(currentLevel.getLevelNumber());
    }

    /**
     * Переходит к следующему вопросу.
     * 
     * @return true если есть следующий вопрос, false если достигнут конец списка
     */
    public boolean nextLevel() {
        MoneyLevel nextLevel = currentLevel.getNextLevel();
        if (nextLevel != null) {
            this.currentLevel = nextLevel;
            this.currentQuestion = questionProvider.getQuestion(currentLevel.getLevelNumber());
            return true;
        }
        return false;
    }

    /**
     * Проверяет, является ли выбранный ответ правильным.
     * 
     * @param answerIndex индекс выбранного ответа (0-3)
     * @return true если ответ правильный, false иначе
     */
    public boolean checkAnswer(int answerIndex) {
        if (currentQuestion == null) {
            return false;
        }
        return answerIndex >= 0 && answerIndex <= 3 && currentQuestion.getCorrectAnswer() == answerIndex;
    }

    /**
     * Замена вопроса текущего уровня
     */
    public Question repalceQuestion() {
        this.currentQuestion = questionProvider.getQuestion(currentLevel.getLevelNumber());
        return this.currentQuestion;
    }

}
