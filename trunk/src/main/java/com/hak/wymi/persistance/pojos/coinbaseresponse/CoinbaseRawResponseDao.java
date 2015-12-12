package com.hak.wymi.persistance.pojos.coinbaseresponse;

import java.util.List;

public interface CoinbaseRawResponseDao {
    void save(CoinbaseRawResponse coinbaseRawResponse);

    List<CoinbaseRawResponse> getUnprocessed();

    void update(CoinbaseRawResponse response);
}
