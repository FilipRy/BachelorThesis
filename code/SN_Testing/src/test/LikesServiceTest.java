package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.filip.dressfriend.Dislikes;
import com.filip.dressfriend.EMF;
import com.filip.dressfriend.Likes;
import com.filip.dressfriend.Photo;
import com.filip.dressfriend.SimplePost;
import com.filip.dressfriend.User;
import com.filip.dressfriend.dislikes.DislikesService;
import com.filip.dressfriend.dislikes.DislikesServiceImpl;
import com.filip.dressfriend.exception.DislikeAlreadyExistsException;
import com.filip.dressfriend.exception.LikeAlreadyExistsException;
import com.filip.dressfriend.exception.UnauthorizedDisLikeException;
import com.filip.dressfriend.likes.LikesService;
import com.filip.dressfriend.likes.LikesServiceImpl;
import com.filip.dressfriend.simplepost.SimplePostService;
import com.filip.dressfriend.simplepost.SimplePostServiceImpl;
import com.filip.dressfriend.user.UserService;
import com.filip.dressfriend.user.UserServiceImpl;

/**
 * These test cases are created for the needs of Bachelor thesis:
 * "Model based testing of cloud based social networks"
 * 
 * @author Filip Rydzi (C)
 * 
 */

public class LikesServiceTest {

	private SimplePostService simplePostDAO;
	private UserService userDAO;
	private LikesService likesDAO;
	private DislikesService dislikesDAO;
	private EntityTransaction tx;
	private EntityManager mgr;

	@Before
	public void setUp() throws Exception {
		mgr = EMF.get().createEntityManager();
		tx = mgr.getTransaction();
		tx.begin();

		simplePostDAO = new SimplePostServiceImpl(mgr);
		userDAO = new UserServiceImpl(mgr);
		likesDAO = new LikesServiceImpl(mgr);
		dislikesDAO = new DislikesServiceImpl(mgr);
	}

	@After
	public void tearDown() throws Exception {
		simplePostDAO = null;
		userDAO = null;
		likesDAO = null;
		dislikesDAO = null;

		tx.rollback();
		mgr.close();
	}

	/**
	 * Test case 1, see section 4.2.2 Liking a photo within post
	 * 
	 * @throws DislikeAlreadyExistsException
	 * @throws LikeAlreadyExistsException
	 * @throws UnauthorizedDisLikeException
	 */
	@Test(expected = LikeAlreadyExistsException.class)
	public void test_insertLikes_shouldThrowLikeAlreadyExistsException()
			throws DislikeAlreadyExistsException, LikeAlreadyExistsException, UnauthorizedDisLikeException {

		/*
		 * creating preconditions
		 */
		User postOwner = new User("user1", "email1");
		User likeCreator = new User("user2", "email2");

		SimplePost simplePost1 = new SimplePost();
		simplePost1.setDescription("post1");
		simplePost1.setPostedBy(postOwner);

		Photo photo1 = new Photo();
		photo1.setDescription("photo1");
		photo1.setPath("gg");

		List<Photo> photos = new ArrayList<Photo>();
		photos.add(photo1);
		simplePost1.setPhotos(photos);

		List<User> postViewers = new ArrayList<User>();
		postViewers.add(likeCreator);
		simplePost1.setUserCanSeePost(postViewers);

		postOwner = userDAO.insertUser(postOwner);
		likeCreator = userDAO.insertUser(likeCreator);
		simplePost1 = simplePostDAO.insertSimplePost(simplePost1);

		/*
		 * creating initial condition
		 */
		Likes likes1 = new Likes();
		likes1.setPhoto(photo1);
		likes1.setUser(likeCreator);

		likes1 = likesDAO.insertLikes(likes1);

		/*
		 * user input
		 */
		Likes likes2 = new Likes();
		likes2.setPhoto(photo1);
		likes2.setUser(likeCreator);

		/*
		 * LikeAlreadyExistsException should be thrown here
		 */
		likes2 = likesDAO.insertLikes(likes2);
	}

	/**
	 * Test case 2, see section 4.2.2 Liking a photo within post
	 * 
	 * @throws DislikeAlreadyExistsException
	 * @throws LikeAlreadyExistsException
	 * @throws UnauthorizedDisLikeException
	 */
	@Test(expected = DislikeAlreadyExistsException.class)
	public void test_insertLikes_shouldThrowDislikeAlreadyExistsException()
			throws DislikeAlreadyExistsException, LikeAlreadyExistsException, UnauthorizedDisLikeException {

		/*
		 * creating preconditions
		 */
		User postOwner = new User("user1", "email1");
		User likeCreator = new User("user2", "email2");

		SimplePost simplePost1 = new SimplePost();
		simplePost1.setDescription("post1");
		simplePost1.setPostedBy(postOwner);

		Photo photo1 = new Photo();
		photo1.setDescription("photo1");
		photo1.setPath("gg");

		List<Photo> photos = new ArrayList<Photo>();
		photos.add(photo1);
		simplePost1.setPhotos(photos);

		List<User> postViewers = new ArrayList<User>();
		postViewers.add(likeCreator);
		simplePost1.setUserCanSeePost(postViewers);

		postOwner = userDAO.insertUser(postOwner);
		likeCreator = userDAO.insertUser(likeCreator);
		simplePost1 = simplePostDAO.insertSimplePost(simplePost1);

		/*
		 * creating initial condition
		 */
		Dislikes dislikes1 = new Dislikes();
		dislikes1.setPhoto(photo1);
		dislikes1.setUser(likeCreator);

		dislikes1 = dislikesDAO.insertDislikes(dislikes1);

		/*
		 * user input
		 */
		Likes likes2 = new Likes();
		likes2.setPhoto(photo1);
		likes2.setUser(likeCreator);

		/*
		 * DislikeAlreadyExistsException should be thrown here
		 */
		likes2 = likesDAO.insertLikes(likes2);
	}

	/**
	 * Test case 3, see section 4.2.2 Liking a photo within post
	 * 
	 * @throws DislikeAlreadyExistsException
	 * @throws LikeAlreadyExistsException
	 * @throws UnauthorizedDisLikeException
	 */
	@Test
	public void test_insertLikes_shouldOK() throws DislikeAlreadyExistsException, LikeAlreadyExistsException,
			UnauthorizedDisLikeException {
		/*
		 * creating preconditions
		 */
		User postOwner = new User("user1", "email1");
		User postViewer = new User("user3", "email123");
		User likeCreator = new User("user2", "email2");

		SimplePost simplePost1 = new SimplePost();
		simplePost1.setDescription("post1");
		simplePost1.setPostedBy(postOwner);

		Photo photo1 = new Photo();
		photo1.setDescription("photo1");
		photo1.setPath("gg");

		List<Photo> photos = new ArrayList<Photo>();
		photos.add(photo1);
		simplePost1.setPhotos(photos);

		List<User> postViewers = new ArrayList<User>();
		postViewers.add(likeCreator);
		postViewers.add(postViewer);
		simplePost1.setUserCanSeePost(postViewers);

		postOwner = userDAO.insertUser(postOwner);
		likeCreator = userDAO.insertUser(likeCreator);
		postViewer = userDAO.insertUser(postViewer);
		simplePost1 = simplePostDAO.insertSimplePost(simplePost1);

		/*
		 * user input
		 */
		Likes likes1 = new Likes();
		likes1.setPhoto(photo1);
		likes1.setUser(likeCreator);

		/*
		 * what is needed to be shown part
		 */
		likes1 = likesDAO.insertLikes(likes1); // (1) like was returned to post
												// viewer

		/*
		 * likes1 is Like at photo of a post (2)
		 */
		assertTrue(likes1.getPhoto().equals(photo1));

		/*
		 * (3) likes1 must be visible for post viewers
		 */
		List<SimplePost> postVisibleForPostViewer = (List<SimplePost>) simplePostDAO.listSimplePostsForUser(
				postViewer.getId()).getItems();

		for (SimplePost sp : postVisibleForPostViewer) {
			if (sp.getPhotos().contains(photo1)) {
				List<Likes> likesOfPhoto = (List<Likes>) likesDAO.listLikesOfPhoto(photo1.getId(), 0, 0)
						.getItems();
				assertTrue(likesOfPhoto.contains(likes1));
			}
		}
		/*
		 * showing post conditions
		 */
		assertTrue(simplePostDAO.getSimplePost(simplePost1.getId()).equals(simplePost1)); // (1)
		assertTrue(userDAO.getUser(likeCreator.getId()).equals(likeCreator));// (2)
		assertTrue(userDAO.getUser(postViewer.getId()).equals(postViewer));// (3)

	}

	/**
	 * Test case 4, see section 4.2.2 Liking a photo within post
	 * 
	 * @throws DislikeAlreadyExistsException
	 * @throws LikeAlreadyExistsException
	 * @throws UnauthorizedDisLikeException
	 */
	@Test(expected = UnauthorizedDisLikeException.class)
	public void test_insertLikes_shouldThrowUnauthorizedDisLike() throws DislikeAlreadyExistsException,
			LikeAlreadyExistsException, UnauthorizedDisLikeException {

		/*
		 * creating preconditions
		 */
		User postOwner = new User("user1", "email1");
		User nonPostViewer = new User("user2", "email2");
		SimplePost simplePost1 = new SimplePost();
		simplePost1.setDescription("post1");
		simplePost1.setPostedBy(postOwner);

		Photo photo1 = new Photo();
		photo1.setDescription("photo1");
		photo1.setPath("gg");

		List<Photo> photos = new ArrayList<Photo>();
		photos.add(photo1);
		simplePost1.setPhotos(photos);

		postOwner = userDAO.insertUser(postOwner);
		nonPostViewer = userDAO.insertUser(nonPostViewer);
		simplePost1 = simplePostDAO.insertSimplePost(simplePost1);

		Likes likes1 = new Likes();
		likes1.setPhoto(photo1);
		likes1.setUser(nonPostViewer);

		/*
		 * user input UnauthorizedDisLikeException should be thrown here
		 */
		likes1 = likesDAO.insertLikes(likes1);
	}

	/**
	 * This test case has discovered a bug. See section 5.1.3 Liking a photo
	 * within post post.
	 * 
	 * @throws DislikeAlreadyExistsException
	 * @throws LikeAlreadyExistsException
	 * @throws UnauthorizedDisLikeException
	 */
	@Test(expected = Exception.class)
	public void test_insertLikes_shouldThrowException() throws DislikeAlreadyExistsException,
			LikeAlreadyExistsException, UnauthorizedDisLikeException {
		/*
		 * creating preconditions
		 */
		User postOwner = new User("user1", "email1");
		User postViewer = new User("user3", "email123");
		User likeCreator = new User("user2", "email2");

		SimplePost simplePost1 = new SimplePost();
		simplePost1.setDescription("post1");
		simplePost1.setPostedBy(postOwner);

		Photo photo1 = new Photo();
		photo1.setDescription("photo1");
		photo1.setPath("gg");

		Photo photo2 = new Photo();
		photo2.setDescription("photo1");
		photo2.setPath("gg");

		List<Photo> photos = new ArrayList<Photo>();
		photos.add(photo1);
		photos.add(photo2);
		simplePost1.setPhotos(photos);

		List<User> postViewers = new ArrayList<User>();
		postViewers.add(likeCreator);
		postViewers.add(postViewer);
		simplePost1.setUserCanSeePost(postViewers);

		postOwner = userDAO.insertUser(postOwner);
		likeCreator = userDAO.insertUser(likeCreator);
		postViewer = userDAO.insertUser(postViewer);
		simplePost1 = simplePostDAO.insertSimplePost(simplePost1);

		/*
		 * user input
		 */
		Likes likes1 = new Likes();
		likes1.setPhoto(photo1);
		likes1.setUser(likeCreator);

		Likes likes2 = new Likes();
		likes2.setPhoto(photo2);
		likes2.setUser(likeCreator);

		likes1 = likesDAO.insertLikes(likes1); // the first like (on photo
												// within simplePost1) by
												// likeCreator is created
		likes2 = likesDAO.insertLikes(likes2); // likeCreator attempts to create
												// second like (on photo within
												// simplePost1), it's not
												// allowed, therefore the
												// exception should be thrown
												// here

	}

}
