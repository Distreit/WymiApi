package com.hak.wymi.persistance.managers;

import com.hak.wymi.persistance.pojos.usertopicrank.UserTopicRankDao;
import com.hak.wymi.persistance.ranker.UserTopicRanker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserTopicRankManager {
    @Autowired
    private UserTopicRankDao userTopicRankDao;

    @Transactional
    public Boolean save(UserTopicRanker ranker) {
        return userTopicRankDao.save(ranker);
    }
}
