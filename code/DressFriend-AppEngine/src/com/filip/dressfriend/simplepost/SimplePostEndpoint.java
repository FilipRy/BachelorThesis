package com.filip.dressfriend.simplepost;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import com.filip.dressfriend.EMF;
import com.filip.dressfriend.SimplePost;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;

@Api(name = "simplepostendpoint", namespace = @ApiNamespace(ownerDomain = "filip.com", ownerName = "filip.com", packagePath = "dressfriend"))
public class SimplePostEndpoint {

	@ApiMethod(name = "listSimplePostsOfUser", path = "listSimplePostsOfUser")
	public CollectionResponse<SimplePost> listSimplePostsOfUser(@Named("userId") Long userId,
			@Nullable @Named("cursor") String cursorString, @Nullable @Named("limit") Integer limit) {

		EntityManager mgr = null;
		CollectionResponse<SimplePost> execute = null;

		try {
			mgr = getEntityManager();

			SimplePostService simplePostService = new SimplePostServiceImpl(mgr);
			execute = simplePostService.listSimplePostsOfUser(userId);

		} finally {
			if (mgr != null) {
				mgr.close();
			}
		}

		return execute;
	}

	@ApiMethod(name = "listSimplePostsForUser", path = "listSimplePostsForUser")
	public CollectionResponse<SimplePost> listSimplePostsForUser(@Named("userId") Long userId,
			@Nullable @Named("cursor") String cursorString, @Nullable @Named("limit") Integer limit) {

		EntityManager mgr = null;
		CollectionResponse<SimplePost> execute = null;

		try {
			mgr = getEntityManager();
			SimplePostService simplePostService = new SimplePostServiceImpl(mgr);
			execute = simplePostService.listSimplePostsForUser(userId);
			
		} finally {
			if (mgr != null) {
				mgr.close();
			}
		}

		return execute;
	}

	/**
	 * This inserts a new entity into App Engine datastore. If the entity
	 * already exists in the datastore, an exception is thrown. It uses HTTP
	 * POST method.
	 * 
	 * @param simplepost
	 *            the entity to be inserted.
	 * @return The inserted entity.
	 */
	@ApiMethod(name = "insertSimplePost")
	public SimplePost insertSimplePost(SimplePost simplepost) {
		EntityManager mgr = null;
		EntityTransaction tx = null;

		try {
			mgr = getEntityManager();
			tx = mgr.getTransaction();
			tx.begin();
			
			SimplePostService simplePostService = new SimplePostServiceImpl(mgr);
			simplepost = simplePostService.insertSimplePost(simplepost);
			
			mgr.getTransaction().commit();

		} finally {
			if (mgr != null) {
				mgr.close();
			}
		}
		return simplepost;
	}

	/**
	 * This method is used for updating an existing entity. If the entity does
	 * not exist in the datastore, an exception is thrown. It uses HTTP PUT
	 * method.
	 * 
	 * @param simplepost
	 *            the entity to be updated.
	 * @return The updated entity.
	 * @throws Exception
	 */
	@ApiMethod(name = "updateSimplePost")
	public SimplePost updateSimplePost(SimplePost simplepost) throws Exception {
		EntityManager mgr = null;
		EntityTransaction tx = null;

		try {
			mgr = getEntityManager();
			tx = mgr.getTransaction();
			tx.begin();
			
			SimplePostService simplePostService = new SimplePostServiceImpl(mgr);
			simplepost = simplePostService.updateSimplePost(simplepost);
			
			mgr.getTransaction().commit();
			
		} finally {
			if (mgr != null) {
				mgr.close();
			}
		}
		return simplepost;
	}

	/**
	 * This method removes the entity with primary key id. It uses HTTP DELETE
	 * method.
	 * 
	 * @param id
	 *            the primary key of the entity to be deleted.
	 * @throws Exception
	 */
	@ApiMethod(name = "removeSimplePost")
	public void removeSimplePost(@Named("id") Long id) throws Exception {
		EntityManager mgr = null;
		EntityTransaction tx = null;

		try {
			
			mgr = getEntityManager();
			tx = mgr.getTransaction();
			tx.begin();
			
			SimplePostService simplePostService = new SimplePostServiceImpl(mgr);
			simplePostService.removeSimplePost(id);
			
			mgr.getTransaction().commit();
		} finally {
			if (mgr != null) {
				mgr.close();
			}
		}
	}
	private static EntityManager getEntityManager() {
		return EMF.get().createEntityManager();
	}
}
