package com.sentilabs.royaltyplanttask.request;

/**
 * Created by sentipy on 09/07/15.
 */
public class OpenAccountRequest {

    private String account;
    private Long clientId;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }
}
