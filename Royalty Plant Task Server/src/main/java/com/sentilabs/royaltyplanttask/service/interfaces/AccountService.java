package com.sentilabs.royaltyplanttask.service.interfaces;

import com.sentilabs.royaltyplanttask.entity.AccountEntity;
import com.sentilabs.royaltyplanttask.request.OpenAccountRequest;

import java.util.List;

/**
 * Created by sentipy on 08/07/15.
 */
public interface AccountService {

    AccountEntity openAccount(OpenAccountRequest openAccountRequest);
    List<AccountEntity> getAccountsForClientId(long clientId);
}
