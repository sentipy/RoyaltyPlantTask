package com.sentilabs.royaltyplanttask.request;

import java.math.BigDecimal;

/**
 * Created by AMachekhin on 08.07.2015.
 */
public class TransferMoneyRequest {

    private String fromAccount;
    private String toAccount;
    private BigDecimal sum;

    public String getFromAccount() {
        return fromAccount;
    }

    public void setFromAccount(String fromAccount) {
        this.fromAccount = fromAccount;
    }

    public String getToAccount() {
        return toAccount;
    }

    public void setToAccount(String toAccount) {
        this.toAccount = toAccount;
    }

    public BigDecimal getSum() {
        return sum;
    }

    public void setSum(BigDecimal sum) {
        this.sum = sum;
    }
}
