package com.sentilabs.royaltyplanttask.repository;

import com.sentilabs.royaltyplanttask.dao.interfaces.AccountDAO;
import com.sentilabs.royaltyplanttask.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by sentipy on 09/07/15.
 */
public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

    @Query("select a from AccountEntity a where a.account = :accountNumber")
    AccountEntity findAccountsByAccountNumber(@Param("accountNumber") String accountNumber);

    @Query("select a from AccountEntity a where a.account in (:accountsNumbers)")
    List<AccountEntity> findAccountsByAccountsNumbers(@Param("accountsNumbers") List<String> accountsNumbers);

    @Query("select a from AccountEntity a where a.client.id = :clientId")
    List<AccountEntity> findAccountsByClientId(@Param("clientId")Long clientId);
}
