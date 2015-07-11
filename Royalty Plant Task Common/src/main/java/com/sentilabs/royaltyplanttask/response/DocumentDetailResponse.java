package com.sentilabs.royaltyplanttask.response;

import java.math.BigDecimal;

/**
 * Created by AMachekhin on 09.07.2015.
 */
public class DocumentDetailResponse {

    private String acc_dt;
    private String acc_kt;
    private String doc_num;
    private BigDecimal doc_sum;
    private String status;

    public String getAcc_dt() {
        return acc_dt;
    }

    public void setAcc_dt(String acc_dt) {
        this.acc_dt = acc_dt;
    }

    public String getAcc_kt() {
        return acc_kt;
    }

    public void setAcc_kt(String acc_kt) {
        this.acc_kt = acc_kt;
    }

    public String getDoc_num() {
        return doc_num;
    }

    public void setDoc_num(String doc_num) {
        this.doc_num = doc_num;
    }

    public BigDecimal getDoc_sum() {
        return doc_sum;
    }

    public void setDoc_sum(BigDecimal doc_sum) {
        this.doc_sum = doc_sum;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
