package com.filip.dressfriend.photo;

import com.filip.dressfriend.Photo;
import com.filip.dressfriend.SimplePost;
import com.filip.dressfriend.comment.CommentServiceImpl;
import com.filip.dressfriend.comment.IReadComment;
import com.filip.dressfriend.dislikes.DislikesServiceImpl;
import com.filip.dressfriend.dislikes.IReadDislikes;
import com.filip.dressfriend.likes.IReadLikes;
import com.filip.dressfriend.likes.LikesServiceImpl;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;

public class PhotoServiceImpl implements PhotoService, ICreatePhoto, IReadPhoto, IRemovePhoto {

	private EntityManager entityManager;

	public PhotoServiceImpl(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	/* (non-Javadoc)
	 * @see com.filip.dressfriend.service.IReadPhoto#getPostOfPhoto(java.lang.Long)
	 */
	@Override
	public SimplePost getPostOfPhoto(Long photo_id) {

		Photo photo = entityManager.find(Photo.class, photo_id);
		if(photo != null) {
			return photo.getSimplePost();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.filip.dressfriend.service.IReadPhoto#getPhoto(java.lang.Long)
	 */
	@Override
	public Photo getPhoto(Long id) {
		Photo photo = null;
		IReadLikes likesService = new LikesServiceImpl(entityManager);
		IReadDislikes dislikesService = new DislikesServiceImpl(entityManager);
		IReadComment commentService = new CommentServiceImpl(entityManager);

		photo = entityManager.find(Photo.class, id);
		photo.setLikesCount(likesService.getPhotoLikesCount(photo.getId()));
		photo.setDislikesCount(dislikesService.getPhotoDislikesCount(photo.getId()));
		photo.setCommentsCount(commentService.getPhotoCommentsCount(photo.getId()));

		return photo;
	}

	/* (non-Javadoc)
	 * @see com.filip.dressfriend.service.ICreatePhoto#insertPhoto(com.filip.dressfriend.Photo, com.filip.dressfriend.SimplePost)
	 */
	@Override
	public Photo insertPhoto(Photo photo, SimplePost parent) {

		photo.setSimplePost(parent);
		if (containsPhoto(photo, entityManager)) {
			throw new EntityExistsException("Object already exists");
		}
		entityManager.persist(photo);
		entityManager.flush();

		return photo;
	}

	/* (non-Javadoc)
	 * @see com.filip.dressfriend.service.IRemovePhoto#removePhoto(java.lang.Long)
	 */
	@Override
	public void removePhoto(Long id) {

		Photo photo = entityManager.find(Photo.class, id);

		/**
		 * this delete the Photos from the Cloud Storage
		 */
		//CloudStorageServiceImpl.instance().deletePhoto(photo);
		
		entityManager.remove(photo);

	}

	private boolean containsPhoto(Photo photo, EntityManager mgr) {

		boolean contains = true;

		if (photo.getId() == null) {
			return false;
		}

		Photo item = mgr.find(Photo.class, photo.getId());
		if (item == null) {
			contains = false;
		}

		return contains;
	}

}
