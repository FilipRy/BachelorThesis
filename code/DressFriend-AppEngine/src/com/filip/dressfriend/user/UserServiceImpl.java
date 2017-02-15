package com.filip.dressfriend.user;

import java.util.Date;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import com.filip.dressfriend.User;

public class UserServiceImpl implements UserService, ICreateUser, IReadUser {

	private EntityManager entityManager;

	public UserServiceImpl(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	/* (non-Javadoc)
	 * @see com.filip.dressfriend.user.ICreateUser#insertUser(com.filip.dressfriend.User)
	 */
	@Override
	public User insertUser(User user) {

		if (containsUser(user)) {
			throw new EntityExistsException("Object already exists");
		}
		user.setSignUpDate(new Date());

		entityManager.persist(user);
		entityManager.flush();

		return user;
	}

	
	/* (non-Javadoc)
	 * @see com.filip.dressfriend.user.IReadUser#getUser(java.lang.Long)
	 */
	@Override
	public User getUser(Long id) {
		User user = null;
		user = entityManager.find(User.class, id);
		entityManager.refresh(user);
		return user;
	}

	

	private boolean containsUser(User user) {
		boolean contains = true;
		if (user.getId() == null) {
			return false;
		}
		User item = entityManager.find(User.class, user.getId());
		if (item == null) {
			contains = false;
		}
		return contains;
	}


}
