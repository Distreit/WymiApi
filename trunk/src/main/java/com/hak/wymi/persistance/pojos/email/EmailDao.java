package com.hak.wymi.persistance.pojos.email;

public interface EmailDao {

    public void save(Email email);

    public Email getUnsent();

    public void update(Email email);
}
