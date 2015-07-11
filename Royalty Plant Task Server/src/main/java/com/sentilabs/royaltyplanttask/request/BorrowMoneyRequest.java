package com.sentilabs.royaltyplanttask.request;

import java.math.BigDecimal;

/**
 * Created by AMachekhin on 08.07.2015.
 */
public class BorrowMoneyRequest {

    private String account;
    private BigDecimal sum;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public BigDecimal getSum() {
        return sum;
    }

    public void setSum(BigDecimal sum) {
        this.sum = sum;
    }
}
