package test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.filip.dressfriend.EMF;
import com.filip.dressfriend.Photo;
import com.filip.dressfriend.SimplePost;
import com.filip.dressfriend.User;
import com.filip.dressfriend.simplepost.SimplePostService;
import com.filip.dressfriend.simplepost.SimplePostServiceImpl;
import com.filip.dressfriend.user.UserService;
import com.filip.dressfriend.user.UserServiceImpl;

/**
 * This test case is created for the needs of Bachelor thesis: "Model based testing of cloud based social networks"
 * @author Filip Rydzi (C)
 *
 */

/**
 * 
 * These test cases are testing the interaction between the client and a
 * database. I am ignoring here the interaction between Cloud storage and client
 * (see the commented code in PhotoServiceImpl::removePhoto), therefore all
 * these test cases are successful, otherwise they show a failure as described
 * in section 5.1.2 Updating and deleting a post.
 * 
 */
public class SimplePostServiceTest {

	private SimplePostService simplePostDAO;
	private UserService userDAO;
	private EntityTransaction tx;
	private EntityManager mgr;

	@Before
	public void setUp() throws Exception {
		mgr = EMF.get().createEntityManager();
		tx = mgr.getTransaction();
		tx.begin();

		simplePostDAO = new SimplePostServiceImpl(mgr);
		userDAO = new UserServiceImpl(mgr);
	}

	@After
	public void tearDown() throws Exception {
		simplePostDAO = null;
		userDAO = null;

		tx.rollback();
		mgr.close();
	}

	/**
	 * Test case 1, see A.4.2
	 */
	@Test
	public void test_createSimplePost_shouldOK() {

		User postOwner = new User("user1", "email1");

		/*
		 * simple post creation
		 */
		SimplePost simplePost1 = new SimplePost();
		simplePost1.setDescription("post1");
		simplePost1.setPostedBy(postOwner);

		Photo photo1 = new Photo();
		photo1.setDescription("photo1");
		photo1.setLikesCount(0l);
		photo1.setPath("path1");

		Photo photo2 = new Photo();
		photo2.setDescription("photo2");
		photo2.setLikesCount(0l);
		photo2.setPath("path2");

		User postViewer1 = new User("user2", "email2");
		User postViewer2 = new User("user3", "email3");
		User unauhtorized = new User("unauth", "unauthmail");

		List<Photo> photos = new ArrayList<Photo>();
		photos.add(photo1);
		photos.add(photo2);

		List<User> postViewers = new ArrayList<User>();
		postViewers.add(postViewer1);
		postViewers.add(postViewer2);

		simplePost1.setPhotos(photos);
		simplePost1.setUserCanSeePost(postViewers);

		/*
		 * creating preconditions (1) (2)
		 */
		postOwner = userDAO.insertUser(postOwner);
		postViewer1 = userDAO.insertUser(postViewer1);
		postViewer2 = userDAO.insertUser(postViewer2);
		unauhtorized = userDAO.insertUser(unauhtorized);

		/*
		 * user input
		 */
		simplePost1 = simplePostDAO.insertSimplePost(simplePost1);

		/*
		 * what is needed to be shown (2) - post must be visible for all post
		 * viewers
		 */
		List<SimplePost> postViewer1Posts = (List<SimplePost>) simplePostDAO.listSimplePostsForUser(
				postViewer1.getId()).getItems();
		List<SimplePost> postViewer2Posts = (List<SimplePost>) simplePostDAO.listSimplePostsForUser(
				postViewer2.getId()).getItems();

		assertTrue(postViewer1Posts.contains(simplePost1));
		assertTrue(postViewer2Posts.contains(simplePost1));

		/*
		 * (2) - post must be visible for post owner
		 */
		List<SimplePost> postOwnerPosts = (List<SimplePost>) simplePostDAO.listSimplePostsOfUser(
				postOwner.getId()).getItems();

		assertTrue(postOwnerPosts.contains(simplePost1));

		/*
		 * (3) - post cannot be visible for unauthorized
		 */
		List<SimplePost> unauthorizedPosts = (List<SimplePost>) simplePostDAO.listSimplePostsForUser(
				unauhtorized.getId()).getItems();

		assertFalse(unauthorizedPosts.contains(simplePost1));

		/*
		 * showing post conditions (2) - post exists is implied by the part
		 * above) (1)
		 */
		assertTrue(userDAO.getUser(postOwner.getId()).equals(postOwner));

		/*
		 * (3)
		 */
		assertTrue(userDAO.getUser(postViewer1.getId()).equals(postViewer1));
		assertTrue(userDAO.getUser(postViewer2.getId()).equals(postViewer2));

	}

	/**
	 * See A.5.2
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	/*
	 * user removes some photos from SimplePost
	 */
	@Test
	public void test_updateSimplePost1_shouldOK() throws IOException, GeneralSecurityException {
		User postOwner = new User("user1", "email1");

		/*
		 * simple post creation
		 */
		SimplePost simplePost1 = new SimplePost();
		simplePost1.setDescription("post1");
		simplePost1.setPostedBy(postOwner);

		Photo photo1 = new Photo();
		photo1.setDescription("photo1");
		photo1.setLikesCount(0l);
		photo1.setPath("path1");

		Photo photo2 = new Photo();
		photo2.setDescription("photo2");
		photo2.setLikesCount(0l);
		photo2.setPath("path2");

		User postViewer1 = new User("user2", "email2");
		User postViewer2 = new User("user3", "email3");
		User unauhtorized = new User("unauth", "unauthmail");

		List<Photo> photos = new ArrayList<Photo>();
		photos.add(photo1);
		photos.add(photo2);

		List<User> postViewers = new ArrayList<User>();
		postViewers.add(postViewer1);
		postViewers.add(postViewer2);

		simplePost1.setPhotos(photos);
		simplePost1.setUserCanSeePost(postViewers);

		/*
		 * creating preconditions (1) (2) (3)
		 */
		postOwner = userDAO.insertUser(postOwner);
		postViewer1 = userDAO.insertUser(postViewer1);
		postViewer2 = userDAO.insertUser(postViewer2);
		unauhtorized = userDAO.insertUser(unauhtorized);
		simplePost1 = simplePostDAO.insertSimplePost(simplePost1);

		/*
		 * UPDATE PART BEGIN
		 */
		/*
		 * remove photo
		 */
		simplePost1.getPhotos().remove(photo1);

		/*
		 * user input
		 */
		simplePost1 = simplePostDAO.updateSimplePost(simplePost1);
		/*
		 * UPDATE PART END
		 */

		/*
		 * what is needed to be shown (2) - post must be visible for all post
		 * viewers
		 */
		List<SimplePost> postViewer1Posts = (List<SimplePost>) simplePostDAO.listSimplePostsForUser(
				postViewer1.getId()).getItems();
		List<SimplePost> postViewer2Posts = (List<SimplePost>) simplePostDAO.listSimplePostsForUser(
				postViewer2.getId()).getItems();

		assertTrue(postViewer1Posts.contains(simplePost1));
		assertTrue(postViewer2Posts.contains(simplePost1));

		/*
		 * (2) - post must be visible for post owner
		 */
		List<SimplePost> postOwnerPosts = (List<SimplePost>) simplePostDAO.listSimplePostsOfUser(
				postOwner.getId()).getItems();

		assertTrue(postOwnerPosts.contains(simplePost1));

		/*
		 * (3) - post cannot be visible for unauthorized
		 */
		List<SimplePost> unauthorizedPosts = (List<SimplePost>) simplePostDAO.listSimplePostsForUser(
				unauhtorized.getId()).getItems();

		assertFalse(unauthorizedPosts.contains(simplePost1));

		/*
		 * showing post conditions (1)
		 */
		assertTrue(userDAO.getUser(postOwner.getId()).equals(postOwner));

		/*
		 * (2)
		 */
		assertTrue(userDAO.getUser(postViewer1.getId()).equals(postViewer1));
		assertTrue(userDAO.getUser(postViewer2.getId()).equals(postViewer2));
		/*
		 * (3)
		 */
		assertTrue(simplePostDAO.getSimplePost(simplePost1.getId()).equals(simplePost1));

	}

	/**
	 * See A.5.2
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	/*
	 * user removes and add some photos from/to SimplePost
	 */
	@Test
	public void test_updateSimplePost2_shouldOK() throws IOException, GeneralSecurityException {
		User postOwner = new User("user1", "email1");

		/*
		 * simple post creation
		 */
		SimplePost simplePost1 = new SimplePost();
		simplePost1.setDescription("post1");
		simplePost1.setPostedBy(postOwner);

		Photo photo1 = new Photo();
		photo1.setDescription("photo1");
		photo1.setLikesCount(0l);
		photo1.setPath("path1");

		Photo photo2 = new Photo();
		photo2.setDescription("photo2");
		photo2.setLikesCount(0l);
		photo2.setPath("path2");

		User postViewer1 = new User("user2", "email2");
		User postViewer2 = new User("user3", "email3");
		User unauhtorized = new User("unauth", "unauthmail");

		List<Photo> photos = new ArrayList<Photo>();
		photos.add(photo1);
		photos.add(photo2);

		List<User> postViewers = new ArrayList<User>();
		postViewers.add(postViewer1);
		postViewers.add(postViewer2);

		simplePost1.setPhotos(photos);
		simplePost1.setUserCanSeePost(postViewers);

		/*
		 * creating preconditions (1) (2) (3)
		 */
		postOwner = userDAO.insertUser(postOwner);
		postViewer1 = userDAO.insertUser(postViewer1);
		postViewer2 = userDAO.insertUser(postViewer2);
		unauhtorized = userDAO.insertUser(unauhtorized);
		simplePost1 = simplePostDAO.insertSimplePost(simplePost1);

		/*
		 * UPDATE PART BEGIN
		 */
		/*
		 * remove photo
		 */
		simplePost1.getPhotos().remove(photo1);
		/*
		 * add photo
		 */
		Photo photo3 = new Photo();
		photo3.setDescription("photo3");
		photo3.setLikesCount(0l);
		photo3.setPath("path3");

		simplePost1.getPhotos().add(photo3);

		/*
		 * user input
		 */
		simplePost1 = simplePostDAO.updateSimplePost(simplePost1);
		/*
		 * UPDATE PART END
		 */

		/*
		 * what is needed to be shown (2) - post must be visible for all post
		 * viewers
		 */
		List<SimplePost> postViewer1Posts = (List<SimplePost>) simplePostDAO.listSimplePostsForUser(
				postViewer1.getId()).getItems();
		List<SimplePost> postViewer2Posts = (List<SimplePost>) simplePostDAO.listSimplePostsForUser(
				postViewer2.getId()).getItems();

		assertTrue(postViewer1Posts.contains(simplePost1));
		assertTrue(postViewer2Posts.contains(simplePost1));

		/*
		 * (2) - post must be visible for post owner
		 */
		List<SimplePost> postOwnerPosts = (List<SimplePost>) simplePostDAO.listSimplePostsOfUser(
				postOwner.getId()).getItems();

		assertTrue(postOwnerPosts.contains(simplePost1));

		/*
		 * (3) - post cannot be visible for unauthorized
		 */
		List<SimplePost> unauthorizedPosts = (List<SimplePost>) simplePostDAO.listSimplePostsForUser(
				unauhtorized.getId()).getItems();

		assertFalse(unauthorizedPosts.contains(simplePost1));

		/*
		 * showing post conditions (1)
		 */
		assertTrue(userDAO.getUser(postOwner.getId()).equals(postOwner));

		/*
		 * (2)
		 */
		assertTrue(userDAO.getUser(postViewer1.getId()).equals(postViewer1));
		assertTrue(userDAO.getUser(postViewer2.getId()).equals(postViewer2));
		/*
		 * (3)
		 */
		assertTrue(simplePostDAO.getSimplePost(simplePost1.getId()).equals(simplePost1));

	}

	/**
	 * See A.5.2
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	/*
	 * user updates the state of SimplePost and user removes some photos from
	 * SimplePost
	 */
	@Test
	public void test_updateSimplePost3_shouldOK() throws IOException, GeneralSecurityException {
		User postOwner = new User("user1", "email1");

		/*
		 * simple post creation
		 */
		SimplePost simplePost1 = new SimplePost();
		simplePost1.setDescription("post1");
		simplePost1.setPostedBy(postOwner);

		Photo photo1 = new Photo();
		photo1.setDescription("photo1");
		photo1.setLikesCount(0l);
		photo1.setPath("path1");

		Photo photo2 = new Photo();
		photo2.setDescription("photo2");
		photo2.setLikesCount(0l);
		photo2.setPath("path2");

		User postViewer1 = new User("user2", "email2");
		User postViewer2 = new User("user3", "email3");
		User unauhtorized = new User("unauth", "unauthmail");

		List<Photo> photos = new ArrayList<Photo>();
		photos.add(photo1);
		photos.add(photo2);

		List<User> postViewers = new ArrayList<User>();
		postViewers.add(postViewer1);
		postViewers.add(postViewer2);

		simplePost1.setPhotos(photos);
		simplePost1.setUserCanSeePost(postViewers);

		/*
		 * creating preconditions (1) (2) (3)
		 */
		postOwner = userDAO.insertUser(postOwner);
		postViewer1 = userDAO.insertUser(postViewer1);
		postViewer2 = userDAO.insertUser(postViewer2);
		unauhtorized = userDAO.insertUser(unauhtorized);
		simplePost1 = simplePostDAO.insertSimplePost(simplePost1);

		/*
		 * UPDATE PART BEGIN
		 */
		/*
		 * remove photo
		 */
		simplePost1.getPhotos().remove(photo1);
		simplePost1.setDescription("new description");

		/*
		 * user input
		 */
		simplePost1 = simplePostDAO.updateSimplePost(simplePost1);
		/*
		 * UPDATE PART END
		 */

		/*
		 * what is needed to be shown (2) - post must be visible for all post
		 * viewers
		 */
		List<SimplePost> postViewer1Posts = (List<SimplePost>) simplePostDAO.listSimplePostsForUser(
				postViewer1.getId()).getItems();
		List<SimplePost> postViewer2Posts = (List<SimplePost>) simplePostDAO.listSimplePostsForUser(
				postViewer2.getId()).getItems();

		assertTrue(postViewer1Posts.contains(simplePost1));
		assertTrue(postViewer2Posts.contains(simplePost1));

		/*
		 * (2) - post must be visible for post owner
		 */
		List<SimplePost> postOwnerPosts = (List<SimplePost>) simplePostDAO.listSimplePostsOfUser(
				postOwner.getId()).getItems();

		assertTrue(postOwnerPosts.contains(simplePost1));

		/*
		 * (3) - post cannot be visible for unauthorized
		 */
		List<SimplePost> unauthorizedPosts = (List<SimplePost>) simplePostDAO.listSimplePostsForUser(
				unauhtorized.getId()).getItems();

		assertFalse(unauthorizedPosts.contains(simplePost1));

		/*
		 * showing post conditions (1)
		 */
		assertTrue(userDAO.getUser(postOwner.getId()).equals(postOwner));

		/*
		 * (2)
		 */
		assertTrue(userDAO.getUser(postViewer1.getId()).equals(postViewer1));
		assertTrue(userDAO.getUser(postViewer2.getId()).equals(postViewer2));
		/*
		 * (3)
		 */
		assertTrue(simplePostDAO.getSimplePost(simplePost1.getId()).equals(simplePost1));

	}

	/**
	 * See A.5.2
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	/*
	 * user updates the state of SimplePost and user removes and adds some
	 * photos from/to SimplePost
	 */
	@Test
	public void test_updateSimplePost4_shouldOK() throws IOException, GeneralSecurityException {
		User postOwner = new User("user1", "email1");

		/*
		 * simple post creation
		 */
		SimplePost simplePost1 = new SimplePost();
		simplePost1.setDescription("post1");
		simplePost1.setPostedBy(postOwner);

		Photo photo1 = new Photo();
		photo1.setDescription("photo1");
		photo1.setLikesCount(0l);
		photo1.setPath("path1");

		Photo photo2 = new Photo();
		photo2.setDescription("photo2");
		photo2.setLikesCount(0l);
		photo2.setPath("path2");

		User postViewer1 = new User("user2", "email2");
		User postViewer2 = new User("user3", "email3");
		User unauhtorized = new User("unauth", "unauthmail");

		List<Photo> photos = new ArrayList<Photo>();
		photos.add(photo1);
		photos.add(photo2);

		List<User> postViewers = new ArrayList<User>();
		postViewers.add(postViewer1);
		postViewers.add(postViewer2);

		simplePost1.setPhotos(photos);
		simplePost1.setUserCanSeePost(postViewers);

		/*
		 * creating preconditions (1) (2) (3)
		 */
		postOwner = userDAO.insertUser(postOwner);
		postViewer1 = userDAO.insertUser(postViewer1);
		postViewer2 = userDAO.insertUser(postViewer2);
		unauhtorized = userDAO.insertUser(unauhtorized);
		simplePost1 = simplePostDAO.insertSimplePost(simplePost1);

		/*
		 * UPDATE PART BEGIN
		 */
		/*
		 * remove photo
		 */
		simplePost1.getPhotos().remove(photo1);
		simplePost1.setDescription("new description");
		/*
		 * add photo
		 */
		Photo photo3 = new Photo();
		photo3.setDescription("photo3");
		photo3.setLikesCount(0l);
		photo3.setPath("path3");

		simplePost1.getPhotos().add(photo3);
		/*
		 * user input
		 */
		simplePost1 = simplePostDAO.updateSimplePost(simplePost1);
		/*
		 * UPDATE PART END
		 */

		/*
		 * what is needed to be shown (2) - post must be visible for all post
		 * viewers
		 */
		List<SimplePost> postViewer1Posts = (List<SimplePost>) simplePostDAO.listSimplePostsForUser(
				postViewer1.getId()).getItems();
		List<SimplePost> postViewer2Posts = (List<SimplePost>) simplePostDAO.listSimplePostsForUser(
				postViewer2.getId()).getItems();

		assertTrue(postViewer1Posts.contains(simplePost1));
		assertTrue(postViewer2Posts.contains(simplePost1));

		/*
		 * (2) - post must be visible for post owner
		 */
		List<SimplePost> postOwnerPosts = (List<SimplePost>) simplePostDAO.listSimplePostsOfUser(
				postOwner.getId()).getItems();

		assertTrue(postOwnerPosts.contains(simplePost1));

		/*
		 * (3) - post cannot be visible for unauthorized
		 */
		List<SimplePost> unauthorizedPosts = (List<SimplePost>) simplePostDAO.listSimplePostsForUser(
				unauhtorized.getId()).getItems();

		assertFalse(unauthorizedPosts.contains(simplePost1));

		/*
		 * showing post conditions (1)
		 */
		assertTrue(userDAO.getUser(postOwner.getId()).equals(postOwner));

		/*
		 * (2)
		 */
		assertTrue(userDAO.getUser(postViewer1.getId()).equals(postViewer1));
		assertTrue(userDAO.getUser(postViewer2.getId()).equals(postViewer2));
		/*
		 * (3)
		 */
		assertTrue(simplePostDAO.getSimplePost(simplePost1.getId()).equals(simplePost1));

	}

	/**
	 * See A.5.2
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	/*
	 * user updates the state of SimplePost
	 */
	@Test
	public void test_updateSimplePost5_shouldOK() throws IOException, GeneralSecurityException {
		User postOwner = new User("user1", "email1");

		/*
		 * simple post creation
		 */
		SimplePost simplePost1 = new SimplePost();
		simplePost1.setDescription("post1");
		simplePost1.setPostedBy(postOwner);

		Photo photo1 = new Photo();
		photo1.setDescription("photo1");
		photo1.setLikesCount(0l);
		photo1.setPath("path1");

		Photo photo2 = new Photo();
		photo2.setDescription("photo2");
		photo2.setLikesCount(0l);
		photo2.setPath("path2");

		User postViewer1 = new User("user2", "email2");
		User postViewer2 = new User("user3", "email3");
		User unauhtorized = new User("unauth", "unauthmail");

		List<Photo> photos = new ArrayList<Photo>();
		photos.add(photo1);
		photos.add(photo2);

		List<User> postViewers = new ArrayList<User>();
		postViewers.add(postViewer1);
		postViewers.add(postViewer2);

		simplePost1.setPhotos(photos);
		simplePost1.setUserCanSeePost(postViewers);

		/*
		 * creating preconditions (1) (2) (3)
		 */
		postOwner = userDAO.insertUser(postOwner);
		postViewer1 = userDAO.insertUser(postViewer1);
		postViewer2 = userDAO.insertUser(postViewer2);
		unauhtorized = userDAO.insertUser(unauhtorized);
		simplePost1 = simplePostDAO.insertSimplePost(simplePost1);

		/*
		 * UPDATE PART BEGIN
		 */

		simplePost1.setDescription("new description");
		/*
		 * user input
		 */
		simplePost1 = simplePostDAO.updateSimplePost(simplePost1);
		/*
		 * UPDATE PART END
		 */

		/*
		 * what is needed to be shown (2) - post must be visible for all post
		 * viewers
		 */
		List<SimplePost> postViewer1Posts = (List<SimplePost>) simplePostDAO.listSimplePostsForUser(
				postViewer1.getId()).getItems();
		List<SimplePost> postViewer2Posts = (List<SimplePost>) simplePostDAO.listSimplePostsForUser(
				postViewer2.getId()).getItems();

		assertTrue(postViewer1Posts.contains(simplePost1));
		assertTrue(postViewer2Posts.contains(simplePost1));

		/*
		 * (2) - post must be visible for post owner
		 */
		List<SimplePost> postOwnerPosts = (List<SimplePost>) simplePostDAO.listSimplePostsOfUser(
				postOwner.getId()).getItems();

		assertTrue(postOwnerPosts.contains(simplePost1));

		/*
		 * (3) - post cannot be visible for unauthorized
		 */
		List<SimplePost> unauthorizedPosts = (List<SimplePost>) simplePostDAO.listSimplePostsForUser(
				unauhtorized.getId()).getItems();

		assertFalse(unauthorizedPosts.contains(simplePost1));

		/*
		 * showing post conditions (1)
		 */
		assertTrue(userDAO.getUser(postOwner.getId()).equals(postOwner));

		/*
		 * (2)
		 */
		assertTrue(userDAO.getUser(postViewer1.getId()).equals(postViewer1));
		assertTrue(userDAO.getUser(postViewer2.getId()).equals(postViewer2));
		/*
		 * (3)
		 */
		assertTrue(simplePostDAO.getSimplePost(simplePost1.getId()).equals(simplePost1));

	}

	/**
	 * See A.5.2
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	/*
	 * user removes and adds some photos from/to SimplePost
	 */
	@Test
	public void test_updateSimplePost6_shouldOK() throws IOException, GeneralSecurityException {
		User postOwner = new User("user1", "email1");

		/*
		 * simple post creation
		 */
		SimplePost simplePost1 = new SimplePost();
		simplePost1.setDescription("post1");
		simplePost1.setPostedBy(postOwner);

		Photo photo1 = new Photo();
		photo1.setDescription("photo1");
		photo1.setLikesCount(0l);
		photo1.setPath("path1");

		Photo photo2 = new Photo();
		photo2.setDescription("photo2");
		photo2.setLikesCount(0l);
		photo2.setPath("path2");

		User postViewer1 = new User("user2", "email2");
		User postViewer2 = new User("user3", "email3");
		User unauhtorized = new User("unauth", "unauthmail");

		List<Photo> photos = new ArrayList<Photo>();
		photos.add(photo1);
		photos.add(photo2);

		List<User> postViewers = new ArrayList<User>();
		postViewers.add(postViewer1);
		postViewers.add(postViewer2);

		simplePost1.setPhotos(photos);
		simplePost1.setUserCanSeePost(postViewers);

		/*
		 * creating preconditions (1) (2) (3)
		 */
		postOwner = userDAO.insertUser(postOwner);
		postViewer1 = userDAO.insertUser(postViewer1);
		postViewer2 = userDAO.insertUser(postViewer2);
		unauhtorized = userDAO.insertUser(unauhtorized);
		simplePost1 = simplePostDAO.insertSimplePost(simplePost1);

		/*
		 * UPDATE PART BEGIN
		 */
		/*
		 * remove photo
		 */
		simplePost1.setDescription("new description");
		/*
		 * add photo
		 */
		Photo photo3 = new Photo();
		photo3.setDescription("photo3");
		photo3.setLikesCount(0l);
		photo3.setPath("path3");

		simplePost1.getPhotos().add(photo3);
		/*
		 * user input
		 */
		simplePost1 = simplePostDAO.updateSimplePost(simplePost1);
		/*
		 * UPDATE PART END
		 */

		/*
		 * what is needed to be shown (2) - post must be visible for all post
		 * viewers
		 */
		List<SimplePost> postViewer1Posts = (List<SimplePost>) simplePostDAO.listSimplePostsForUser(
				postViewer1.getId()).getItems();
		List<SimplePost> postViewer2Posts = (List<SimplePost>) simplePostDAO.listSimplePostsForUser(
				postViewer2.getId()).getItems();

		assertTrue(postViewer1Posts.contains(simplePost1));
		assertTrue(postViewer2Posts.contains(simplePost1));

		/*
		 * (2) - post must be visible for post owner
		 */
		List<SimplePost> postOwnerPosts = (List<SimplePost>) simplePostDAO.listSimplePostsOfUser(
				postOwner.getId()).getItems();

		assertTrue(postOwnerPosts.contains(simplePost1));

		/*
		 * (3) - post cannot be visible for unauthorized
		 */
		List<SimplePost> unauthorizedPosts = (List<SimplePost>) simplePostDAO.listSimplePostsForUser(
				unauhtorized.getId()).getItems();

		assertFalse(unauthorizedPosts.contains(simplePost1));

		/*
		 * showing post conditions (1)
		 */
		assertTrue(userDAO.getUser(postOwner.getId()).equals(postOwner));

		/*
		 * (2)
		 */
		assertTrue(userDAO.getUser(postViewer1.getId()).equals(postViewer1));
		assertTrue(userDAO.getUser(postViewer2.getId()).equals(postViewer2));
		/*
		 * (3)
		 */
		assertTrue(simplePostDAO.getSimplePost(simplePost1.getId()).equals(simplePost1));

	}

	/**
	 * See A.5.2
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	/*
	 * user adds some photos to SimplePost
	 */
	@Test
	public void test_updateSimplePost7_shouldOK() throws IOException, GeneralSecurityException {
		User postOwner = new User("user1", "email1");

		/*
		 * simple post creation
		 */
		SimplePost simplePost1 = new SimplePost();
		simplePost1.setDescription("post1");
		simplePost1.setPostedBy(postOwner);

		Photo photo1 = new Photo();
		photo1.setDescription("photo1");
		photo1.setLikesCount(0l);
		photo1.setPath("path1");

		Photo photo2 = new Photo();
		photo2.setDescription("photo2");
		photo2.setLikesCount(0l);
		photo2.setPath("path2");

		User postViewer1 = new User("user2", "email2");
		User postViewer2 = new User("user3", "email3");
		User unauhtorized = new User("unauth", "unauthmail");

		List<Photo> photos = new ArrayList<Photo>();
		photos.add(photo1);
		photos.add(photo2);

		List<User> postViewers = new ArrayList<User>();
		postViewers.add(postViewer1);
		postViewers.add(postViewer2);

		simplePost1.setPhotos(photos);
		simplePost1.setUserCanSeePost(postViewers);

		/*
		 * creating preconditions (1) (2) (3)
		 */
		postOwner = userDAO.insertUser(postOwner);
		postViewer1 = userDAO.insertUser(postViewer1);
		postViewer2 = userDAO.insertUser(postViewer2);
		unauhtorized = userDAO.insertUser(unauhtorized);
		simplePost1 = simplePostDAO.insertSimplePost(simplePost1);

		/*
		 * UPDATE PART BEGIN
		 */
		/*
		 * add photo
		 */
		Photo photo3 = new Photo();
		photo3.setDescription("photo3");
		photo3.setLikesCount(0l);
		photo3.setPath("path3");

		simplePost1.getPhotos().add(photo3);
		/*
		 * user input
		 */
		simplePost1 = simplePostDAO.updateSimplePost(simplePost1);
		/*
		 * UPDATE PART END
		 */

		/*
		 * what is needed to be shown (2) - post must be visible for all post
		 * viewers
		 */
		List<SimplePost> postViewer1Posts = (List<SimplePost>) simplePostDAO.listSimplePostsForUser(
				postViewer1.getId()).getItems();
		List<SimplePost> postViewer2Posts = (List<SimplePost>) simplePostDAO.listSimplePostsForUser(
				postViewer2.getId()).getItems();

		assertTrue(postViewer1Posts.contains(simplePost1));
		assertTrue(postViewer2Posts.contains(simplePost1));

		/*
		 * (2) - post must be visible for post owner
		 */
		List<SimplePost> postOwnerPosts = (List<SimplePost>) simplePostDAO.listSimplePostsOfUser(
				postOwner.getId()).getItems();

		assertTrue(postOwnerPosts.contains(simplePost1));

		/*
		 * (3) - post cannot be visible for unauthorized
		 */
		List<SimplePost> unauthorizedPosts = (List<SimplePost>) simplePostDAO.listSimplePostsForUser(
				unauhtorized.getId()).getItems();

		assertFalse(unauthorizedPosts.contains(simplePost1));

		/*
		 * showing post conditions (1)
		 */
		assertTrue(userDAO.getUser(postOwner.getId()).equals(postOwner));

		/*
		 * (2)
		 */
		assertTrue(userDAO.getUser(postViewer1.getId()).equals(postViewer1));
		assertTrue(userDAO.getUser(postViewer2.getId()).equals(postViewer2));
		/*
		 * (3)
		 */
		assertTrue(simplePostDAO.getSimplePost(simplePost1.getId()).equals(simplePost1));

	}

	/**
	 * Test case 1, see A.6.2
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	@Test
	public void test_deleteSimplePost_shouldOK() throws IOException, GeneralSecurityException {

		User postOwner = new User("user1", "email1");

		/*
		 * simple post creation
		 */
		SimplePost simplePost1 = new SimplePost();
		simplePost1.setDescription("post1");
		simplePost1.setPostedBy(postOwner);

		Photo photo1 = new Photo();
		photo1.setDescription("photo1");
		photo1.setLikesCount(0l);
		photo1.setPath("path1");

		Photo photo2 = new Photo();
		photo2.setDescription("photo2");
		photo2.setLikesCount(0l);
		photo2.setPath("path2");

		User postViewer1 = new User("user2", "email2");
		User postViewer2 = new User("user3", "email3");
		User unauhtorized = new User("unauth", "unauthmail");

		List<Photo> photos = new ArrayList<Photo>();
		photos.add(photo1);
		photos.add(photo2);

		List<User> postViewers = new ArrayList<User>();
		postViewers.add(postViewer1);
		postViewers.add(postViewer2);

		simplePost1.setPhotos(photos);
		simplePost1.setUserCanSeePost(postViewers);

		/*
		 * creating preconditions (1) (2) (3)
		 */
		postOwner = userDAO.insertUser(postOwner);
		postViewer1 = userDAO.insertUser(postViewer1);
		postViewer2 = userDAO.insertUser(postViewer2);
		unauhtorized = userDAO.insertUser(unauhtorized);
		simplePost1 = simplePostDAO.insertSimplePost(simplePost1);

		/*
		 * user input
		 */
		simplePostDAO.removeSimplePost(simplePost1.getId());

		/*
		 * what is needed to be shown:
		 */
		assertFalse(simplePostDAO.listSimplePost().getItems().contains(simplePost1));

		/*
		 * showing post conditions: Post condition (3) is shown in the assert
		 * above (1), (2)
		 */
		assertTrue(userDAO.getUser(postOwner.getId()).equals(postOwner));
		assertTrue(userDAO.getUser(postViewer1.getId()).equals(postViewer1));
		assertTrue(userDAO.getUser(postViewer2.getId()).equals(postViewer2));

	}

}
