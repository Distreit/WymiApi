package com.hak.wymi.rent;

import com.hak.wymi.persistance.managers.OwnershipTransactionManager;
import com.hak.wymi.persistance.managers.TopicManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class RentScheduler {

    @Autowired
    private TopicManager topicManager;

    @Autowired
    private OwnershipTransactionManager ownershipTransactionManager;

    @Autowired
    private RentManager rentManager;

    @Scheduled(fixedRate = 5000)
    public void checkRent() {
        topicManager.getRentDue().stream().forEach(rentManager::processTopic);
    }

    @Scheduled(fixedRate = 5000)
    public void processOwnershipTransactions() {
        ownershipTransactionManager.getRentPeriodExpired().stream().forEach(rentManager::processRentPeriodExpired);
    }
}
