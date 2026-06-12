package com.hse.lab4.data;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * Реализация хранилища результатов игры на SQLite через ORMLite.
 */
public class DBRecordRepository {

    private final ConnectionSource connectionSource;
    private final Dao<GameRecord, Integer> recordDao;

    public DBRecordRepository() throws SQLException {
        this.connectionSource = new JdbcConnectionSource(Constants.DATABASE_URL);
        TableUtils.createTableIfNotExists(connectionSource, GameRecord.class);
        this.recordDao = DaoManager.createDao(connectionSource, GameRecord.class);
    }

    /**
     * Сохраняет запись о результате игры.
     *
     * @param playerName имя игрока
     * @param amount     сумма выигрыша
     */
    public void saveRecord(String playerName, int amount) {
        if (playerName == null) {
            return;
        }

        try {
            recordDao.create(new GameRecord(playerName, LocalDateTime.now(), amount));
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to save game record", e);
        }
    }

    /**
     * Возвращает все записи.
     *
     * @return список результатов игры
     */
    public List<GameRecord> getTopRecords(int count) {
        try {
            var queryBuilder = recordDao.queryBuilder();
            queryBuilder.orderBy("amount", false).limit((long) count);
            return recordDao.queryForAll();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to load game records", e);
        }
    }
}
