package com.hak.wymi.persistance.pojos.email;

import java.util.List;

public interface EmailDao {

    public void save(Email email);

    public List<Email> getUnsent();

    public void update(Email email);
}
