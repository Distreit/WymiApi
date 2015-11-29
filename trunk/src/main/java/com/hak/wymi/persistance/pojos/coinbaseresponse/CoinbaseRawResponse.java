package com.hak.wymi.persistance.pojos.coinbaseresponse;

import com.hak.wymi.persistance.pojos.PersistentObject;
import com.hak.wymi.validations.groups.Creation;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Null;

@Entity
@Table(name = "coinbaseresponse")
public class CoinbaseRawResponse extends PersistentObject {
    private static final long serialVersionUID = 1949625540493733465L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Null(groups = {Creation.class})
    private Integer coinbaseResponseId;

    private String responseText;

    private Boolean processed;

    public Integer getCoinbaseResponseId() {
        return coinbaseResponseId;
    }

    public void setCoinbaseResponseId(Integer coinbaseResponseId) {
        this.coinbaseResponseId = coinbaseResponseId;
    }

    public String getResponseText() {
        return responseText;
    }

    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }

    public Boolean getProcessed() {
        return processed;
    }

    public void setProcessed(Boolean processed) {
        this.processed = processed;
    }
}
