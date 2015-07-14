package com.sentilabs.royaltyplanttask.scheduler;

import com.sentilabs.royaltyplanttask.entity.DocumentEntity;
import com.sentilabs.royaltyplanttask.entity.helper.DocumentStatus;
import com.sentilabs.royaltyplanttask.repository.AccountRepository;
import com.sentilabs.royaltyplanttask.repository.DocumentRepository;
import com.sentilabs.royaltyplanttask.service.impl.DocumentServiceImpl;
import com.sentilabs.royaltyplanttask.service.interfaces.DocumentService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by sentipy on 11/07/15.
 */
@Component
public class DocumentProcessor implements IDocumentProcessor {

    private static Logger logger = LogManager.getLogger(DocumentProcessor.class);

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private DocumentService documentService;

    @Override
    public void processDocuments() {
        while (true) {
            Pageable pageable = new PageRequest(0, 10); //TODO: to take from parameters;
            List<DocumentEntity> documentEntities = documentRepository
                    .findDocumentsByStatus(DocumentStatus.CREATED.name(), pageable);
            if (documentEntities.size() == 0) {
                break;
            }
            for (DocumentEntity documentEntity : documentEntities) {
                try {
                    documentService.processDocument(documentEntity.getId());
                }
                catch (Throwable t) {
                    logger.error("error processing document id = " + documentEntity.getId(), t);
                }
            }
        }
    }
}
