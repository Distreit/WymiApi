package com.hak.wymi.persistance.pojos.balancetransaction;

import com.hak.wymi.persistance.interfaces.HasPointsBalance;
import com.hak.wymi.persistance.pojos.user.User;

public interface DonationTransaction extends BalanceTransaction {
    @Override
    HasPointsBalance getSource();

    User getDestinationUser();

    User getSourceUser();
}
