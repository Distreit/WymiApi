package com.hak.wymi.coinbase;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CoinbaseResponse {
    private CoinbaseOrder order;

    public CoinbaseOrder getOrder() {
        return order;
    }

    public void setOrder(CoinbaseOrder order) {
        this.order = order;
    }
}
