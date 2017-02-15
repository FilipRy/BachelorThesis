package com.filip.dressfriend.user;

import com.filip.dressfriend.User;

public interface UserService {

	
	/** 
	 * @param user
	 *            the entity to be inserted.
	 * @return The inserted entity.
	 */
	public User insertUser(User user);

	/**
	 * This method gets the entity having primary key id. It uses HTTP GET
	 * method.
	 * 
	 * @param id
	 *            the primary key of the java bean.
	 * @return The entity with primary key id.
	 */
	public User getUser(Long id);



}