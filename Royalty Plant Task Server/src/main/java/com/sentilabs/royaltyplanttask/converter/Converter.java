package com.sentilabs.royaltyplanttask.converter;

import com.sentilabs.royaltyplanttask.entity.AccountEntity;
import com.sentilabs.royaltyplanttask.entity.DocumentEntity;
import com.sentilabs.royaltyplanttask.request.BorrowMoneyRequest;
import com.sentilabs.royaltyplanttask.response.AccountDetailResponse;
import com.sentilabs.royaltyplanttask.response.DocumentDetailResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sentipy on 11/07/15.
 */
public class Converter {

    public static AccountDetailResponse convert(AccountEntity accountEntity) {
        AccountDetailResponse accountDetailResponse = new AccountDetailResponse();
        accountDetailResponse.setAccountNumber(accountEntity.getAccount());
        accountDetailResponse.setBalance(accountEntity.getBalance());
        return accountDetailResponse;
    }

    public static List<AccountDetailResponse> convertAccountEntitiesListToAccountDetailResponses(List<AccountEntity> accountEntities) {
        List<AccountDetailResponse> accountDetailResponses = new ArrayList<>();
        for (AccountEntity accountEntity : accountEntities) {
            accountDetailResponses.add(convert(accountEntity));
        }
        return accountDetailResponses;
    }

    public static DocumentDetailResponse convert(DocumentEntity documentEntity) {
        DocumentDetailResponse documentDetailResponse = new DocumentDetailResponse();
        documentDetailResponse.setAcc_dt(documentEntity.getDebetAccount().getAccount());
        documentDetailResponse.setAcc_kt(documentEntity.getCreditAccount().getAccount());
        documentDetailResponse.setDoc_num(documentEntity.getDocNum());
        documentDetailResponse.setDoc_sum(documentEntity.getDocSum());
        documentDetailResponse.setStatus(documentEntity.getStatus());
        return documentDetailResponse;
    }

    public static List<DocumentDetailResponse> convertDocumentEntitiesToDocumentDetailResponses(List<DocumentEntity> documentEntities) {
        List<DocumentDetailResponse> documentDetailResponses = new ArrayList<>();
        for (DocumentEntity documentEntity : documentEntities) {
            documentDetailResponses.add(convert(documentEntity));
        }
        return documentDetailResponses;
    }
}
