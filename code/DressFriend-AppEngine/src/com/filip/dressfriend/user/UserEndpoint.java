package com.filip.dressfriend.user;

import com.filip.dressfriend.EMF;
import com.filip.dressfriend.User;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;

import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

@Api(name = "userendpoint", namespace = @ApiNamespace(ownerDomain = "filip.com", ownerName = "filip.com", packagePath = "dressfriend"))
public class UserEndpoint {


	/**
	 * This inserts a new entity into App Engine datastore. If the entity
	 * already exists in the datastore, an exception is thrown. It uses HTTP
	 * POST method.
	 * 
	 * @param user
	 *            the entity to be inserted.
	 * @return The inserted entity.
	 */
	@ApiMethod(name = "insertUser")
	public User insertUser(User user) {
		EntityManager mgr = null;
		EntityTransaction tx = null;

		try {
			mgr = getEntityManager();
			tx = mgr.getTransaction();
			tx.begin();

			UserService userService = new UserServiceImpl(mgr);
			user = userService.insertUser(user);

			mgr.getTransaction().commit();
		} finally {
			if (mgr != null) {
				mgr.close();
			}
		}
		return user;
	}

	@ApiMethod(name = "getUser")
	public User getUser(@Named("id") Long userId) {
		EntityManager mgr = null;
		User user = null;
		try {
			mgr = getEntityManager();
			UserService userService = new UserServiceImpl(mgr);
			user = userService.getUser(userId);
		} finally {
			if (mgr != null) {
				mgr.close();
			}
		}
		return user;
	}

	

	private static EntityManager getEntityManager() {
		return EMF.get().createEntityManager();
	}

}
