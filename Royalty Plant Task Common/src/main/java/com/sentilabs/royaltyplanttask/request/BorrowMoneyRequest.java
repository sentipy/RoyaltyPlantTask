package com.sentilabs.royaltyplanttask.request;

import java.math.BigDecimal;

/**
 * Created by AMachekhin on 08.07.2015.
 */
public class BorrowMoneyRequest {

    private String accountNumber;
    private BigDecimal sum;

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimal getSum() {
        return sum;
    }

    public void setSum(BigDecimal sum) {
        this.sum = sum;
    }
}
