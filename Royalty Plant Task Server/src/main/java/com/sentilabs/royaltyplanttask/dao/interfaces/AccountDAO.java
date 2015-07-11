package com.sentilabs.royaltyplanttask.dao.interfaces;

import com.sentilabs.royaltyplanttask.entity.AccountEntity;

import java.util.List;

/**
 * Created by sentipy on 04/07/15.
 */
public interface AccountDAO {

    AccountEntity getAccountById(long accountId);
    List<AccountEntity> getAccountsByClientId(long clientId);
    AccountEntity openAccount(long clientId);
    AccountEntity openAccount(long clientId, String account);
    AccountEntity getAccountByNumber(String accountNumber);
    List<AccountEntity> getAccountsByNumbers(List<String> accountsNumbers);
    void lockRowInDatabaseById(Long accountId);
    void lockRowInDatabaseByAccountNumber(String accountNumber);
}
