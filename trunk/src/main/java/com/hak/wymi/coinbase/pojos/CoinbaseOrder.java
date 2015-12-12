package com.hak.wymi.coinbase.pojos;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.Map;

@XmlRootElement
public class CoinbaseOrder {
    private String id;
    private String code;
    private String type;
    private String name;
    private String description;
    private CoinbaseCurrencyAmount amount;
    private String receipt_url;
    private String resource;
    private String resource_path;
    private String status;
    private CoinbaseCurrencyAmount bitcoin_amount;
    private CoinbaseCurrencyAmount payout_amount;
    private String bitcoin_address;
    private String refund_address;
    private String bitcoin_uri;
    private String notifications_url;
    private Date paid_at;
    private Date mispaid_at;
    private Date expires_at;
    private Map<String, String> metadata;
    private Date created_at;
    private Date updated_at;
    private Object customer_info;
    private Object transaction;
    private Object[] mispayments;
    private Object[] refunds;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CoinbaseCurrencyAmount getAmount() {
        return amount;
    }

    public void setAmount(CoinbaseCurrencyAmount amount) {
        this.amount = amount;
    }

    public String getReceipt_url() {
        return receipt_url;
    }

    public void setReceipt_url(String receipt_url) {
        this.receipt_url = receipt_url;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getResource_path() {
        return resource_path;
    }

    public void setResource_path(String resource_path) {
        this.resource_path = resource_path;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public CoinbaseCurrencyAmount getBitcoin_amount() {
        return bitcoin_amount;
    }

    public void setBitcoin_amount(CoinbaseCurrencyAmount bitcoin_amount) {
        this.bitcoin_amount = bitcoin_amount;
    }

    public CoinbaseCurrencyAmount getPayout_amount() {
        return payout_amount;
    }

    public void setPayout_amount(CoinbaseCurrencyAmount payout_amount) {
        this.payout_amount = payout_amount;
    }

    public String getBitcoin_address() {
        return bitcoin_address;
    }

    public void setBitcoin_address(String bitcoin_address) {
        this.bitcoin_address = bitcoin_address;
    }

    public String getRefund_address() {
        return refund_address;
    }

    public void setRefund_address(String refund_address) {
        this.refund_address = refund_address;
    }

    public String getBitcoin_uri() {
        return bitcoin_uri;
    }

    public void setBitcoin_uri(String bitcoin_uri) {
        this.bitcoin_uri = bitcoin_uri;
    }

    public String getNotifications_url() {
        return notifications_url;
    }

    public void setNotifications_url(String notifications_url) {
        this.notifications_url = notifications_url;
    }

    public Date getPaid_at() {
        return paid_at;
    }

    public void setPaid_at(Date paid_at) {
        this.paid_at = paid_at;
    }

    public Date getMispaid_at() {
        return mispaid_at;
    }

    public void setMispaid_at(Date mispaid_at) {
        this.mispaid_at = mispaid_at;
    }

    public Date getExpires_at() {
        return expires_at;
    }

    public void setExpires_at(Date expires_at) {
        this.expires_at = expires_at;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

    public Object getCustomer_info() {
        return customer_info;
    }

    public void setCustomer_info(Object customer_info) {
        this.customer_info = customer_info;
    }

    public Object getTransaction() {
        return transaction;
    }

    public void setTransaction(Object transaction) {
        this.transaction = transaction;
    }

    public Object[] getMispayments() {
        return mispayments;
    }

    public void setMispayments(Object[] mispayments) {
        this.mispayments = mispayments;
    }

    public Object[] getRefunds() {
        return refunds;
    }

    public void setRefunds(Object[] refunds) {
        this.refunds = refunds;
    }
}
