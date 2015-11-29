package com.hak.wymi.coinbase;

import org.joda.time.DateTime;

public class CoinbaseResponse {
    private String id;

    private String type;

    private CoinbaseResponseData data;

    private CoinbaseUser user;

    private CoinbaseAccount account;

    private Integer delivery_attempts;

    private CoinbaseDeliveryResponse delivery_response;

    private DateTime created_at;

    private DateTime updated_at;

    private String resource;

    private String resource_path;

    private CoinbaseSubscriber subscriber;
}
