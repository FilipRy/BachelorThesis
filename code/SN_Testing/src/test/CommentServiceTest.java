package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.filip.dressfriend.Comment;
import com.filip.dressfriend.EMF;
import com.filip.dressfriend.Photo;
import com.filip.dressfriend.SimplePost;
import com.filip.dressfriend.User;
import com.filip.dressfriend.comment.CommentService;
import com.filip.dressfriend.comment.CommentServiceImpl;
import com.filip.dressfriend.exception.UnAuthorziedCommentException;
import com.filip.dressfriend.simplepost.SimplePostService;
import com.filip.dressfriend.simplepost.SimplePostServiceImpl;
import com.filip.dressfriend.user.UserService;
import com.filip.dressfriend.user.UserServiceImpl;

/**
 * These test cases are created for the needs of Bachelor thesis: "Model based testing of cloud based social networks"
 * @author Filip Rydzi (C)
 *
 */

public class CommentServiceTest {
	
	private CommentService commentDAO;
	private SimplePostService simplePostDAO;
	private UserService userDAO;
	private EntityManager mgr;
	private EntityTransaction tx;

	@Before
	public void setUp() throws Exception {
		
		mgr = EMF.get().createEntityManager();
		tx = mgr.getTransaction();
		tx.begin();
		
		commentDAO = new CommentServiceImpl(mgr);
		simplePostDAO = new SimplePostServiceImpl(mgr);
		userDAO = new UserServiceImpl(mgr);
	}

	@After
	public void tearDown() throws Exception {
		commentDAO = null;
		simplePostDAO = null;
		userDAO = null;
		
		tx.rollback();
		mgr.close();
		
	}
	
	/**
	 * Test case 1, see A.8.2
	 * @throws UnAuthorziedCommentException 
	 */
	@Test
	public void test_insertComment_shouldOK() throws UnAuthorziedCommentException {
		/*
		 * Creating preconditions
		 */
		User postOwner = new User("user1","email1");
		User postViewer = new User("user3", "email123");
		User commentCreator = new User("user2","email2");
		
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
		postViewers.add(commentCreator);
		postViewers.add(postViewer);
		simplePost1.setUserCanSeePost(postViewers);
		
		postOwner = userDAO.insertUser(postOwner);
		commentCreator = userDAO.insertUser(commentCreator);
		postViewer = userDAO.insertUser(postViewer);
		simplePost1 = simplePostDAO.insertSimplePost(simplePost1);
		
		/*
		 * user input
		 */
		Comment comment = new Comment();
		comment.setPhoto(photo1);
		comment.setUser(commentCreator);
		comment.setTimestamp(System.currentTimeMillis());
		
		/*
		 * what is needed to be shown part
		 */
		comment = commentDAO.insertComment(comment); // (1) comment was returned to the post viewer(comment creator)
		
		/*
		 * comment is Comment at photo of a post
		 * (2)
		 */
		assertTrue(comment.getPhoto().equals(photo1));
		
		/*
		 * comment must be visible for post viewers of comment' post (3)
		 */
		List<SimplePost> postVisibleForPostViewer = (List<SimplePost>) simplePostDAO.listSimplePostsForUser(postViewer.getId()).getItems();
		
		for(SimplePost sp : postVisibleForPostViewer) {
			if(sp.getPhotos().contains(photo1)) {
				List<Comment> dislikesOfPhoto = (List<Comment>) commentDAO.listNewCommentsOfPhoto(photo1.getId(), 0, 0).getItems();
				assertTrue(dislikesOfPhoto.contains(comment));
			}
		}
		
		/*
		 * showing post conditions
		 */
		assertTrue(simplePostDAO.getSimplePost(simplePost1.getId()).equals(simplePost1)); // (1)
		assertTrue(userDAO.getUser(commentCreator.getId()).equals(commentCreator));// (2)
		assertTrue(userDAO.getUser(postViewer.getId()).equals(postViewer));// (3)
	}
	
	/**
	 * Test case 2, see A.8.2
	 * @throws UnAuthorziedCommentException 
	 */
	@Test(expected = UnAuthorziedCommentException.class)
	public void test_insertComment_shouldThrowException() throws UnAuthorziedCommentException {
		/*
		 * Creating preconditions
		 */
		User postOwner = new User("user1","email1");
		User postViewer = new User("user3", "email123");
		User commentCreator = new User("user2","email2");
		
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
		postViewers.add(postViewer);
		simplePost1.setUserCanSeePost(postViewers);
		
		postOwner = userDAO.insertUser(postOwner);
		commentCreator = userDAO.insertUser(commentCreator);
		postViewer = userDAO.insertUser(postViewer);
		simplePost1 = simplePostDAO.insertSimplePost(simplePost1);
		
		/*
		 * user input
		 */
		Comment comment = new Comment();
		comment.setPhoto(photo1);
		comment.setUser(commentCreator);
		comment.setTimestamp(System.currentTimeMillis());
		
		/*
		 * The Exception should be thrown here, commentCreator is not a post viewer of simplePost1.
		 */
		comment = commentDAO.insertComment(comment);
		
	}

}
