package com.hse.lab4.data;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * Реализация поставщика вопросов на SQLite через ORMLite.
 */
public class DBQuestionRepository implements IQuestionProvider {

    private final ConnectionSource connectionSource;
    private final Dao<Question, Integer> questionDao;

    public DBQuestionRepository() throws SQLException {
        this.connectionSource = new JdbcConnectionSource(Constants.DATABASE_URL);
        TableUtils.createTableIfNotExists(connectionSource, Question.class);
        this.questionDao = DaoManager.createDao(connectionSource, Question.class);
    }

    @Override
    public Question getQuestion(int level) {
        if (level < 1 || level > 15) {
            return null;
        }

        try {
            QueryBuilder<Question, Integer> queryBuilder = questionDao.queryBuilder();
            queryBuilder.where().eq("level", level);
            queryBuilder.orderByRaw("RANDOM()").limit(1L);
            return queryBuilder.queryForFirst();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to load question from database", e);
        }
    }

    /**
     * Импортирует вопросы в базу данных.
     *
     * @param questions список вопросов для сохранения
     * @throws SQLException если возникает ошибка доступа к БД
     */
    public void importQuestions(List<Question> questions) throws SQLException {
        if (questions == null) {
            return;
        }
        clearQuestions();
        for (Question question : questions) {
            if (question == null) {
                continue;
            }
            questionDao.create(question);
        }
    }

    private void clearQuestions() throws SQLException {
        List<Question> existingQuestions = questionDao.queryForAll();
        if (!existingQuestions.isEmpty()) {
            questionDao.delete(existingQuestions);
        }
    }

}
