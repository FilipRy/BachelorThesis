package com.filip.dressfriend.userseepost;

import com.filip.dressfriend.UserSeePost;

public interface ICreateUserSeePost {

	/**
	 * This inserts a new entity into App Engine datastore. If the entity
	 * already exists in the datastore, an exception is thrown. It uses HTTP
	 * POST method.
	 * 
	 * @param userseepost
	 *            the entity to be inserted.
	 * @return The inserted entity.
	 */
	public abstract UserSeePost insertUserSeePost(UserSeePost userseepost);

}