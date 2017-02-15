package com.filip.dressfriend.likes;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.filip.dressfriend.Likes;
import com.filip.dressfriend.Photo;
import com.filip.dressfriend.SimplePost;
import com.filip.dressfriend.User;
import com.filip.dressfriend.dislikes.DislikesServiceImpl;
import com.filip.dressfriend.dislikes.IReadDislikes;
import com.filip.dressfriend.exception.DislikeAlreadyExistsException;
import com.filip.dressfriend.exception.LikeAlreadyExistsException;
import com.filip.dressfriend.exception.UnauthorizedDisLikeException;
import com.filip.dressfriend.photo.IReadPhoto;
import com.filip.dressfriend.photo.PhotoServiceImpl;
import com.filip.dressfriend.simplepost.IReadSimplePost;
import com.filip.dressfriend.simplepost.SimplePostServiceImpl;
import com.filip.dressfriend.user.IReadUser;
import com.filip.dressfriend.user.UserServiceImpl;
import com.filip.dressfriend.userseepost.IReadUserSeePost;
import com.filip.dressfriend.userseepost.UserSeePostServiceImpl;
import com.google.api.server.spi.response.CollectionResponse;

public class LikesServiceImpl implements LikesService, ICreateLikes, IReadLikes {

	private EntityManager entityManager;

	public LikesServiceImpl(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	/* (non-Javadoc)
	 * @see com.filip.dressfriend.service.ICreateLikes#insertLikes(com.filip.dressfriend.Likes)
	 */
	@Override
	public Likes insertLikes(Likes likes) throws DislikeAlreadyExistsException, UnauthorizedDisLikeException,
			LikeAlreadyExistsException {
		if (containsLikes(likes)) {
			throw new EntityExistsException("Object already exists");
		}

		IReadDislikes dislikesService = new DislikesServiceImpl(entityManager);
		IReadPhoto photoService = new PhotoServiceImpl(entityManager);
		IReadUserSeePost userSeePostService = new UserSeePostServiceImpl(entityManager);

		SimplePost sp = photoService.getPostOfPhoto(likes.getPhoto().getId());

		if (dislikesService.existsPostsDislikesByUser(likes.getUser().getId(), sp.getId())) {
			throw new DislikeAlreadyExistsException("There is already a dislike at this photo by this user");
		}

		if (!userSeePostService.existsUserSeePost(likes.getUser().getId(), sp.getId())) {
			throw new UnauthorizedDisLikeException("Request to like for user invisible photo");
		}

		if (existsPostsLikesByUser(likes.getUser().getId(), likes.getPhoto().getId())) {
			throw new LikeAlreadyExistsException("There is already a like at this photo by this user");
		}

		entityManager.persist(likes);
		entityManager.flush();

		return likes;
	}

	/* (non-Javadoc)
	 * @see com.filip.dressfriend.service.IReadLikes#listLikesOfPhoto(java.lang.Long, int, int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public CollectionResponse<Likes> listLikesOfPhoto(Long photoId, int clientLikesCount, int hashCode) {
		List<Likes> execute = null;

		int persistLikesCount = getPhotoLikesCount(photoId).intValue();
		// there is no new likes for the client
		if (persistLikesCount == clientLikesCount) {
			execute = new ArrayList<>();
		} else {
			IReadPhoto photoService = new PhotoServiceImpl(entityManager);
			Photo photo = photoService.getPhoto(photoId);

			Query query = entityManager.createQuery("select l from Likes l where l.photo = :photoId");
			query = query.setParameter("photoId", photo);

			execute = (List<Likes>) query.getResultList();
			if (execute == null || execute.size() == 0) {
				// likes indicates that there are no likes at this photo anymore
				Likes likes = new Likes();
				likes.setId(-11l);
				execute.add(likes);
			}
		}
		return CollectionResponse.<Likes> builder().setItems(execute).build();

	}

	/* (non-Javadoc)
	 * @see com.filip.dressfriend.service.IReadLikes#getUserLikeAtPost(java.lang.Long, java.lang.Long)
	 */
	@Override
	public Likes getUserLikeAtPost(Long postId, Long userId) {
		Likes result = new Likes();

		try {

			IReadSimplePost simplePostService = new SimplePostServiceImpl(entityManager);
			SimplePost sp = simplePostService.getSimplePost(postId);

			IReadUser userService = new UserServiceImpl(entityManager);
			User user = userService.getUser(userId);

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

			Query query = entityManager.createQuery("select l from Likes l where " + photo_where_query
					+ " and l.user = :userId");

			photosCount = 0;
			for (Photo photo : sp.getPhotos()) {
				query = query.setParameter("photoId" + photosCount++, photo);
			}
			query = query.setParameter("userId", user);

			result = (Likes) query.getSingleResult();

		} catch (javax.persistence.NoResultException e) {
			result = null;
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see com.filip.dressfriend.service.IReadLikes#getPhotoLikesCount(java.lang.Long)
	 */
	@Override
	public Long getPhotoLikesCount(Long photo_id) {
		Query query = entityManager.createNativeQuery("SELECT COUNT(*) FROM LIKES l WHERE l.PHOTO_ID = ?");
		query = query.setParameter(1, photo_id);
		return (Long) query.getSingleResult();
	}

	/* (non-Javadoc)
	 * @see com.filip.dressfriend.service.IReadLikes#existsPostsLikesByUser(java.lang.Long, java.lang.Long)
	 */
	@Override
	public boolean existsPostsLikesByUser(Long user_id, Long photoId) {
		long likesCount = 0l;

		Query query = entityManager
				.createNativeQuery("select exists (select * from LIKES l where l.PHOTO_ID = ? AND l.USER_ID = ?)");

		query = query.setParameter(1, photoId);
		query = query.setParameter(2, user_id);

		likesCount = (long) query.getSingleResult();

		return likesCount > 0;
	}

	private boolean containsLikes(Likes likes) {
		boolean contains = true;
		if (likes.getId() == null) {
			return false;
		}
		Likes item = entityManager.find(Likes.class, likes.getId());
		if (item == null) {
			contains = false;
		}
		return contains;
	}

}
