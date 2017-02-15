package com.filip.dressfriend.photo;

import com.filip.dressfriend.Photo;
import com.filip.dressfriend.SimplePost;

public interface ICreatePhoto {

	/**
	 * This inserts a new entity into App Engine datastore. If the entity
	 * already exists in the datastore, an exception is thrown. It uses HTTP
	 * POST method.
	 * 
	 * @param photo
	 *            the entity to be inserted.
	 * @return The inserted entity.
	 */
	public abstract Photo insertPhoto(Photo photo, SimplePost parent);

}