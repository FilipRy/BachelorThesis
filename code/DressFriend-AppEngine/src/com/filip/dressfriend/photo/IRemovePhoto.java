package com.filip.dressfriend.photo;

public interface IRemovePhoto {

	/**
	 * This method removes the entity with primary key id. It uses HTTP DELETE
	 * method.
	 * 
	 * @param id
	 *            the primary key of the entity to be deleted.
	 */
	public abstract void removePhoto(Long id);

}