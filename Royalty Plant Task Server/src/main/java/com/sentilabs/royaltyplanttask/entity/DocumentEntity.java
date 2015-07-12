package com.sentilabs.royaltyplanttask.entity;

import com.sentilabs.royaltyplanttask.entity.helper.DocumentStatus;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by AMachekhin on 08.07.2015.
 */
@Entity
@Table(name = "documents")
public class DocumentEntity {

    @Id
    @SequenceGenerator(name = "documents_id_seq", sequenceName = "documents_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "documents_id_seq")
    private Long id;

    @Column(name = "status")
    private String status = DocumentStatus.CREATED.name();

    @Column(name = "doc_num")
    private String docNum;

    //@Column(name = "acc_dt")
    @ManyToOne(targetEntity = AccountEntity.class)
    @JoinColumn(name = "acc_dt")
    private AccountEntity debetAccount;

    //@Column(name = "acc_kt")
    @ManyToOne(targetEntity = AccountEntity.class)
    @JoinColumn(name = "acc_kt")
    private AccountEntity creditAccount;

    @Column(name = "doc_sum")
    private BigDecimal docSum = BigDecimal.ZERO;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDocNum() {
        return docNum;
    }

    public void setDocNum(String docNum) {
        this.docNum = docNum;
    }

    public AccountEntity getDebetAccount() {
        return debetAccount;
    }

    public void setDebetAccount(AccountEntity debetAccount) {
        this.debetAccount = debetAccount;
    }

    public AccountEntity getCreditAccount() {
        return creditAccount;
    }

    public void setCreditAccount(AccountEntity creditAccount) {
        this.creditAccount = creditAccount;
    }

    public BigDecimal getDocSum() {
        return docSum;
    }

    public void setDocSum(BigDecimal docSum) {
        this.docSum = docSum;
    }

    @Override
    public String toString() {
        return "DocumentEntity {" +
                "id=" + id +
                ", status='" + status + '\'' +
                ", docNum='" + docNum + '\'' +
                ", debetAccount=" + debetAccount +
                ", creditAccount=" + creditAccount +
                ", docSum='" + docSum + '\'' +
                '}';
    }
}
