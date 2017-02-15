package com.filip.dressfriend.simplepost;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;

import com.filip.dressfriend.Dislikes;
import com.filip.dressfriend.Likes;
import com.filip.dressfriend.Photo;
import com.filip.dressfriend.SimplePost;
import com.filip.dressfriend.User;
import com.filip.dressfriend.UserSeePost;
import com.filip.dressfriend.dislikes.DislikesServiceImpl;
import com.filip.dressfriend.dislikes.IReadDislikes;
import com.filip.dressfriend.likes.IReadLikes;
import com.filip.dressfriend.likes.LikesServiceImpl;
import com.filip.dressfriend.photo.ICreatePhoto;
import com.filip.dressfriend.photo.IReadPhoto;
import com.filip.dressfriend.photo.IRemovePhoto;
import com.filip.dressfriend.photo.PhotoServiceImpl;
import com.filip.dressfriend.user.IReadUser;
import com.filip.dressfriend.user.UserServiceImpl;
import com.filip.dressfriend.userseepost.IRemoveUserSeePost;
import com.filip.dressfriend.userseepost.UserSeePostService;
import com.filip.dressfriend.userseepost.UserSeePostServiceImpl;
import com.google.api.server.spi.response.CollectionResponse;

public class SimplePostServiceImpl implements SimplePostService, ICreateSimplePost, IReadSimplePost, IUpdateSimplePost, IRemoveSimplePost {

	private EntityManager entityManager;

	public SimplePostServiceImpl(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	/* (non-Javadoc)
	 * @see com.filip.dressfriend.simplepost.ICreateSimplePost#insertSimplePost(com.filip.dressfriend.SimplePost)
	 */
	@Override
	public SimplePost insertSimplePost(SimplePost simplepost) {

		if (containsSimplePost(simplepost)) {
			throw new EntityExistsException("Object already exists");
		}

		simplepost.setPostTime(new Date());

		entityManager.persist(simplepost);
		entityManager.flush();

		String photo_ids = saveAndMapPhotosToString(simplepost);
		simplepost.setPHOTO_IDS(photo_ids);

		if (simplepost.getPostViewers() != null) {
			UserSeePostService userSeePostEndpoint = new UserSeePostServiceImpl(entityManager);
			for (User user : simplepost.getPostViewers()) {
				UserSeePost userSeePost = new UserSeePost();
				userSeePost.setPost(simplepost);
				userSeePost.setUser(user);

				userSeePostEndpoint.insertUserSeePost(userSeePost);
			}
		}

		return simplepost;
	}

	/* (non-Javadoc)
	 * @see com.filip.dressfriend.simplepost.IReadSimplePost#listSimplePost()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public CollectionResponse<SimplePost> listSimplePost() {

		List<SimplePost> execute = null;

		Query query = entityManager.createQuery("select sp from SimplePost sp");

		execute = (List<SimplePost>) query.getResultList();

		// Tight loop for fetching all entities from datastore and
		// accomodate
		// for lazy fetch.
		if (execute != null) {
			for (SimplePost obj : execute) {
				String photos_id = obj.getPHOTO_IDS();
				obj.setPhotos(getPhotosFromString(photos_id));
			}
		}

		return CollectionResponse.<SimplePost> builder().setItems(execute).build();
	}

	/* (non-Javadoc)
	 * @see com.filip.dressfriend.simplepost.IReadSimplePost#listSimplePostsOfUser(java.lang.Long)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public CollectionResponse<SimplePost> listSimplePostsOfUser(Long userId) {
		List<SimplePost> execute = null;

		IReadUser userService = new UserServiceImpl(entityManager);
		User user = userService.getUser(userId);

		Query query = entityManager
				.createQuery("SELECT p FROM SimplePost p WHERE p.postedBy = :userId ORDER BY p.id DESC");

		query = query.setParameter("userId", user);

		execute = (List<SimplePost>) query.getResultList();

		// Tight loop for fetching all entities from datastore and
		// accomodate
		// for lazy fetch.
		if (execute != null) {
			for (SimplePost obj : execute) {
				String photos_id = obj.getPHOTO_IDS();
				obj.setPhotos(getPhotosFromString(photos_id));
			}
		}

		return CollectionResponse.<SimplePost> builder().setItems(execute).build();
	}


	/* (non-Javadoc)
	 * @see com.filip.dressfriend.simplepost.IReadSimplePost#listSimplePostsForUser(java.lang.Long)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public CollectionResponse<SimplePost> listSimplePostsForUser(Long userId) {
		List<SimplePost> execute = new ArrayList<>();
		List<Object[]> executeObjects = null;

		IReadLikes likesService = new LikesServiceImpl(entityManager);
		IReadDislikes dislikesService = new DislikesServiceImpl(entityManager);

		IReadUser userService = new UserServiceImpl(entityManager);
		// select distinct sp.ID, sp.POSTEDBY_ID, sp.DESCRIPTION,
		// sp.POSTTIME, sp.PHOTO_IDS from SIMPLEPOST sp join USERSEEPOST usp
		// ON usp.POST_ID=sp.ID WHERE usp.USER_ID='263' ORDER BY sp.ID DESC
		// Query query = mgr
		// .createNativeQuery("select distinct sp.ID, sp.POSTEDBY_ID, sp.DESCRIPTION, sp.POSTTIME, sp.PHOTO_IDS from SIMPLEPOST sp join USER u on u.ID = sp.POSTEDBY_ID JOIN FRIENDSHIP f ON ((u.ID = f.USER1_ID AND f.USER2_ID = ?) OR (u.ID = f.USER2_ID AND f.USER1_ID = ?)) ORDER BY sp.ID DESC");
		Query query = entityManager
				.createNativeQuery("select distinct sp.ID, sp.POSTEDBY_ID, sp.DESCRIPTION, sp.POSTTIME, sp.PHOTO_IDS from SIMPLEPOST sp join USERSEEPOST usp ON usp.POST_ID=sp.ID WHERE usp.USER_ID=? ORDER BY sp.ID DESC");
		query = query.setParameter(1, userId);

		executeObjects = (List<Object[]>) query.getResultList();

		// Tight loop for fetching all entities from datastore and
		// accomodate
		// for lazy fetch.
		for (Object[] sp : executeObjects) {
			Long id = new Long((Integer) sp[0]);
			Long postedBy_ID = new Long((Integer) sp[1]);
			User user = userService.getUser(postedBy_ID);
			String desc = (String) sp[2];
			Date date = (Date) sp[3];
			String photos_id = (String) sp[4];

			SimplePost simplePost = new SimplePost();
			simplePost.setId(id);
			simplePost.setPostedBy(user);
			simplePost.setDescription(desc);
			simplePost.setPostTime(date);
			simplePost.setPHOTO_IDS(photos_id);
			simplePost.setPhotos(getPhotosFromString(photos_id));

			Likes myLike = likesService.getUserLikeAtPost(id, userId);
			Dislikes myDislike = dislikesService.getUserDislikeAtPost(id, userId);

			simplePost.setMyLike(myLike);
			simplePost.setLikedByMe(myLike != null);

			simplePost.setMyDislike(myDislike);
			simplePost.setDislikedByMe(myDislike != null);

			execute.add(simplePost);
		}

		return CollectionResponse.<SimplePost> builder().setItems(execute).build();
	}

	/* (non-Javadoc)
	 * @see com.filip.dressfriend.simplepost.IReadSimplePost#getSimplePost(java.lang.Long)
	 */
	@Override
	public SimplePost getSimplePost(Long id) {
		SimplePost simplepost = entityManager.find(SimplePost.class, id);
		simplepost.setPhotos(getPhotosFromString(simplepost.getPHOTO_IDS()));
		return simplepost;
	}

	/* (non-Javadoc)
	 * @see com.filip.dressfriend.simplepost.IUpdateSimplePost#updateSimplePost(com.filip.dressfriend.SimplePost)
	 */
	@Override
	public SimplePost updateSimplePost(SimplePost simplepost) {
		if (!containsSimplePost(simplepost)) {
			throw new EntityNotFoundException("Object does not exist");
		}

		removeOldPhotos(simplepost.getPhotos(), simplepost.getPHOTO_IDS());

		// update post photos
		String newPhotosIds = this.saveAndMapPhotosToString(simplepost);
		simplepost.setPHOTO_IDS(newPhotosIds);

		// remove old viewers of a post
		UserSeePostService userSeePostEndpoint = new UserSeePostServiceImpl(entityManager);
		userSeePostEndpoint.removeUserSeeForPost(simplepost.getId());

		// add new viewers
		if (simplepost.getPostViewers() != null) {
			for (User user : simplepost.getPostViewers()) {
				UserSeePost userSeePost = new UserSeePost();
				userSeePost.setPost(simplepost);
				userSeePost.setUser(user);

				userSeePostEndpoint.insertUserSeePost(userSeePost);
			}
		}

		entityManager.merge(simplepost);
		entityManager.flush();

		return simplepost;
	}
	/* (non-Javadoc)
	 * @see com.filip.dressfriend.simplepost.IRemoveSimplePost#removeSimplePost(java.lang.Long)
	 */
	@Override
	public void removeSimplePost(Long id) {

		SimplePost simplepost = entityManager.find(SimplePost.class, id);
		IRemovePhoto photoEndpoint = new PhotoServiceImpl(entityManager);
		simplepost.setPhotos(getPhotosFromString(simplepost.getPHOTO_IDS()));

		if (simplepost.getPhotos() != null) {
			for (Photo photo : simplepost.getPhotos()) {
				photoEndpoint.removePhoto(photo.getId());
			}
		}
		IRemoveUserSeePost userSeePostEndpoint = new UserSeePostServiceImpl(entityManager);
		userSeePostEndpoint.removeUserSeeForPost(id);

		entityManager.remove(simplepost);
	}

	private boolean containsSimplePost(SimplePost simplepost) {
		boolean contains = true;

		if (simplepost.getId() == null) {
			return false;
		}

		SimplePost item = entityManager.find(SimplePost.class, simplepost.getId());
		if (item == null) {
			contains = false;
		}

		return contains;
	}

	private List<Photo> getPhotosFromString(String photos_id) {

		IReadPhoto photoEndpoint = new PhotoServiceImpl(entityManager);

		List<Photo> photos = new ArrayList<Photo>();

		if (photos_id != null && !photos_id.equals("")) {
			int index = photos_id.indexOf(" ");
			int lastIndexOf = 0;
			while (index != -1) {
				long photo_id = Long.parseLong(photos_id.substring(lastIndexOf, index));
				Photo photo = photoEndpoint.getPhoto(photo_id);
				photos.add(photo);
				lastIndexOf = index + 1;
				index = photos_id.indexOf(" ", lastIndexOf);
			}
			long photo_id = Long.parseLong(photos_id.substring(lastIndexOf));

			Photo photo = photoEndpoint.getPhoto(photo_id);
			photos.add(photo);

		}

		return photos;
	}

	private String saveAndMapPhotosToString(SimplePost simplePost) {
		List<Photo> photos = simplePost.getPhotos();

		String photo_ids = "";
		if (photos != null) {
			ICreatePhoto photoEndpoint = new PhotoServiceImpl(entityManager);
			for (Photo photo : photos) {
				if (photo.getId() == null || photo.getId() == -1l) {// photo
																	// hasn't
																	// been
																	// inserted
																	// yet

					photo = photoEndpoint.insertPhoto(photo, simplePost);
				}
				if (photo_ids.equals("")) {
					photo_ids = photo.getId().toString();
				} else {
					photo_ids = photo_ids + " " + photo.getId().toString();
				}
			}
		}
		return photo_ids;
	}

	private void removeOldPhotos(List<Photo> photos, String photoIDs) {

		IRemovePhoto photoEndpoint = new PhotoServiceImpl(entityManager);

		if (photoIDs != null && !photoIDs.equals("")) {
			int index = photoIDs.indexOf(" ");

			List<Long> photo_IDs = new ArrayList<>();

			int lastIndexOf = 0;
			while (index != -1) {
				long photo_id = Long.parseLong(photoIDs.substring(lastIndexOf, index));
				lastIndexOf = index + 1;
				index = photoIDs.indexOf(" ", lastIndexOf);
				photo_IDs.add(photo_id);
			}
			long photo_id = Long.parseLong(photoIDs.substring(lastIndexOf));
			photo_IDs.add(photo_id);

			for (Long photo_ID : photo_IDs) {
				if (photos != null) {
					boolean found = false;
					for (Photo p : photos) {
						if (p.getId() == null) {
							continue;
						}
						if (p.getId().equals(photo_ID)) {
							found = true;
							break;
						}
					}
					if (!found) {
						photoEndpoint.removePhoto(photo_ID);
					}
				}
			}

		}

	}

}
