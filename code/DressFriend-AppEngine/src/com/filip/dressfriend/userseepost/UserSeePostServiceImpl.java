package com.filip.dressfriend.userseepost;

import com.filip.dressfriend.UserSeePost;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.Query;


public class UserSeePostServiceImpl implements UserSeePostService, ICreateUserSeePost, IReadUserSeePost, IRemoveUserSeePost {

	private EntityManager entityManager;

	public UserSeePostServiceImpl(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	/* (non-Javadoc)
	 * @see com.filip.dressfriend.userseepost.IReadUserSeePost#existsUserSeePost(java.lang.Long, java.lang.Long)
	 */
	@Override
	public boolean existsUserSeePost(Long userId, Long postId) {
		Query query = entityManager.createNativeQuery("SELECT EXISTS(SELECT * FROM USERSEEPOST WHERE USERSEEPOST.POST_ID=? AND USERSEEPOST.USER_ID=?)");
		query = query.setParameter(1, postId);
		query = query.setParameter(2, userId);
		long count = (long) query.getSingleResult();
		return count > 0;
		
	}


	/* (non-Javadoc)
	 * @see com.filip.dressfriend.userseepost.ICreateUserSeePost#insertUserSeePost(com.filip.dressfriend.UserSeePost)
	 */
	@Override
	public UserSeePost insertUserSeePost(UserSeePost userseepost) {
		try {
			if (containsUserSeePost(userseepost, entityManager)) {
				throw new EntityExistsException("Object already exists");
			}
			entityManager.persist(userseepost);
			entityManager.flush();
			
		} finally {
			
		}
		return userseepost;
	}

	/* (non-Javadoc)
	 * @see com.filip.dressfriend.userseepost.IRemoveUserSeePost#removeUserSeeForPost(java.lang.Long)
	 */
	@Override
	public void removeUserSeeForPost(Long id) {
		try {
			Query nativeQuery = entityManager.createNativeQuery("DELETE FROM USERSEEPOST WHERE POST_ID=?");
			nativeQuery = nativeQuery.setParameter(1, id);

			nativeQuery.executeUpdate();
		} finally {
			
		}
	}

	private boolean containsUserSeePost(UserSeePost userseepost, EntityManager mgr) {
		boolean contains = true;
		if (userseepost.getId() == null) {
			return false;
		}

		UserSeePost item = mgr.find(UserSeePost.class, userseepost.getId());
		if (item == null) {
			contains = false;
		}

		return contains;
	}

}
