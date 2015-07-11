package com.sentilabs.royaltyplanttask.service.interfaces;

import com.sentilabs.royaltyplanttask.entity.DocumentEntity;
import com.sentilabs.royaltyplanttask.request.BorrowMoneyRequest;
import com.sentilabs.royaltyplanttask.request.TransferMoneyRequest;

import java.util.List;

/**
 * Created by AMachekhin on 08.07.2015.
 */
public interface DocumentService {

    DocumentEntity borrow(BorrowMoneyRequest borrowMoneyRequest, Long clientId) throws Exception;
    DocumentEntity transfer(TransferMoneyRequest transferMoneyRequest, Long clientId) throws Exception;
    DocumentEntity getDocumentById(Long documentId) throws Exception;
    List<DocumentEntity> getDocumentsForAccountId(Long accountId) throws Exception;
}
