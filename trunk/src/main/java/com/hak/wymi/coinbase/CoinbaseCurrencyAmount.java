package com.hak.wymi.coinbase;

public class CoinbaseCurrencyAmount {
    private Double cents;

    private String currency_iso;

    public Double getCents() {
        return cents;
    }

    public void setCents(Double cents) {
        this.cents = cents;
    }

    public String getCurrency_iso() {
        return currency_iso;
    }

    public void setCurrency_iso(String currency_iso) {
        this.currency_iso = currency_iso;
    }
}
