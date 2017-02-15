package com.filip.dressfriend.likes;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import com.filip.dressfriend.EMF;
import com.filip.dressfriend.Likes;
import com.filip.dressfriend.exception.DislikeAlreadyExistsException;
import com.filip.dressfriend.exception.LikeAlreadyExistsException;
import com.filip.dressfriend.exception.UnauthorizedDisLikeException;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;

@Api(name = "likesendpoint", namespace = @ApiNamespace(ownerDomain = "filip.com", ownerName = "filip.com", packagePath = "dressfriend"))
public class LikesEndpoint {

	/**
	 * This inserts a new entity into App Engine datastore. If the entity
	 * already exists in the datastore, an exception is thrown. It uses HTTP
	 * POST method.
	 * 
	 * @param likes
	 *            the entity to be inserted.
	 * @return The inserted entity.
	 * @throws DislikeAlreadyExistsException
	 * @throws LikeAlreadyExistsException
	 * @throws UnauthorizedDisLikeException
	 */
	@ApiMethod(name = "insertLikes")
	public Likes insertLikes(Likes likes) throws DislikeAlreadyExistsException, LikeAlreadyExistsException,
			UnauthorizedDisLikeException {

		EntityManager mgr = null;
		EntityTransaction tx = null;

		try {
			mgr = getEntityManager();
			tx = mgr.getTransaction();
			tx.begin();

			LikesService likesService = new LikesServiceImpl(mgr);
			likes = likesService.insertLikes(likes);

			mgr.getTransaction().commit();
		} finally {
			if (mgr != null) {
				mgr.close();
			}
		}
		return likes;
	}
	
	@ApiMethod(name = "listLikesOfPhoto", path = "listLikesOfPhoto")
	public CollectionResponse<Likes> listLikesOfPhoto(
			@Named("photoId") Long photoId,
			@Named("likesCount") Integer likesCount,
			@Named("likesCountHashCode") Integer likesCountHashCode,
			@Nullable @Named("cursor") String cursorString, @Nullable @Named("limit") Integer limit) {

		EntityManager mgr = null;
		CollectionResponse<Likes> execute = null;

		try {
			mgr = getEntityManager();
			LikesService likesService = new LikesServiceImpl(mgr);
			execute = likesService.listLikesOfPhoto(photoId, likesCount, likesCountHashCode);
		} finally {
			if (mgr != null) {
				mgr.close();
			}
		}

		return execute;
	}

	
	private static EntityManager getEntityManager() {
		return EMF.get().createEntityManager();
	}

}
