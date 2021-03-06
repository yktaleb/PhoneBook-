package com.phonebook.persistence.storage.database.dao;

import com.phonebook.persistence.exceptions.NonUniqueResultException;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

public abstract class AbstractDao {
    private final JdbcTemplate jdbcTemplate;

    public AbstractDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    protected Long executeInsertWithId(String insertQuery, Object... params) {

        jdbcTemplate.update(insertQuery, params);

        return jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID() ", Long.class);
    }

    protected int executeUpdate(String sql, Object... params) {
        return jdbcTemplate.update(sql, params);
    }

    protected <E> E findOne(String query, RowMapper<E> mapper, Object... params) {
        List<E> result = jdbcTemplate.query(query, mapper, params);
        if (result.size() > 1) {
            throw new NonUniqueResultException();
        }
        if (result.size() == 0) {
            return null;
        }
        return result.get(0);
    }

    protected <E> List<E> findMultiple(String sql, RowMapper<E> rowMapper) {
        return jdbcTemplate.query(sql, rowMapper);
    }

    protected <E> List<E> findMultiple(String sql, RowMapper<E> rowMapper, Object... params) {
        return jdbcTemplate.query(sql, rowMapper, params);
    }

    protected <E> List<E> findMultiplePage(String sql, RowMapper<E> rowMapper, Long size, Long offset) {
        return jdbcTemplate.query(sql + " LIMIT ? OFFSET ?", rowMapper, size, offset);
    }

    protected int[] batchUpdate(String sql, List<Object[]> batchArgs) {
        return jdbcTemplate.batchUpdate(sql, batchArgs);
    }
}
