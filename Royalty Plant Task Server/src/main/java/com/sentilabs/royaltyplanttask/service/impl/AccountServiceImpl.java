package com.sentilabs.royaltyplanttask.service.impl;

import com.sentilabs.royaltyplanttask.dao.interfaces.AccountDAO;
import com.sentilabs.royaltyplanttask.entity.AccountEntity;
import com.sentilabs.royaltyplanttask.entity.UserEntity;
import com.sentilabs.royaltyplanttask.repository.AccountRepository;
import com.sentilabs.royaltyplanttask.request.OpenAccountRequest;
import com.sentilabs.royaltyplanttask.service.interfaces.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by sentipy on 08/07/15.
 */
@Component
public class AccountServiceImpl implements AccountService {

    @Autowired
    AccountDAO accountDAO;

    @Autowired
    private AccountRepository accountRepository;

    private boolean allowRandomAccountNumber = true; //TODO: to take from properties

    @Override
    @Transactional
    public AccountEntity openAccount(OpenAccountRequest openAccountRequest) {
        if (openAccountRequest.getAccount() != null) {
            if (allowRandomAccountNumber){
                AccountEntity accountEntity = new AccountEntity();
                UserEntity userEntity = new UserEntity();
                userEntity.setId(openAccountRequest.getClientId());
                accountEntity.setClient(userEntity);
                accountEntity.setAccount(openAccountRequest.getAccount());
                return accountRepository.save(accountEntity);
                //return accountDAO.openAccount(openAccountRequest.getClientId(), openAccountRequest.getAccountNumber());
            }
        }
        return accountDAO.openAccount(openAccountRequest.getClientId());
    }

    @Override
    public List<AccountEntity> getAccountsForClientId(long clientId) {
        return accountDAO.getAccountsByClientId(clientId);
    }
}
