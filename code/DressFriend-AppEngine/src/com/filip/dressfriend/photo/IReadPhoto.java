package com.filip.dressfriend.photo;

import com.filip.dressfriend.Photo;
import com.filip.dressfriend.SimplePost;

public interface IReadPhoto {

	public abstract SimplePost getPostOfPhoto(Long photo_id);

	/**
	 * This method gets the entity having primary key id. It uses HTTP GET
	 * method.
	 * 
	 * @param id
	 *            the primary key of the java bean.
	 * @return The entity with primary key id.
	 */
	public abstract Photo getPhoto(Long id);

}