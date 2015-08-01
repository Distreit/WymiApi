package com.hak.wymi.persistance.pojos.user;

import java.security.Principal;

public interface UserDao {
	public boolean save(User user);

	public User get(Principal principal);

	public User getFromName(String name);

	public User getFromEmail(String email);
}
