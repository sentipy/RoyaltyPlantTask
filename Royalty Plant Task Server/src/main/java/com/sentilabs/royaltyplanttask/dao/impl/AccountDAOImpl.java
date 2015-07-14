package com.sentilabs.royaltyplanttask.dao.impl;

import com.sentilabs.royaltyplanttask.dao.interfaces.AccountDAO;
import com.sentilabs.royaltyplanttask.entity.AccountEntity;
import com.sentilabs.royaltyplanttask.entity.UserEntity;
import com.sentilabs.royaltyplanttask.repository.AccountRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.*;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by sentipy on 04/07/15.
 */

@Component
public class AccountDAOImpl implements AccountDAO {

    private static Logger logger = LogManager.getLogger(AccountDAOImpl.class);

    @Autowired
    private NamedParameterJdbcOperations ops;

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private AccountRepository accountRepository;

    private static RowMapper<AccountEntity> accountEntityRowMapper = (rs, rowNum) -> {
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setId(rs.getLong("id"));
        accountEntity.setAccount(rs.getString("account"));
        UserEntity userEntity = new UserEntity();
        userEntity.setId(rs.getLong("client"));
        accountEntity.setClient(userEntity);
        accountEntity.setBalance(rs.getBigDecimal("balance"));
        return accountEntity;
    };

    private AccountEntity mapToAccountEntity(Map<String, Object> map) {
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setId((Long) map.get("id"));
        accountEntity.setAccount((String) map.get("account"));
        UserEntity userEntity = new UserEntity();
        userEntity.setId((Long) map.get("client"));
        accountEntity.setClient(userEntity);
        accountEntity.setBalance((BigDecimal) map.get("balance"));
        return accountEntity;
    }

    @Override
    public AccountEntity getAccountById(long accountId) {
        logger.info("Retrieving account by id " + accountId);
        return accountRepository.findOne(accountId);
        /*MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("accountId", accountId);
        AccountEntity accountEntity = null;
        try {
            accountEntity = ops.queryForObject(
                    "select a.id, a.account, a.client, a.balance" +
                            " from accounts a join client c on a.client = c.id" +
                            " where id = :accountId"
                    , parameters
                    , accountEntityRowMapper);
        }
        catch (Throwable t) {
            logger.info("Unable to retrieve account by id = " + accountId,  t);
        }
        return accountEntity;*/
    }

    @Override
    public List<AccountEntity> getAccountsByClientId(long clientId) {
        logger.info("Retrieving account by clientId " + clientId);
        return accountRepository.findAccountsByClientId(clientId);
        /*MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("clientId", clientId);
        List<AccountEntity> accountEntity = null;
        try {
            accountEntity = ops.query(
                    "select a.id, a.account, a.client, a.balance" +
                            " from accounts a join client c on a.client = c.id" +
                            " where client = :clientId"
                    , parameters
                    , accountEntityRowMapper);
        }
        catch (Throwable t) {
            logger.info("Unable to retrieve account by clientId = " + clientId,  t);
        }
        return accountEntity;*/
    }

    @Override
    @Transactional
    public AccountEntity openAccount(long clientId) {
        return this.openAccount(clientId, null);
        /*MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("clientId", clientId);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        ops.update("insert into accounts (client) values (:clientId)", parameters, keyHolder);
        final List<Map<String, Object>> keyList = keyHolder.getKeyList();
        final Map<String, Object> map = keyList.get(0);
        return mapToAccountEntity(map);*/
    }

    @Override
    @Transactional
    public AccountEntity openAccount(long clientId, String account) {
        AccountEntity accountEntity = new AccountEntity();
        UserEntity userEntity = new UserEntity();
        userEntity.setId(clientId);
        accountEntity.setClient(userEntity);
        accountEntity.setAccount(StringUtils.isEmpty(account) ? null : account);
        accountEntity = accountRepository.save(accountEntity);
        if (StringUtils.isEmpty(accountEntity.getAccount())) {
            accountEntity.setAccount("40702" + StringUtils.leftPad(String.valueOf(accountEntity.getId()), 15, '0'));
        }
        return accountRepository.save(accountEntity);

        /*MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("clientId", clientId)
                .addValue("account", account);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        ops.update("insert into accounts (account, client) values (:account, :clientId)"
                , parameters
                , keyHolder);
        final List<Map<String, Object>> keyList = keyHolder.getKeyList();
        final Map<String, Object> map = keyList.get(0);
        if (map.size() == 1) {
            return this.getAccountById((Long) keyHolder.getKey());
        }
        return mapToAccountEntity(map);*/
    }

    @Override
    @Transactional(readOnly = true)
    public AccountEntity getAccountByNumber(String accountNumber) {
        return accountRepository.findAccountsByAccountNumber(accountNumber);
        /*MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("accountNumber", accountNumber);
        AccountEntity accountEntity = ops.queryForObject("select a.id, a.account, a.client, a.balance from accounts a where account = accountNumber"
                , parameters
                , accountEntityRowMapper);
        return accountEntity;*/
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountEntity> getAccountsByNumbers(List<String> accountsNumbers) {
        return accountRepository.findAccountsByAccountsNumbers(accountsNumbers);
        /*MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("accountsNumbers", accountsNumbers);
        List<AccountEntity> accountEntities = ops.query("select a.id, a.account, a.client, a.balance from accounts a where account in (:accountsNumbers)"
                , parameters
                , accountEntityRowMapper);
        return accountEntities;*/
    }

    @Override
    public void lockRowInDatabaseById(Long accountId) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createSQLQuery("select * from accounts a where a.id = :accountId for update nowait");
        query.setParameter("accountId", accountId);
        query.uniqueResult();
    }

    @Override
    public void lockRowInDatabaseByAccountNumber(String accountNumber) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createSQLQuery("select * from accounts a where a.account = :accountNumber for update nowait");
        query.setParameter("accountNumber", accountNumber);
        query.uniqueResult();
    }
}
