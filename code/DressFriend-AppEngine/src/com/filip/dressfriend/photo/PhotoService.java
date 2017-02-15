package com.filip.dressfriend.photo;

import com.filip.dressfriend.Photo;
import com.filip.dressfriend.SimplePost;

public interface PhotoService {

	public SimplePost getPostOfPhoto(Long photo_id);

	/**
	 * This method gets the entity having primary key id. It uses HTTP GET
	 * method.
	 * 
	 * @param id
	 *            the primary key of the java bean.
	 * @return The entity with primary key id.
	 */
	public Photo getPhoto(Long id);

	/**
	 * This inserts a new entity into App Engine datastore. If the entity
	 * already exists in the datastore, an exception is thrown. It uses HTTP
	 * POST method.
	 * 
	 * @param photo
	 *            the entity to be inserted.
	 * @return The inserted entity.
	 */
	public Photo insertPhoto(Photo photo, SimplePost parent);

	/**
	 * This method removes the entity with primary key id. It uses HTTP DELETE
	 * method.
	 * 
	 * @param id
	 *            the primary key of the entity to be deleted.
	 */
	public void removePhoto(Long id);

}