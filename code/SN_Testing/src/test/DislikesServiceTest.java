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
 * These test cases are created for the needs of Bachelor thesis: "Model based testing of cloud based social networks"
 * @author Filip Rydzi (C)
 *
 */

public class DislikesServiceTest {

	private SimplePostService simplePostDAO;
	private UserService userDAO;
	private DislikesService dislikesDAO;
	private LikesService likesDAO;
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
		dislikesDAO = null;
		likesDAO = null;

		tx.rollback();
		mgr.close();
	}

	/**
	 * Test case 1, see A.7.2
	 * 
	 * @throws LikeAlreadyExistsException
	 * @throws DislikeAlreadyExistsException
	 * @throws UnauthorizedDisLikeException
	 */
	@Test(expected = LikeAlreadyExistsException.class)
	public void test_insertDislikes_shouldThrowLikeAlreadyExistsException()
			throws LikeAlreadyExistsException, DislikeAlreadyExistsException, UnauthorizedDisLikeException {

		/*
		 * creating preconditions
		 */
		User postOwner = new User("user1", "email1");
		User dislikeCreator = new User("user2", "email2");

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
		postViewers.add(dislikeCreator);
		simplePost1.setUserCanSeePost(postViewers);

		postOwner = userDAO.insertUser(postOwner);
		dislikeCreator = userDAO.insertUser(dislikeCreator);
		simplePost1 = simplePostDAO.insertSimplePost(simplePost1);

		/*
		 * creating initial condition
		 */
		Likes likes1 = new Likes();
		likes1.setPhoto(photo1);
		likes1.setUser(dislikeCreator);

		likes1 = likesDAO.insertLikes(likes1);

		/*
		 * user input
		 */
		Dislikes dislikes2 = new Dislikes();
		dislikes2.setPhoto(photo1);
		dislikes2.setUser(dislikeCreator);

		/*
		 * LikeAlreadyExistsException should be thrown here
		 */
		dislikes2 = dislikesDAO.insertDislikes(dislikes2);
	}

	/**
	 * Test case 2, A.7.2
	 * 
	 * @throws LikeAlreadyExistsException
	 * @throws DislikeAlreadyExistsException
	 * @throws UnauthorizedDisLikeException
	 */
	@Test(expected = DislikeAlreadyExistsException.class)
	public void test_insertDislikes_shouldThrowDislikeAlreadyExistsException()
			throws LikeAlreadyExistsException, DislikeAlreadyExistsException, UnauthorizedDisLikeException {

		/*
		 * creating preconditions
		 */
		User postOwner = new User("user1", "email1");
		User dislikeCreator = new User("user2", "email2");

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
		postViewers.add(dislikeCreator);
		simplePost1.setUserCanSeePost(postViewers);

		postOwner = userDAO.insertUser(postOwner);
		dislikeCreator = userDAO.insertUser(dislikeCreator);
		simplePost1 = simplePostDAO.insertSimplePost(simplePost1);

		/*
		 * creating initial condition
		 */
		Dislikes dislikes1 = new Dislikes();
		dislikes1.setPhoto(photo1);
		dislikes1.setUser(dislikeCreator);

		dislikes1 = dislikesDAO.insertDislikes(dislikes1);

		/*
		 * user input
		 */
		Dislikes dislikes2 = new Dislikes();
		dislikes2.setPhoto(photo1);
		dislikes2.setUser(dislikeCreator);

		/*
		 * DislikeAlreadyExistsException should be thrown here
		 */
		dislikes2 = dislikesDAO.insertDislikes(dislikes2);
	}

	/**
	 * Test case 3, see A.7.2
	 * 
	 * @throws LikeAlreadyExistsException
	 * @throws DislikeAlreadyExistsException
	 * @throws UnauthorizedDisLikeException
	 */
	@Test
	public void test_insertDislikes_shouldOK() throws LikeAlreadyExistsException,
			DislikeAlreadyExistsException, UnauthorizedDisLikeException {
		/*
		 * creating preconditions
		 */
		User postOwner = new User("user1", "email1");
		User postViewer = new User("user3", "email123");
		User dislikeCreator = new User("user2", "email2");

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
		postViewers.add(dislikeCreator);
		postViewers.add(postViewer);
		simplePost1.setUserCanSeePost(postViewers);

		postOwner = userDAO.insertUser(postOwner);
		dislikeCreator = userDAO.insertUser(dislikeCreator);
		postViewer = userDAO.insertUser(postViewer);
		simplePost1 = simplePostDAO.insertSimplePost(simplePost1);

		/*
		 * user input
		 */
		Dislikes dislikes1 = new Dislikes();
		dislikes1.setPhoto(photo1);
		dislikes1.setUser(dislikeCreator);

		/*
		 * what is needed to be shown part
		 */
		dislikes1 = dislikesDAO.insertDislikes(dislikes1); // (1) dislike was
															// returned to post
															// viewer

		/*
		 * dislikes1 is Dislike at photo of a post (2)
		 */
		assertTrue(dislikes1.getPhoto().equals(photo1));

		/*
		 * (3) dislikes1 must be visible for post viewers
		 */
		List<SimplePost> postVisibleForPostViewer = (List<SimplePost>) simplePostDAO.listSimplePostsForUser(
				postViewer.getId()).getItems();

		for (SimplePost sp : postVisibleForPostViewer) {
			if (sp.getPhotos().contains(photo1)) {
				List<Dislikes> dislikesOfPhoto = (List<Dislikes>) dislikesDAO.listDislikesOfPhoto(
						photo1.getId(), 0, 0).getItems();
				assertTrue(dislikesOfPhoto.contains(dislikes1));
			}
		}

		/*
		 * showing post conditions
		 */
		assertTrue(simplePostDAO.getSimplePost(simplePost1.getId()).equals(simplePost1)); // (1)
		assertTrue(userDAO.getUser(dislikeCreator.getId()).equals(dislikeCreator));// (2)
		assertTrue(userDAO.getUser(postViewer.getId()).equals(postViewer));// (3)
	}

	/**
	 * Test case 4, see A.7.2
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

		Dislikes dislikes1 = new Dislikes();
		dislikes1.setPhoto(photo1);
		dislikes1.setUser(nonPostViewer);

		/*
		 * user input UnauthorizedDisLikeException should be thrown here
		 */
		dislikes1 = dislikesDAO.insertDislikes(dislikes1);
	}

	/**
	 * This test case has discovered a bug. See section 5.1.4 Disliking a photo within
	 * post.
	 * 
	 * @throws LikeAlreadyExistsException
	 * @throws DislikeAlreadyExistsException
	 * @throws UnauthorizedDisLikeException
	 */
	@Test(expected = Exception.class)
	public void test_insertDislikes_shouldThrowExpection() throws LikeAlreadyExistsException,
			DislikeAlreadyExistsException, UnauthorizedDisLikeException {
		/*
		 * creating preconditions
		 */
		User postOwner = new User("user1", "email1");
		User postViewer = new User("user3", "email123");
		User dislikeCreator = new User("user2", "email2");

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
		postViewers.add(dislikeCreator);
		postViewers.add(postViewer);
		simplePost1.setUserCanSeePost(postViewers);

		postOwner = userDAO.insertUser(postOwner);
		dislikeCreator = userDAO.insertUser(dislikeCreator);
		postViewer = userDAO.insertUser(postViewer);
		simplePost1 = simplePostDAO.insertSimplePost(simplePost1);

		/*
		 * user input
		 */
		Dislikes dislikes1 = new Dislikes();
		dislikes1.setPhoto(photo1);
		dislikes1.setUser(dislikeCreator);

		dislikes1 = dislikesDAO.insertDislikes(dislikes1); // the exception
															// should be thrown
															// here. Dislike
															// creator tries to
															// dislike a photo
															// within post,
															// which contains
															// more photos.

	}

}
