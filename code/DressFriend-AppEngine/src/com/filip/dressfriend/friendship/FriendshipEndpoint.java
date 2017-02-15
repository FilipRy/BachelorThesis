package com.filip.dressfriend.friendship;

import com.filip.dressfriend.EMF;
import com.filip.dressfriend.Friendrequest;
import com.filip.dressfriend.Friendship;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

@Api(name = "friendshipendpoint", namespace = @ApiNamespace(ownerDomain = "filip.com", ownerName = "filip.com", packagePath = "dressfriend"))
public class FriendshipEndpoint {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.filip.dressfriend.FriendshipService#listFriendshipsOfUser(java.lang
	 * .Long, java.lang.String, java.lang.Integer)
	 */
	@ApiMethod(name = "listFriendshipsOfUser", path = "listFriendshipsOfUser")
	public CollectionResponse<Friendship> listFriendshipsOfUser(@Named("userId") Long userId,
			@Nullable @Named("cursor") String cursorString, @Nullable @Named("limit") Integer limit) {

		CollectionResponse<Friendship> execute = null;
		EntityManager mgr = null;

		try {
			mgr = getEntityManager();
			FriendshipService friendshipService = new FriendshipServiceImpl(mgr);
			execute = friendshipService.listFriendshipsOfUser(userId);
		} finally {
			if (mgr != null) {
				mgr.close();
			}
		}

		return execute;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.filip.dressfriend.FriendshipService#createFriendshipFromFriendrequest
	 * (com.filip.dressfriend.Friendrequest)
	 */
	@ApiMethod(name = "createFriendshipFromFriendrequest", path = "createFriendshipFromFriendrequest")
	public Friendship createFriendshipFromFriendrequest(Friendrequest fr) {

		EntityManager mgr = null;
		EntityTransaction tx = null;

		Friendship friendship = null;
		try {
			mgr = getEntityManager();
			tx = mgr.getTransaction();
			tx.begin();

			FriendshipService friendshipService = new FriendshipServiceImpl(mgr);
			friendship = friendshipService.createFriendshipFromFriendrequest(fr);
			
			mgr.getTransaction().commit();
		} finally {
			if (mgr != null) {
				mgr.close();
			}
		}

		return friendship;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.filip.dressfriend.FriendshipService#removeFriendship(java.lang.Long)
	 */
	@ApiMethod(name = "removeFriendship")
	public void removeFriendship(@Named("id") Long id) {

		EntityTransaction tx = null;
		EntityManager mgr = null;

		try {
			mgr = getEntityManager();
			tx = mgr.getTransaction();
			tx.begin();
			
			FriendshipService friendshipService = new FriendshipServiceImpl(mgr);
			friendshipService.removeFriendship(id);
			
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
