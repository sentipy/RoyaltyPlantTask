package com.sentilabs.royaltyplanttask.dao.impl;

import com.sentilabs.royaltyplanttask.dao.interfaces.UserDAO;
import com.sentilabs.royaltyplanttask.entity.UserEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * Created by sentipy on 04/07/15.
 */

@Component
public class UserDAOImpl implements UserDAO {

    private static Logger logger = LogManager.getLogger(UserDAOImpl.class);
    private static RowMapper<UserEntity> userEntityRowMapper = new RowMapper<UserEntity>() {

        @Override
        public UserEntity mapRow(ResultSet resultSet, int i) throws SQLException {
            UserEntity userEntity = new UserEntity();
            userEntity.setId(resultSet.getLong("id"));
            userEntity.setUsername(resultSet.getString("name"));
            userEntity.setPassword(resultSet.getString("password"));
            return userEntity;
        }
    };

    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private NamedParameterJdbcOperations ops;

    @Override
    @Transactional(readOnly = true)
    public UserEntity getUserByName(String userName) {
        return this.getUserByName(userName, false);
    }

    @Override
    @Transactional(readOnly = true)
    public UserEntity getUserByName(String userName, boolean setPassword) {
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("username", userName);
        UserEntity userEntity = null;
        try {
            userEntity = ops.queryForObject(
                    "select c.id, c.name, c.password from clients c where c.name = :username"
                    , parameters
                    , userEntityRowMapper
            );
            if (!setPassword) {
                userEntity.setPassword(null);
            }
        }
        catch (Throwable t) {
            logger.info("unable to retrieve user = " + userName);
        }
        return userEntity;
    }

    @Override
    @Transactional(readOnly = true)
    public UserEntity getUserById(Long userId) {
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("userId", userId);
        UserEntity userEntity = null;
        try {
            userEntity = ops.queryForObject(
                    "select c.id, c.name, c.password from clients c where c.id = :userId"
                    , parameters
                    , userEntityRowMapper
            );
        }
        catch (Throwable t) {
            logger.info("unable to retrieve user with id = " + userId);
        }
        return userEntity;
    }

    @Override
    @Transactional
    public UserEntity createUser(UserEntity userEntity) {
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("name", userEntity.getUsername())
                .addValue("password", bCryptPasswordEncoder.encode(userEntity.getPassword()));
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            ops.update("insert into clients (name, password) values (:name, :password)"
                    , parameters
                    , keyHolder);
            final Map<String, Object> map = keyHolder.getKeyList().get(0);
            if (map.size() == 1) {
                return this.getUserById((Long) keyHolder.getKey());
            }
            UserEntity resultUserEntity = new UserEntity();
            resultUserEntity.setId((Long) map.get("id"));
            resultUserEntity.setUsername((String) map.get("name"));
            return resultUserEntity;
        }
        catch (Throwable t) {
            logger.info("failed to create user " + userEntity.getUsername());
        }
        return null;
    }
}
