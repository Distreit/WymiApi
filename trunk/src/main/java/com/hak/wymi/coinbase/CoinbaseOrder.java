package com.hak.wymi.coinbase;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.Map;

@XmlRootElement
public class CoinbaseOrder {
    private String id;

    private String uuid;

    private String type;

    private String status;

    private CoinbaseEvent event;

    private CoinbaseResponseData data;

    private CoinbaseUser user;

    private CoinbaseAccount account;

    private Integer delivery_attempts;

    private CoinbaseDeliveryResponse delivery_response;

    private Date created_at;

    private Date updated_at;

    private String resource;

    private String resource_path;

    private CoinbaseSubscriber subscriber;

    private Map<String, String> metadata;

    private CoinbaseCurrencyAmount total_btc;

    private CoinbaseCurrencyAmount total_native;

    private CoinbaseCurrencyAmount total_payout;

    private String custom;

    private String receive_address;

    private String refund_address;

    private CoinbaseButton button;

    private CoinbaseTransaction transaction;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public CoinbaseResponseData getData() {
        return data;
    }

    public void setData(CoinbaseResponseData data) {
        this.data = data;
    }

    public CoinbaseUser getUser() {
        return user;
    }

    public void setUser(CoinbaseUser user) {
        this.user = user;
    }

    public CoinbaseAccount getAccount() {
        return account;
    }

    public void setAccount(CoinbaseAccount account) {
        this.account = account;
    }

    public Integer getDelivery_attempts() {
        return delivery_attempts;
    }

    public void setDelivery_attempts(Integer delivery_attempts) {
        this.delivery_attempts = delivery_attempts;
    }

    public CoinbaseDeliveryResponse getDelivery_response() {
        return delivery_response;
    }

    public void setDelivery_response(CoinbaseDeliveryResponse delivery_response) {
        this.delivery_response = delivery_response;
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

    public CoinbaseSubscriber getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(CoinbaseSubscriber subscriber) {
        this.subscriber = subscriber;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public CoinbaseEvent getEvent() {
        return event;
    }

    public void setEvent(CoinbaseEvent event) {
        this.event = event;
    }

    public CoinbaseCurrencyAmount getTotal_btc() {
        return total_btc;
    }

    public void setTotal_btc(CoinbaseCurrencyAmount total_btc) {
        this.total_btc = total_btc;
    }

    public CoinbaseCurrencyAmount getTotal_native() {
        return total_native;
    }

    public void setTotal_native(CoinbaseCurrencyAmount total_native) {
        this.total_native = total_native;
    }

    public CoinbaseCurrencyAmount getTotal_payout() {
        return total_payout;
    }

    public void setTotal_payout(CoinbaseCurrencyAmount total_payout) {
        this.total_payout = total_payout;
    }

    public String getCustom() {
        return custom;
    }

    public void setCustom(String custom) {
        this.custom = custom;
    }

    public String getReceive_address() {
        return receive_address;
    }

    public void setReceive_address(String receive_address) {
        this.receive_address = receive_address;
    }

    public CoinbaseButton getButton() {
        return button;
    }

    public void setButton(CoinbaseButton button) {
        this.button = button;
    }

    public String getRefund_address() {
        return refund_address;
    }

    public void setRefund_address(String refund_address) {
        this.refund_address = refund_address;
    }

    public CoinbaseTransaction getTransaction() {
        return transaction;
    }

    public void setTransaction(CoinbaseTransaction transaction) {
        this.transaction = transaction;
    }
}
