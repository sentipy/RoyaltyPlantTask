package com.sentilabs.royaltyplanttask.entity;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by sentipy on 04/07/15.
 */
@Entity
@Table(name = "accounts")
public class AccountEntity {

    @Id
    @SequenceGenerator(name = "accounts_id_seq", sequenceName = "accounts_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "accounts_id_seq")
    private Long id;

    @Column(name = "account")
    private String account;

    //@Column(name = "client")
    @ManyToOne(targetEntity = UserEntity.class)
    @JoinColumn(name = "client")
    private UserEntity client;

    @Column(name = "balance")
    private BigDecimal balance = BigDecimal.ZERO;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public UserEntity getClient() {
        return client;
    }

    public void setClient(UserEntity client) {
        this.client = client;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public boolean canBalanceBeNegative() { //TODO: move to database
        return !StringUtils.isEmpty(this.account) && this.account.startsWith("202");
    }

    @Override
    public String toString() {
        return "AccountEntity {" +
                "id=" + id +
                ", account='" + account + '\'' +
                ", client=" + client +
                ", balance=" + balance +
                '}';
    }
}
