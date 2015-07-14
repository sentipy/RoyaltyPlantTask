package com.sentilabs.royaltyplanttask.service.impl;

import com.sentilabs.royaltyplanttask.dao.interfaces.AccountDAO;
import com.sentilabs.royaltyplanttask.dao.interfaces.DocumentDAO;
import com.sentilabs.royaltyplanttask.entity.AccountEntity;
import com.sentilabs.royaltyplanttask.entity.DocumentEntity;
import com.sentilabs.royaltyplanttask.entity.helper.DocumentStatus;
import com.sentilabs.royaltyplanttask.exception.NotFoundException;
import com.sentilabs.royaltyplanttask.repository.AccountRepository;
import com.sentilabs.royaltyplanttask.repository.DocumentRepository;
import com.sentilabs.royaltyplanttask.request.BorrowMoneyRequest;
import com.sentilabs.royaltyplanttask.request.TransferMoneyRequest;
import com.sentilabs.royaltyplanttask.service.interfaces.DocumentService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by AMachekhin on 08.07.2015.
 */

@Component
public class DocumentServiceImpl implements DocumentService {

    private static Logger logger = LogManager.getLogger(DocumentServiceImpl.class);

    @Autowired
    private NamedParameterJdbcOperations ops;

    @Autowired
    private AccountDAO accountDAO;

    @Autowired
    private DocumentDAO documentDAO;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private AccountRepository accountRepository;

    /*@Autowired
    private SessionFactory sessionFactory;*/

    private static RowMapper<DocumentEntity> documentEntityRowMapper = (resultSet, i) -> {
        DocumentEntity documentEntity = new DocumentEntity();
        documentEntity.setId(resultSet.getLong("id"));

        AccountEntity dtAccountEntity = new AccountEntity();
        dtAccountEntity.setId(resultSet.getLong("fromAccountId"));
        dtAccountEntity.setAccount(resultSet.getString("fromAccount"));
        documentEntity.setDebetAccount(dtAccountEntity);

        AccountEntity ktAccountEntity = new AccountEntity();
        ktAccountEntity.setId(resultSet.getLong("toAccountId"));
        ktAccountEntity.setAccount(resultSet.getString("toAccount"));
        documentEntity.setCreditAccount(ktAccountEntity);

        documentEntity.setDocSum(resultSet.getBigDecimal("doc_sum"));
        documentEntity.setDocNum(resultSet.getString("doc_num"));
        documentEntity.setStatus(resultSet.getString("status"));
        return documentEntity;
    };

    private DocumentEntity mapToDocumentEntity(Map<String, Object> map) {
        DocumentEntity documentEntity = new DocumentEntity();
        documentEntity.setId((Long) map.get("id"));

        AccountEntity dtAccountEntity = new AccountEntity();
        dtAccountEntity.setId((Long) map.get("fromAccountId"));
        dtAccountEntity.setAccount((String) map.get("fromAccount"));
        documentEntity.setDebetAccount(dtAccountEntity);

        AccountEntity ktAccountEntity = new AccountEntity();
        ktAccountEntity.setId((Long) map.get("toAccountId"));
        ktAccountEntity.setAccount((String) map.get("toAccount"));
        documentEntity.setCreditAccount(ktAccountEntity);

        documentEntity.setDocSum((BigDecimal) map.get("doc_sum"));
        documentEntity.setDocNum((String) map.get("doc_num"));
        documentEntity.setStatus((String) map.get("status"));
        return documentEntity;
    }

    private DocumentEntity createDocument(DocumentEntity documentEntity) {
        return documentRepository.save(documentEntity);
        /*MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("fromAccount", documentEntity.getDebetAccount().getId())
                .addValue("toAccount", documentEntity.getCreditAccount().getId())
                .addValue("sum", documentEntity.getDocSum());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        ops.update("insert into documents (acc_dt, acc_kt, doc_sum) values (:fromAccount, :toAccount, :sum)", parameters, keyHolder);
        Map<String, Object> map = keyHolder.getKeyList().get(0);
        if (map.size() == 1) {
            try {
                return this.getDocumentById((Long) keyHolder.getKey());
            } catch (Exception e) {
                logger.error(e);
                throw e;
            }
        }
        return mapToDocumentEntity(map);*/
    }

    @Override
    @Transactional
    public DocumentEntity borrow(BorrowMoneyRequest borrowMoneyRequest, Long clientId) {
        String bankLoanAccountNumber = "20202000000000000000"; // TODO remove this hard code!
        List<String> accountsNumbers = new ArrayList<>();
        accountsNumbers.add(bankLoanAccountNumber);
        accountsNumbers.add(borrowMoneyRequest.getAccountNumber());
        List<AccountEntity> accountsByNumbers = accountDAO.getAccountsByNumbers(accountsNumbers);
        AccountEntity targetAccount = null;
        AccountEntity bankLoanAccount= null;
        for (AccountEntity account : accountsByNumbers) {
            if (account.getAccount().equals(borrowMoneyRequest.getAccountNumber())) {
                targetAccount = account;
            }
            else if (account.getAccount().equals(bankLoanAccountNumber)) {
                bankLoanAccount = account;
            }
        }
        if (bankLoanAccount == null) {
            throw new NotFoundException("Account with number " + bankLoanAccountNumber + " not found");
        }
        if (targetAccount == null) {
            throw new NotFoundException("Account with number " + borrowMoneyRequest.getAccountNumber() + " not found");
        }
        DocumentEntity documentEntity = new DocumentEntity();
        documentEntity.setDebetAccount(bankLoanAccount);
        documentEntity.setCreditAccount(targetAccount);
        documentEntity.setDocSum(borrowMoneyRequest.getSum());
        return this.createDocument(documentEntity);
    }

    @Override
    @Transactional
    public DocumentEntity transfer(TransferMoneyRequest transferMoneyRequest, Long clientId) {
        List<String> accountsNumbers = new ArrayList<>();
        accountsNumbers.add(transferMoneyRequest.getFromAccount());
        accountsNumbers.add(transferMoneyRequest.getToAccount());
        //List<AccountEntity> accountsByNumbers = accountDAO.getAccountsByNumbers(accountsNumbers);
        List<AccountEntity> accountsByNumbers = accountRepository.findAccountsByAccountsNumbers(accountsNumbers);
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
        if (fromAccount == null) {
            throw new NotFoundException("Account with number " + transferMoneyRequest.getFromAccount() + " not found");
        }
        if (toAccount == null) {
            throw new NotFoundException("Account with number " + transferMoneyRequest.getToAccount() + " not found");
        }
        DocumentEntity documentEntity = new DocumentEntity();
        documentEntity.setDebetAccount(fromAccount);
        documentEntity.setCreditAccount(toAccount);
        documentEntity.setDocSum(transferMoneyRequest.getSum());
        return this.createDocument(documentEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentEntity getDocumentById(Long documentId) {
        DocumentEntity documentEntity = documentRepository.findOne(documentId);
        return documentEntity;
        /*MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("documentId", documentId);
        DocumentEntity documentEntity = ops.queryForObject(
                "select d.id, a_dt.account fromAccount, d.acc_dt fromAccountId, a_kt.account toAccount, d.acc_kt toAccountId" +
                        ", d.doc_sum, d.doc_num, d.status" +
                        " from documents d" +
                        " left join accounts a_dt on d.acc_dt = a_dt.id" +
                        " left join accounts a_kt on d.acc_kt = a_kt.id" +
                        " where d.id = :documentId"
                , parameters
                , documentEntityRowMapper);
        return documentEntity;*/
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentEntity> getDocumentsForAccountId(Long accountId) {
        return documentRepository.findDocumentsForAccountId(accountId);
        /*MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("accountId", accountId);
        List<DocumentEntity> documentEntities = ops.query(
                "select d.id, a_dt.account fromAccount, d.acc_dt fromAccountId, a_kt.account toAccount, d.acc_kt toAccountId" +
                        ", d.doc_sum, d.doc_num, d.status" +
                        " from documents d" +
                        " left join accounts a_dt on d.acc_dt = a_dt.id" +
                        " left join accounts a_kt on d.acc_kt = a_kt.id" +
                        " where d.acc_dt = :accountId or d.acc_kt = :accountId"
                , parameters
                , documentEntityRowMapper);
        return documentEntities;*/
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentEntity> getDocumentsForAccountNumber(String accountNumber) {
        return documentRepository.findDocumentsForAccountNumber(accountNumber);
    }

    @Override
    @Transactional
    public DocumentEntity processDocument(Long documentId) {
        if (!documentDAO.lockRowInDatabaseById(documentId)) {
            return null;
        }
        DocumentEntity documentEntity = this.getDocumentById(documentId);
        if (documentEntity == null) {
            return null;
        }
        if (!documentEntity.getStatus().equals(DocumentStatus.CREATED.name())) {
            return null;
        }

        List<String> accountsNumbers = new ArrayList<>();
        accountsNumbers.add(documentEntity.getDebetAccount().getAccount());
        accountsNumbers.add(documentEntity.getCreditAccount().getAccount());
        Collections.sort(accountsNumbers);
        AccountEntity fromAccount = null, toAccount = null;
        for (String accountNumber : accountsNumbers) {
            accountDAO.lockRowInDatabaseByAccountNumber(accountNumber);
            AccountEntity accountByNumber = accountDAO.getAccountByNumber(accountNumber);
            if (accountByNumber.getId().equals(documentEntity.getDebetAccount().getId())) {
                fromAccount = accountByNumber;
            }
            else if (accountByNumber.getId().equals(documentEntity.getCreditAccount().getId())) {
                toAccount = accountByNumber;
            }
        }
        if (fromAccount == null) {
            documentEntity.setStatus(DocumentStatus.MANUALLY.name());
            documentEntity = documentRepository.save(documentEntity);
            return documentEntity;
        }
        if (toAccount == null) {
            documentEntity.setStatus(DocumentStatus.MANUALLY.name());
            documentEntity = documentRepository.save(documentEntity);
            return documentEntity;
        }
        if (fromAccount.getBalance().compareTo(documentEntity.getDocSum()) == -1 && !fromAccount.canBalanceBeNegative()) {
            documentEntity.setStatus(DocumentStatus.REJECTED.name());
            documentEntity = documentRepository.save(documentEntity);
            return documentEntity;
        }
        fromAccount.setBalance(fromAccount.getBalance().subtract(documentEntity.getDocSum()));
        toAccount.setBalance(toAccount.getBalance().add(documentEntity.getDocSum()));
        documentEntity.setStatus(DocumentStatus.PROV.name());

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);
        documentEntity = documentRepository.save(documentEntity);
        return documentEntity;
    }
}
