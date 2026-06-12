package com.hse.lab4.data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Запись о результате игры.
 */
@DatabaseTable(tableName = "records")
public class GameRecord {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false)
    private String playerName;

    // SQLite не поддерживает даты
    @DatabaseField(canBeNull = false)
    private String playedAt;

    @DatabaseField(canBeNull = false, index = true)
    private int amount;

    public GameRecord() {
    }

    public GameRecord(String playerName, LocalDateTime playedAt, int amount) {
        this.playerName = playerName;
        this.setPlayedAt(playedAt);
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public LocalDateTime getPlayedAt() {
        return LocalDateTime.parse(playedAt);
    }

    public void setPlayedAt(LocalDateTime playedAt) {
        this.playedAt = playedAt.toString();
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedDate = getPlayedAt().format(formatter);
        return String.format("%,d | %s | %s",
                amount,
                formattedDate,
                playerName);
    }
}
