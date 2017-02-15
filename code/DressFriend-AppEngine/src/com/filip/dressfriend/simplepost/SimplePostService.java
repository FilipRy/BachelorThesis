package com.filip.dressfriend.simplepost;

import com.filip.dressfriend.SimplePost;
import com.google.api.server.spi.response.CollectionResponse;

public interface SimplePostService {

	/**
	 * @param simplepost
	 *            the entity to be inserted.
	 * @return The inserted entity.
	 */
	public SimplePost insertSimplePost(SimplePost simplepost);
	
	
	/**
	 * @return A CollectionResponse class containing the list of all entities
	 *         persisted and a cursor to the next page.
	 */
	public CollectionResponse<SimplePost> listSimplePost();
	public CollectionResponse<SimplePost> listSimplePostsOfUser(Long userId);
	/*
	 * returns a list of simple posts, which are visible for user with ID = userId
	 */
	public CollectionResponse<SimplePost> listSimplePostsForUser(Long userId);

	/**
	 * This method gets the entity having primary key id. It uses HTTP GET
	 * method.
	 * 
	 * @param id
	 *            the primary key of the java bean.
	 * @return The entity with primary key id.
	 */
	public SimplePost getSimplePost(Long id);

	/**
	 * @param simplepost
	 *            the entity to be updated.
	 * @return The updated entity.
	 * @throws Exception
	 */
	public SimplePost updateSimplePost(SimplePost simplepost);

	/**
	 * @param id
	 *            the primary key of the entity to be deleted.
	 * @throws Exception
	 */
	public void removeSimplePost(Long id);

}