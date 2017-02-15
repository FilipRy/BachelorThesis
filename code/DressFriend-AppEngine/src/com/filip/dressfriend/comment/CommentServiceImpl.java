package com.filip.dressfriend.comment;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.filip.dressfriend.Comment;
import com.filip.dressfriend.Photo;
import com.filip.dressfriend.SimplePost;
import com.filip.dressfriend.exception.UnAuthorziedCommentException;
import com.filip.dressfriend.photo.IReadPhoto;
import com.filip.dressfriend.photo.PhotoServiceImpl;
import com.filip.dressfriend.userseepost.IReadUserSeePost;
import com.filip.dressfriend.userseepost.UserSeePostServiceImpl;
import com.google.api.server.spi.response.CollectionResponse;

public class CommentServiceImpl implements CommentService, ICreateComment, IReadComment {

	private EntityManager entityManager;

	public CommentServiceImpl(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public Comment insertComment(Comment comment) throws UnAuthorziedCommentException {

		if (containsComment(comment)) {
			throw new EntityExistsException("Object already exists");
		}
		
		IReadPhoto photoService = new PhotoServiceImpl(entityManager);
		IReadUserSeePost userSeePostService = new UserSeePostServiceImpl(entityManager);
		
		SimplePost sp = photoService.getPostOfPhoto(comment.getPhoto().getId());
		
		if(!userSeePostService.existsUserSeePost(comment.getUser().getId(), sp.getId())) {
			throw new UnAuthorziedCommentException();
		}
		
		entityManager.persist(comment);
		entityManager.flush();
		return comment;
	}

	/* (non-Javadoc)
	 * @see com.filip.dressfriend.service.IReadComment#listNewCommentsOfPhoto(java.lang.Long, int, int)
	 */
	@SuppressWarnings("unchecked")
	public CollectionResponse<Comment> listNewCommentsOfPhoto(Long photoId, int clientCommentsCount, int clientCommentsHashCode) {
		List<Comment> execute = null;
		
		int persistCommentCount = getPhotoCommentsCount(photoId);
		//there is no new comment for the client
		//TODO hashCode
		if(persistCommentCount == clientCommentsCount) {
			execute = new ArrayList<>();
		} else {
			IReadPhoto photoService = new PhotoServiceImpl(entityManager);
			Photo photo = photoService.getPhoto(photoId);
			Query query = entityManager.createQuery("select c from Comment c where c.photo = :photoId");
			query = query.setParameter("photoId", photo);
			execute = (List<Comment>) query.getResultList();
			if(execute == null || execute.size() == 0) {
				//c indicates that there are no Comment at this photo anymore
				Comment c = new Comment();
				c.setId(-11l);
				execute.add(c);
			}
		}

		return CollectionResponse.<Comment> builder().setItems(execute).build();
	}
	

	/* (non-Javadoc)
	 * @see com.filip.dressfriend.service.IReadComment#getPhotoCommentsCount(java.lang.Long)
	 */
	public int getPhotoCommentsCount(Long photo_id) {
		Query query = entityManager.createNativeQuery("SELECT COUNT(*) FROM COMMENT d WHERE d.PHOTO_ID = ?");
		query = query.setParameter(1, photo_id);

		return ((Long) query.getSingleResult()).intValue();
	}

	private boolean containsComment(Comment comment) {
		boolean contains = true;

		if (comment.getId() == null) {
			return false;
		}

		Comment item = entityManager.find(Comment.class, comment.getId());
		if (item == null) {
			contains = false;
		}

		return contains;
	}

}
