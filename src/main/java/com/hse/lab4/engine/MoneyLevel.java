package com.hse.lab4.engine;

import java.util.Locale;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * Перечисление уровней вознаграждения для игры "Кто хочет стать миллионером".
 * 
 * Каждый уровень содержит сумму денег и номер уровня (0-15).
 * Уровни отсортированы от 500 до 3 000 000 рублей.
 */
public enum MoneyLevel {
    LEVEL01(500, 1),
    LEVEL02(1000, 2),
    LEVEL03(2000, 3),
    LEVEL04(3000, 4),
    LEVEL05(5000, 5),
    LEVEL06(10000, 6),
    LEVEL07(15000, 7),
    LEVEL08(25000, 8),
    LEVEL09(50000, 9),
    LEVEL10(100000, 10),
    LEVEL11(200000, 11),
    LEVEL12(400000, 12),
    LEVEL13(800000, 13),
    LEVEL14(1500000, 14),
    LEVEL15(3000000, 15);

    private final int amount;
    private final int levelNumber;

    /**
     * Инициализирует уровень вознаграждения.
     * 
     * @param amount      сумма денег в рублях
     * @param levelNumber номер уровня (0-15)
     */
    MoneyLevel(int amount, int levelNumber) {
        this.amount = amount;
        this.levelNumber = levelNumber;
    }

    /**
     * Возвращает сумму вознаграждения.
     * 
     * @return сумма в рублях
     */
    public int getAmount() {
        return amount;
    }

    /**
     * Возвращает номер уровня (1-15).
     * 
     * @return номер уровня
     */
    public int getLevelNumber() {
        return levelNumber;
    }

    /**
     * Возвращает двузначный код уровня (00-15).
     * 
     * @return строка с двузначным кодом уровня
     */
    public String getLevelCode() {
        return String.format("%02d", levelNumber);
    }

    /**
     * Ищет уровень по его номеру.
     * 
     * @param level номер уровня
     * @return объект MoneyLevel или null, если уровень не найден
     */
    public static MoneyLevel fromLevel(int level) {
        for (MoneyLevel ml : values()) {
            if (ml.levelNumber == level)
                return ml;
        }
        return null;
    }

    /**
     * Возвращает следующий уровень вознаграждения.
     * 
     * @return следующий уровень или null, если текущий уровень - LEVEL15 (максимум)
     */
    public MoneyLevel getNextLevel() {
        if (levelNumber >= 15) {
            return null;
        }
        return fromLevel(levelNumber + 1);
    }

    /**
     * Возвращает массив всех уровней в убывающем порядке (от 3 000 000 к 0).
     * 
     * @return массив уровней в порядке убывания
     */
    public static MoneyLevel[] descending() {
        MoneyLevel[] vals = values();
        MoneyLevel[] desc = new MoneyLevel[vals.length];
        for (int i = 0; i < vals.length; i++) {
            desc[i] = vals[vals.length - 1 - i];
        }
        return desc;
    }

    @Override
    public String toString() {
        return formatWithSpaces(amount);
    }

    private static String formatWithSpaces(int value) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator(' ');
        DecimalFormat df = new DecimalFormat("#,###", symbols);
        df.setGroupingUsed(true);
        return df.format(value);
    }
}
