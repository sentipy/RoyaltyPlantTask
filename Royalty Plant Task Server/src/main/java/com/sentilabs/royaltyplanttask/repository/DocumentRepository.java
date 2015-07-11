package com.sentilabs.royaltyplanttask.repository;

import com.sentilabs.royaltyplanttask.entity.DocumentEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by sentipy on 09/07/15.
 */
public interface DocumentRepository extends JpaRepository<DocumentEntity, Long> {

    @Query(value = "select d from DocumentEntity d where d.debetAccount.id = :accountId or d.creditAccount.id = :accountId")
    List<DocumentEntity> findDocumentsForAccountId(Long accountId);

    @Query(value = "select d from DocumentEntity d where d.debetAccount.account = :accountNumber or d.creditAccount.account = :accountNumber")
    List<DocumentEntity> findDocumentsForAccountNumber(@Param("accountNumber")String accountNumber);

    @Query(value = "select d from DocumentEntity d where d.status = :status")
    List<DocumentEntity> findDocumentsByStatus(@Param("status")String status, Pageable pageable);
}
