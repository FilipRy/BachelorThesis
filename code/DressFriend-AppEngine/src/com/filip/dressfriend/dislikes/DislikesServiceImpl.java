package com.filip.dressfriend.dislikes;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.filip.dressfriend.Dislikes;
import com.filip.dressfriend.Photo;
import com.filip.dressfriend.SimplePost;
import com.filip.dressfriend.User;
import com.filip.dressfriend.exception.DislikeAlreadyExistsException;
import com.filip.dressfriend.exception.LikeAlreadyExistsException;
import com.filip.dressfriend.exception.UnauthorizedDisLikeException;
import com.filip.dressfriend.likes.IReadLikes;
import com.filip.dressfriend.likes.LikesServiceImpl;
import com.filip.dressfriend.photo.IReadPhoto;
import com.filip.dressfriend.photo.PhotoServiceImpl;
import com.filip.dressfriend.simplepost.IReadSimplePost;
import com.filip.dressfriend.simplepost.SimplePostServiceImpl;
import com.filip.dressfriend.user.IReadUser;
import com.filip.dressfriend.user.UserServiceImpl;
import com.filip.dressfriend.userseepost.IReadUserSeePost;
import com.filip.dressfriend.userseepost.UserSeePostServiceImpl;
import com.google.api.server.spi.response.CollectionResponse;

/**
 * This test case is created for the needs of Bachelor thesis: "Model based testing of cloud based social networks"
 * @author Filip Rydzi  (C)
 *
 */


public class DislikesServiceImpl implements DislikesService, ICreateDislikes, IReadDislikes {

	private EntityManager entityManager;

	public DislikesServiceImpl(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	/* (non-Javadoc)
	 * @see com.filip.dressfriend.service.ICreateDislikes#insertDislikes(com.filip.dressfriend.Dislikes)
	 */
	public Dislikes insertDislikes(Dislikes dislikes) throws LikeAlreadyExistsException,
			DislikeAlreadyExistsException, UnauthorizedDisLikeException {

		if (containsDislikes(dislikes)) {
			throw new EntityExistsException("Object already exists");
		}

		IReadPhoto photoService = new PhotoServiceImpl(entityManager);
		IReadLikes likesService = new LikesServiceImpl(entityManager);
		IReadUserSeePost userSeePostService = new UserSeePostServiceImpl(entityManager);

		SimplePost sp = photoService.getPostOfPhoto(dislikes.getPhoto().getId());

		if (!userSeePostService.existsUserSeePost(dislikes.getUser().getId(), sp.getId())) {
			throw new UnauthorizedDisLikeException("Request to dislike for user invisible photo");
		}

		if (existsPostsDislikesByUser(dislikes.getUser().getId(), sp.getId())) {
			throw new DislikeAlreadyExistsException("There is already a dislike at this photo by this user");
		}

		if (likesService.existsPostsLikesByUser(dislikes.getUser().getId(), dislikes.getPhoto().getId())) {
			throw new LikeAlreadyExistsException("There is already a like at this photo by this user");
		}

		entityManager.persist(dislikes);
		entityManager.flush();

		return dislikes;
	}

	/* (non-Javadoc)
	 * @see com.filip.dressfriend.service.IReadDislikes#listDislikesOfPhoto(java.lang.Long, int, int)
	 */
	@SuppressWarnings("unchecked")
	public CollectionResponse<Dislikes> listDislikesOfPhoto(Long photoId, int clientDislikesCount, int clientDislikesHashCode) {
		List<Dislikes> execute = null;

		int persistDislikesCount = getPhotoDislikesCount(photoId).intValue();
		//there is no new dislikes for the client
		//TODO hashCode
		if(persistDislikesCount == clientDislikesCount) {
			execute = new ArrayList<>();
		} else {
			IReadPhoto photoService = new PhotoServiceImpl(entityManager);
			Photo photo = photoService.getPhoto(photoId);
			Query query = entityManager.createQuery("select d from Dislikes d where d.photo = :photoId");
			query = query.setParameter("photoId", photo);
			execute = (List<Dislikes>) query.getResultList();
			if(execute == null || execute.size() == 0) {
				//dislikes indicates that there are no dislikes at this photo anymore
				Dislikes dislikes = new Dislikes();
				dislikes.setId(-11l);
				execute.add(dislikes);
			}
		}

		return CollectionResponse.<Dislikes> builder().setItems(execute).build();
	}

	/* (non-Javadoc)
	 * @see com.filip.dressfriend.service.IReadDislikes#getUserDislikeAtPost(java.lang.Long, java.lang.Long)
	 */
	public Dislikes getUserDislikeAtPost(Long postId, Long userId) {
		Dislikes result = new Dislikes();

		try {

			IReadSimplePost simplePostService = new SimplePostServiceImpl(entityManager);
			SimplePost sp = simplePostService.getSimplePost(postId);

			IReadUser userEndpoint = new UserServiceImpl(entityManager);
			User user = userEndpoint.getUser(userId);

			String photo_where_query = "";
			int photosCount = 0;
			while (photosCount < sp.getPhotos().size()) {
				if (photosCount == 0) {
					photo_where_query = "l.photo = :photoId" + photosCount;

				} else {
					photo_where_query = photo_where_query + " OR l.photo = :photoId" + photosCount;
				}
				photosCount++;
			}
			photo_where_query = "(" + photo_where_query + ")";

			Query query = entityManager.createQuery("select l from Dislikes l where " + photo_where_query
					+ " and l.user = :userId");

			photosCount = 0;
			for (Photo photo : sp.getPhotos()) {
				query = query.setParameter("photoId" + photosCount++, photo);
			}
			query = query.setParameter("userId", user);

			result = (Dislikes) query.getSingleResult();

		} catch (javax.persistence.NoResultException e) {
			result = null;
		}

		return result;
	}

	/* (non-Javadoc)
	 * @see com.filip.dressfriend.service.IReadDislikes#getPhotoDislikesCount(java.lang.Long)
	 */
	public Long getPhotoDislikesCount(Long photo_id) {
		Query query = entityManager.createNativeQuery("SELECT COUNT(*) FROM DISLIKES d WHERE d.PHOTO_ID = ?");
		query = query.setParameter(1, photo_id);

		return (Long) query.getSingleResult();
	}


	/* (non-Javadoc)
	 * @see com.filip.dressfriend.service.IReadDislikes#existsPostsDislikesByUser(java.lang.Long, java.lang.Long)
	 */
	public boolean existsPostsDislikesByUser(Long user_id, Long postId) {
		long dislikesCount = 0l;
		IReadSimplePost simplePostService = new SimplePostServiceImpl(entityManager);
		SimplePost sp = simplePostService.getSimplePost(postId);

		String photo_where_query = "";
		int photosCount = 0;

		while (photosCount < sp.getPhotos().size()) {
			if (photosCount == 0) {
				photo_where_query = "l.PHOTO_ID = ?";

			} else {
				photo_where_query = photo_where_query + " OR l.PHOTO_ID = ?";
			}
			photosCount++;
		}
		photo_where_query = "(" + photo_where_query + ")";

		Query query = entityManager.createNativeQuery("select exists (select * from DISLIKES l where "
				+ photo_where_query + " AND l.USER_ID = ?)");

		photosCount = 0;
		for (Photo photo : sp.getPhotos()) {
			query = query.setParameter(++photosCount, photo.getId());
		}
		query = query.setParameter(++photosCount, user_id);

		dislikesCount = (long) query.getSingleResult();

		return dislikesCount > 0;
	}

	private boolean containsDislikes(Dislikes dislikes) {

		boolean contains = true;

		if (dislikes.getId() == null) {
			return false;
		}

		Dislikes item = entityManager.find(Dislikes.class, dislikes.getId());
		if (item == null) {
			contains = false;
		}

		return contains;
	}

}
