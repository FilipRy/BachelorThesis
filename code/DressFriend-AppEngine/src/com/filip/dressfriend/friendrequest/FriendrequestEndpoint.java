package com.filip.dressfriend.friendrequest;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import com.filip.dressfriend.EMF;
import com.filip.dressfriend.Friendrequest;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;

@Api(name = "friendrequestendpoint", namespace = @ApiNamespace(ownerDomain = "filip.com", ownerName = "filip.com", packagePath = "dressfriend"))
public class FriendrequestEndpoint {

	/**
	 * This inserts a new entity into App Engine datastore. If the entity
	 * already exists in the datastore, an exception is thrown. It uses HTTP
	 * POST method.
	 * 
	 * @param friendrequest
	 *            the entity to be inserted.
	 * @return The inserted entity.
	 */
	@ApiMethod(name = "insertFriendrequest")
	public Friendrequest insertFriendrequest(Friendrequest friendrequest) {
		EntityManager mgr = null;
		EntityTransaction tx = null;

		try {
			mgr = getEntityManager();
			tx = mgr.getTransaction();
			tx.begin();

			FriendrequestService friendrequestService = new FriendrequestServiceImpl(mgr);
			friendrequest = friendrequestService.insertFriendrequest(friendrequest);
			
			mgr.getTransaction().commit();
		} finally {
			if (mgr != null) {
				mgr.close();
			}
		}
		return friendrequest;
	}

	@ApiMethod(name = "listFriendrequestsToUser", path = "listFriendrequestsToUser")
	public CollectionResponse<Friendrequest> listFriendrequestsToUser(@Named("userId") Long userId,
			@Nullable @Named("cursor") String cursorString, @Nullable @Named("limit") Integer limit) {

		EntityManager mgr = null;
		CollectionResponse<Friendrequest> execute = null;

		try {
			mgr = getEntityManager();

			FriendrequestService friendrequestService = new FriendrequestServiceImpl(mgr);
			execute = friendrequestService.listFriendrequestsToUser(userId);

		} finally {
			if (mgr != null) {
				mgr.close();
			}
		}
		return execute;

	}
	
	@ApiMethod(name = "listFriendrequestsFromUser", path = "listFriendrequestsFromUser")
	public CollectionResponse<Friendrequest> listFriendrequestsFromUser(@Named("userId") Long userId,
			@Nullable @Named("cursor") String cursorString, @Nullable @Named("limit") Integer limit) {

		EntityManager mgr = null;
		CollectionResponse<Friendrequest> execute = null;

		try {
			mgr = getEntityManager();
			FriendrequestService friendrequestService = new FriendrequestServiceImpl(mgr);
			execute = friendrequestService.listFriendrequestsFromUser(userId);
		} finally {
			if (mgr != null) {
				mgr.close();
			}
		}
		return execute;

	}

	/**
	 * This method removes the entity with primary key id. It uses HTTP DELETE
	 * method.
	 * 
	 * @param id
	 *            the primary key of the entity to be deleted.
	 */
	@ApiMethod(name = "removeFriendrequest")
	public void removeFriendrequest(@Named("id") Long id) {

		EntityManager mgr = null;
		EntityTransaction tx = null;

		try {
			mgr = getEntityManager();
			tx = mgr.getTransaction();
			tx.begin();
			
			FriendrequestService friendrequestService = new FriendrequestServiceImpl(mgr);
			friendrequestService.removeFriendrequest(id);

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
