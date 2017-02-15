package com.filip.dressfriend.friendrequest;

import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.filip.dressfriend.Friendrequest;
import com.filip.dressfriend.User;
import com.filip.dressfriend.user.IReadUser;
import com.filip.dressfriend.user.UserServiceImpl;
import com.google.api.server.spi.response.CollectionResponse;

public class FriendrequestServiceImpl implements FriendrequestService, ICreateFriendrequest, IReadFriendrequest, IRemoveFriendrequest{

	private EntityManager entityManager;

	public FriendrequestServiceImpl(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	/* (non-Javadoc)
	 * @see com.filip.dressfriend.service.ICreateFriendrequest#insertFriendrequest(com.filip.dressfriend.Friendrequest)
	 */
	public Friendrequest insertFriendrequest(Friendrequest friendrequest) {
		if (containsFriendrequest(friendrequest)) {
			throw new EntityExistsException("Object already exists");
		}
		boolean wasSent = isFriendrequestSent(friendrequest.getUserFrom().getId(), friendrequest.getUserTo()
				.getId());
		if (wasSent) {
			throw new EntityExistsException("Friendrequest has been already sent!");
		}
		entityManager.persist(friendrequest);
		entityManager.flush();
		return friendrequest;
	}

	/* (non-Javadoc)
	 * @see com.filip.dressfriend.service.IReadFriendrequest#listFriendrequest()
	 */
	@SuppressWarnings("unchecked")
	public CollectionResponse<Friendrequest> listFriendrequest() {
		List<Friendrequest> execute = null;
		Query query = entityManager.createQuery("select f from Friendrequest f");
		execute = (List<Friendrequest>) query.getResultList();
		return CollectionResponse.<Friendrequest> builder().setItems(execute).build();
	}

	/* (non-Javadoc)
	 * @see com.filip.dressfriend.service.IReadFriendrequest#listFriendrequestsFromUser(java.lang.Long)
	 */
	@SuppressWarnings("unchecked")
	public CollectionResponse<Friendrequest> listFriendrequestsFromUser(Long userId) {
		List<Friendrequest> execute = null;

		IReadUser userService = new UserServiceImpl(entityManager);
		User user = userService.getUser(userId);

		Query query = entityManager
				.createQuery("select f from Friendrequest f where f.userFrom = :userFromId");
		query = query.setParameter("userFromId", user);
		execute = (List<Friendrequest>) query.getResultList();
		return CollectionResponse.<Friendrequest> builder().setItems(execute).build();
	}

	/* (non-Javadoc)
	 * @see com.filip.dressfriend.service.IReadFriendrequest#listFriendrequestsToUser(java.lang.Long)
	 */
	@SuppressWarnings("unchecked")
	public CollectionResponse<Friendrequest> listFriendrequestsToUser(Long userId) {
		List<Friendrequest> execute = null;

		IReadUser userService = new UserServiceImpl(entityManager);
		User user = userService.getUser(userId);

		Query query = entityManager.createQuery("select f from Friendrequest f where f.userTo = :userToId");
		query = query.setParameter("userToId", user);
		execute = (List<Friendrequest>) query.getResultList();
		return CollectionResponse.<Friendrequest> builder().setItems(execute).build();
	}

	/* (non-Javadoc)
	 * @see com.filip.dressfriend.service.IReadFriendrequest#isFriendrequestSent(java.lang.Long, java.lang.Long)
	 */
	@SuppressWarnings("unchecked")
	public boolean isFriendrequestSent(Long user1Id, Long user2Id) {
		List<Friendrequest> execute = null;

		IReadUser userService = new UserServiceImpl(entityManager);
		User user1 = userService.getUser(user1Id);
		User user2 = userService.getUser(user2Id);

		Query query = entityManager
				.createQuery("select f from Friendrequest f where f.userTo = :userToId and f.userFrom = :userFromId or f.userTo = :userToId1 and f.userFrom = :userFromId1");
		query = query.setParameter("userToId", user1);
		query = query.setParameter("userFromId", user2);
		query = query.setParameter("userToId1", user2);
		query = query.setParameter("userFromId1", user1);

		execute = (List<Friendrequest>) query.getResultList();

		return execute != null && execute.size() > 0;

	}

	/* (non-Javadoc)
	 * @see com.filip.dressfriend.service.IReadFriendrequest#getFriendrequest(java.lang.Long)
	 */
	public Friendrequest getFriendrequest(Long id) {
		return entityManager.find(Friendrequest.class, id);
	}

	/* (non-Javadoc)
	 * @see com.filip.dressfriend.service.IRemoveFriendrequest#removeFriendrequest(java.lang.Long)
	 */
	public void removeFriendrequest(Long id) {
		Friendrequest friendrequest = entityManager.find(Friendrequest.class, id);
		entityManager.remove(friendrequest);
	}

	private boolean containsFriendrequest(Friendrequest friendrequest) {
		boolean contains = true;
		if (friendrequest.getId() == null) {
			return false;
		}
		Friendrequest item = entityManager.find(Friendrequest.class, friendrequest.getId());
		if (item == null) {
			contains = false;
		}
		return contains;
	}

}
