package com.hak.wymi.persistance.managers;

import com.hak.wymi.persistance.pojos.email.Email;
import com.hak.wymi.persistance.pojos.email.EmailDao;
import com.hak.wymi.utility.emailer.Emailer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EmailManager {
    @Autowired
    private EmailDao emailDao;

    @Autowired
    private Emailer emailer;

    @Transactional
    public void save(Email email) {
        emailDao.save(email);
        emailer.add(email);
    }

    @Transactional
    public List<Email> getUnsent() {
        return emailDao.getUnsent();
    }

    @Transactional
    public void update(Email email) {
        emailDao.update(email);
    }
}
