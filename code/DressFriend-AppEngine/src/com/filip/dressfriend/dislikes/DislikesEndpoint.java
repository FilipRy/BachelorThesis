package com.filip.dressfriend.dislikes;

import com.filip.dressfriend.Dislikes;
import com.filip.dressfriend.EMF;
import com.filip.dressfriend.exception.DislikeAlreadyExistsException;
import com.filip.dressfriend.exception.LikeAlreadyExistsException;
import com.filip.dressfriend.exception.UnauthorizedDisLikeException;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

@Api(name = "dislikesendpoint", namespace = @ApiNamespace(ownerDomain = "filip.com", ownerName = "filip.com", packagePath = "dressfriend"))
public class DislikesEndpoint {

	/**
	 * This inserts a new entity into App Engine datastore. If the entity
	 * already exists in the datastore, an exception is thrown. It uses HTTP
	 * POST method.
	 * 
	 * @param dislikes
	 *            the entity to be inserted.
	 * @return The inserted entity.
	 * @throws DislikeOnVotingPostException
	 * @throws DislikeAlreadyExistsException
	 * @throws LikeAlreadyExistsException
	 * @throws UnauthorizedDisLikeException
	 */
	@ApiMethod(name = "insertDislikes")
	public Dislikes insertDislikes(Dislikes dislikes) throws DislikeAlreadyExistsException, LikeAlreadyExistsException, UnauthorizedDisLikeException {
		
		EntityManager mgr = null;
		EntityTransaction tx = null;

		try {
			mgr = getEntityManager();
			tx = mgr.getTransaction();
			tx.begin();
			
			DislikesService dislikesService = new DislikesServiceImpl(mgr);
			dislikes = dislikesService.insertDislikes(dislikes);
			
			mgr.getTransaction().commit();
			
		} finally {
			if (mgr != null) {
				mgr.close();
			}
		}
		return dislikes;
	}

	
	@ApiMethod(name = "listDislikesOfPhoto", path = "listDislikesOfPhoto")
	public CollectionResponse<Dislikes> listDislikesOfPhoto(
			@Named("photoId") Long photoId,
			@Named("dislikesCount") Integer dislikesCount,
			@Named("dislikesCountHashCode") Integer hashCode,
			@Nullable @Named("cursor") String cursorString, @Nullable @Named("limit") Integer limit) {

		EntityManager mgr = null;
		CollectionResponse<Dislikes> dislikes = null;
		try {
			mgr = getEntityManager();

			DislikesService dislikesService = new DislikesServiceImpl(mgr);
			dislikes = dislikesService.listDislikesOfPhoto(photoId, dislikesCount, hashCode);

		} finally {
			if (mgr != null) {
				mgr.close();
			}
		}
		
		return dislikes;
	}

	private static EntityManager getEntityManager() {
		return EMF.get().createEntityManager();
	}

}
