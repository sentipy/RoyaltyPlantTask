package com.sentilabs.royaltyplanttask.service.impl;

import com.sentilabs.royaltyplanttask.dao.interfaces.AccountDAO;
import com.sentilabs.royaltyplanttask.entity.AccountEntity;
import com.sentilabs.royaltyplanttask.entity.DocumentEntity;
import com.sentilabs.royaltyplanttask.request.BorrowMoneyRequest;
import com.sentilabs.royaltyplanttask.request.TransferMoneyRequest;
import com.sentilabs.royaltyplanttask.service.interfaces.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by AMachekhin on 08.07.2015.
 */

@Component
public class DocumentServiceImpl implements DocumentService {

    @Autowired
    private NamedParameterJdbcOperations ops;

    @Autowired
    private AccountDAO accountDAO;

    private static RowMapper<DocumentEntity> documentEntityRowMapper = (resultSet, i) -> {
        DocumentEntity documentEntity = new DocumentEntity();
        documentEntity.setDocumentId(resultSet.getLong("id"));
        documentEntity.setFromAccount(resultSet.getString("fromAccount"));
        documentEntity.setFromAccountId(resultSet.getLong("fromAccountId"));
        documentEntity.setToAccount(resultSet.getString("toAccount"));
        documentEntity.setToAccountId(resultSet.getLong("toAccountId"));
        documentEntity.setSum(resultSet.getBigDecimal("sum"));
        documentEntity.setStatus(resultSet.getString("status"));
        return documentEntity;
    };

    private DocumentEntity mapToDocumentEntity(Map<String, Object> map) {
        DocumentEntity documentEntity = new DocumentEntity();
        documentEntity.setDocumentId((Long) map.get("id"));
        documentEntity.setFromAccount((String) map.get("fromAccount"));
        documentEntity.setToAccount((String) map.get("toAccount"));
        documentEntity.setSum((BigDecimal) map.get("sum"));
        documentEntity.setStatus((String) map.get("status"));
        return documentEntity;
    }

    private DocumentEntity createDocument(DocumentEntity documentEntity) throws Exception {
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("fromAccount", documentEntity.getFromAccountId())
                .addValue("toAccount", documentEntity.getToAccountId())
                .addValue("sum", documentEntity.getSum());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        ops.update("insert into documents (acc_dt, acc_kt, doc_sum) values (:fromAccount, :toAccount, :sum)", parameters, keyHolder);
        Map<String, Object> map = keyHolder.getKeyList().get(0);
        if (map.size() == 1) {
            return this.getDocumentById((Long) keyHolder.getKey());
        }
        return mapToDocumentEntity(map);
    }

    @Override
    @Transactional
    public DocumentEntity borrow(BorrowMoneyRequest borrowMoneyRequest, Long clientId) throws Exception {
        String bankLoanAccountNumber = "20202000000000000000"; // TODO remove this hard code!
        List<String> accountsNumbers = new ArrayList<>();
        accountsNumbers.add(bankLoanAccountNumber);
        accountsNumbers.add(borrowMoneyRequest.getAccount());
        List<AccountEntity> accountsByNumbers = accountDAO.getAccountsByNumbers(accountsNumbers);
        AccountEntity targetAccount = null;
        AccountEntity bankLoanAccount= null;
        for (AccountEntity account : accountsByNumbers) {
            if (account.getAccount().equals(borrowMoneyRequest.getAccount())) {
                targetAccount = account;
            }
            else if (account.getAccount().equals(bankLoanAccountNumber)) {
                bankLoanAccount = account;
            }
        }
        DocumentEntity documentEntity = new DocumentEntity();
        documentEntity.setFromAccountId(bankLoanAccount.getId());
        documentEntity.setToAccountId(targetAccount.getId());
        documentEntity.setSum(borrowMoneyRequest.getSum());
        return this.createDocument(documentEntity);
    }

    @Override
    @Transactional
    public DocumentEntity transfer(TransferMoneyRequest transferMoneyRequest, Long clientId) throws Exception {
        List<String> accountsNumbers = new ArrayList<>();
        accountsNumbers.add(transferMoneyRequest.getFromAccount());
        accountsNumbers.add(transferMoneyRequest.getToAccount());
        List<AccountEntity> accountsByNumbers = accountDAO.getAccountsByNumbers(accountsNumbers);
        AccountEntity fromAccount = null;
        AccountEntity toAccount = null;
        for (AccountEntity account : accountsByNumbers) {
            if (account.getAccount().equals(transferMoneyRequest.getFromAccount())) {
                fromAccount = account;
            }
            else if (account.getAccount().equals(transferMoneyRequest.getToAccount())) {
                toAccount = account;
            }
        }
        DocumentEntity documentEntity = new DocumentEntity();
        documentEntity.setFromAccountId(fromAccount.getId());
        documentEntity.setToAccountId(toAccount.getId());
        documentEntity.setSum(transferMoneyRequest.getSum());
        return this.createDocument(documentEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentEntity getDocumentById(Long documentId) throws Exception {
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("documentId", documentId);
        DocumentEntity documentEntity = ops.queryForObject(
                "select d.id, a_dt.account fromAccount, d.acc_dt fromAccountId, a_kt.account toAccount, d.acc_kt toAccountId, d.sum, d.status" +
                        " from documents d" +
                        " left join accounts a_dt on d.acc_dt = a_dt.id" +
                        " left join accounts a_kt on d.acc_kt = a_kt.id" +
                        " where d.id = :documentId"
                , parameters
                , documentEntityRowMapper);
        return documentEntity;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentEntity> getDocumentsForAccountId(Long accountId) throws Exception {
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("accountId", accountId);
        List<DocumentEntity> documentEntities = ops.query(
                "select d.id, a_dt.account fromAccount, d.acc_dt fromAccountId, a_kt.account toAccount, d.acc_kt toAccountId, d.sum, d.status" +
                        " from documents d" +
                        " left join accounts a_dt on d.acc_dt = a_dt.id" +
                        " left join accounts a_kt on d.acc_kt = a_kt.id" +
                        " where d.acc_dt = :accountId or d.acc_kt = :accountId"
                , parameters
                , documentEntityRowMapper);
        return documentEntities;
    }
}
