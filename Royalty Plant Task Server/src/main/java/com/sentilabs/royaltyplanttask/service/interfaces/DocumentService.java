package com.sentilabs.royaltyplanttask.service.interfaces;

import com.sentilabs.royaltyplanttask.entity.DocumentEntity;
import com.sentilabs.royaltyplanttask.request.BorrowMoneyRequest;
import com.sentilabs.royaltyplanttask.request.TransferMoneyRequest;

import java.util.List;

/**
 * Created by AMachekhin on 08.07.2015.
 */
public interface DocumentService {

    DocumentEntity borrow(BorrowMoneyRequest borrowMoneyRequest, Long clientId);
    DocumentEntity transfer(TransferMoneyRequest transferMoneyRequest, Long clientId);
    DocumentEntity getDocumentById(Long documentId);
    List<DocumentEntity> getDocumentsForAccountId(Long accountId);
    List<DocumentEntity> getDocumentsForAccountNumber(String accountNumber);
    DocumentEntity processDocument(Long documentId);
}
