package com.sentilabs.royaltyplanttask.response;

import java.math.BigDecimal;

/**
 * Created by AMachekhin on 09.07.2015.
 */
public class AccountDetailResponse {

    private String accountNumber;
    private BigDecimal balance;

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
