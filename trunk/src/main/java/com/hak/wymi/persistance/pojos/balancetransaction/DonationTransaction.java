package com.hak.wymi.persistance.pojos.balancetransaction;

import com.hak.wymi.persistance.pojos.user.User;

public interface DonationTransaction extends BalanceTransaction {
    User getSourceUser();

    User getDestinationUser();
}
