package com.filip.dressfriend.comment;

import com.filip.dressfriend.Comment;
import com.filip.dressfriend.EMF;
import com.filip.dressfriend.exception.UnAuthorziedCommentException;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

@Api(name = "commentendpoint", namespace = @ApiNamespace(ownerDomain = "filip.com", ownerName = "filip.com", packagePath = "dressfriend"))
public class CommentEndpoint {

	/**
	 * This inserts a new entity into App Engine datastore. If the entity
	 * already exists in the datastore, an exception is thrown. It uses HTTP
	 * POST method.
	 * 
	 * @param comment
	 *            the entity to be inserted.
	 * @return The inserted entity.
	 * @throws UnAuthorziedCommentException 
	 */
	@ApiMethod(name = "insertComment")
	public Comment insertComment(Comment comment) throws UnAuthorziedCommentException {
		EntityManager mgr = null;
		EntityTransaction tx = null;

		try {
			mgr = getEntityManager();
			tx = mgr.getTransaction();
			tx.begin();

			ICreateComment commentService = new CommentServiceImpl(mgr);
			comment = commentService.insertComment(comment);

			mgr.getTransaction().commit();

		} finally {
			if (mgr != null) {
				mgr.close();
			}
		}
		return comment;
	}

	@ApiMethod(name = "listCommentsOfPhoto", path = "listCommentsOfPhoto")
	public CollectionResponse<Comment> listCommentsOfPhoto(
			@Named("photoId") Long photoId,
			@Named("commentsCount") Integer commentsCount,
			@Named("commentsCountHashCode") Integer commentsCountHashCode,
			@Nullable @Named("cursor") String cursorString, @Nullable @Named("limit") Integer limit) {

		EntityManager mgr = null;
		CollectionResponse<Comment> comments = null;
		try {
			mgr = getEntityManager();

			CommentService commentService = new CommentServiceImpl(mgr);
			comments = commentService.listNewCommentsOfPhoto(photoId, commentsCount, commentsCountHashCode);

		} finally {
			if (mgr != null) {
				mgr.close();
			}
		}

		return comments;
	}

	private static EntityManager getEntityManager() {
		return EMF.get().createEntityManager();
	}

}
