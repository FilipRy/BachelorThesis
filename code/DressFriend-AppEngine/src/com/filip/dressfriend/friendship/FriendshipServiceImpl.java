package com.filip.dressfriend.friendship;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.filip.dressfriend.Friendrequest;
import com.filip.dressfriend.Friendship;
import com.filip.dressfriend.User;
import com.filip.dressfriend.friendrequest.FriendrequestServiceImpl;
import com.filip.dressfriend.friendrequest.IRemoveFriendrequest;
import com.filip.dressfriend.user.IReadUser;
import com.filip.dressfriend.user.UserServiceImpl;
import com.google.api.server.spi.response.CollectionResponse;

public class FriendshipServiceImpl implements FriendshipService, ICreateFriendship, IReadFriendship, IRemoveFriendship {

	private EntityManager entityManager;

	public FriendshipServiceImpl(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	/* (non-Javadoc)
	 * @see com.filip.dressfriend.service.ICreateFriendship#insertFriendship(com.filip.dressfriend.Friendship)
	 */
	@Override
	public Friendship insertFriendship(Friendship friendship) {
		if (containsFriendship(friendship)) {
			throw new EntityExistsException("Object already exists");
		}

		boolean areUsersFriends = areUsersFriends(friendship.getUser1().getId(), friendship.getUser2()
				.getId());
		if (areUsersFriends) {
			throw new EntityExistsException("You are already friends!");
		}

		entityManager.persist(friendship);
		entityManager.flush();
		return friendship;
	}

	/* (non-Javadoc)
	 * @see com.filip.dressfriend.service.ICreateFriendship#createFriendshipFromFriendrequest(com.filip.dressfriend.Friendrequest)
	 */
	@Override
	public Friendship createFriendshipFromFriendrequest(Friendrequest fr) {

		IRemoveFriendrequest friendrequestService = new FriendrequestServiceImpl(entityManager);
		friendrequestService.removeFriendrequest(fr.getId());
		
		Friendship friendship = new Friendship();
		friendship.setUser1(fr.getUserTo());
		friendship.setUser2(fr.getUserFrom());
		friendship.setFriendsSince(new Date());
		
		friendship = insertFriendship(friendship);

		return friendship;
	}

	/* (non-Javadoc)
	 * @see com.filip.dressfriend.service.IReadFriendship#listFriendship()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public CollectionResponse<Friendship> listFriendship() {
		List<Friendship> execute = null;
		Query query = entityManager.createQuery("select f from Friendship f");

		execute = (List<Friendship>) query.getResultList();

		return CollectionResponse.<Friendship> builder().setItems(execute).build();
	}

	/* (non-Javadoc)
	 * @see com.filip.dressfriend.service.IReadFriendship#listFriendshipsOfUser(java.lang.Long)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public CollectionResponse<Friendship> listFriendshipsOfUser(Long userId) {
		List<Friendship> execute = null;

		entityManager.clear();// clear all values, which holds entity manager

		IReadUser userService = new UserServiceImpl(entityManager);
		User user = userService.getUser(userId);

		Query query = entityManager
				.createQuery("select f from Friendship f where f.user1 = :user1Id or f.user2 = :user2Id");
		query = query.setParameter("user1Id", user);
		query = query.setParameter("user2Id", user);

		execute = (List<Friendship>) query.getResultList();

		return CollectionResponse.<Friendship> builder().setItems(execute).build();
	}

	/* (non-Javadoc)
	 * @see com.filip.dressfriend.service.IReadFriendship#areUsersFriends(java.lang.Long, java.lang.Long)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean areUsersFriends(Long user1Id, Long user2Id) {
		List<Friendship> execute = null;

		IReadUser userEndpoint = new UserServiceImpl(entityManager);
		User user1 = userEndpoint.getUser(user1Id);
		User user2 = userEndpoint.getUser(user2Id);

		Query query = entityManager
				.createQuery("select f from Friendship f where f.user1 = :user1Id and f.user2 = :user2Id or f.user1 = :user11Id and f.user2 = :user12Id");
		query = query.setParameter("user1Id", user1);
		query = query.setParameter("user2Id", user2);
		query = query.setParameter("user11Id", user2);
		query = query.setParameter("user12Id", user1);

		execute = (List<Friendship>) query.getResultList();

		return execute != null && execute.size() > 0;
	}

	/* (non-Javadoc)
	 * @see com.filip.dressfriend.service.IRemoveFriendship#removeFriendship(java.lang.Long)
	 */
	@Override
	public void removeFriendship(Long id) {
		Friendship friendship = entityManager.find(Friendship.class, id);
		entityManager.remove(friendship);
	}

	private boolean containsFriendship(Friendship friendship) {
		boolean contains = true;
		if (friendship.getId() == null) {
			return false;
		}
		Friendship item = entityManager.find(Friendship.class, friendship.getId());
		if (item == null) {
			contains = false;
		}
		return contains;
	}

}
