package test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.filip.dressfriend.EMF;
import com.filip.dressfriend.Friendrequest;
import com.filip.dressfriend.Friendship;
import com.filip.dressfriend.User;
import com.filip.dressfriend.friendrequest.FriendrequestService;
import com.filip.dressfriend.friendrequest.FriendrequestServiceImpl;
import com.filip.dressfriend.friendship.FriendshipService;
import com.filip.dressfriend.friendship.FriendshipServiceImpl;
import com.filip.dressfriend.user.UserService;
import com.filip.dressfriend.user.UserServiceImpl;

/**
 * These test cases are created for the needs of Bachelor thesis:
 * "Model based testing of cloud based social networks"
 * 
 * @author Filip Rydzi (C)
 * 
 */

public class FriendrequestServiceTest {

	private FriendrequestService friendrequestService;
	private FriendshipService friendshipService;
	private UserService userService;
	private EntityTransaction tx;
	private EntityManager mgr;

	@Before
	public void setUp() throws Exception {
		mgr = EMF.get().createEntityManager();
		tx = mgr.getTransaction();
		tx.begin();

		friendrequestService = new FriendrequestServiceImpl(mgr);
		userService = new UserServiceImpl(mgr);
		friendshipService = new FriendshipServiceImpl(mgr);
	}

	@After
	public void tearDown() throws Exception {
		friendrequestService = null;
		userService = null;
		friendshipService = null;

		tx.rollback();
		mgr.close();
	}

	/**
	 * Test case 1, see A.1.2
	 */
	@Test(expected = EntityExistsException.class)
	public void test_insertFriendrequest_shouldThrowEntityExistsException() {

		User sender = new User("senderName", "sender@somemail.com");
		User receiver = new User("receiverName", "reciever@somemail.com");

		/*
		 * creating precondition (1), (2), (3)
		 */
		sender = userService.insertUser(sender);
		receiver = userService.insertUser(receiver);

		/*
		 * creating initial condition
		 */
		Friendrequest f1 = new Friendrequest(sender, receiver);
		friendrequestService.insertFriendrequest(f1);

		/*
		 * user input
		 */
		Friendrequest friendrequest = new Friendrequest(sender, receiver);
		friendrequest = friendrequestService.insertFriendrequest(friendrequest);

		List<Friendrequest> allFriendrequests = (List<Friendrequest>) friendrequestService
				.listFriendrequest().getItems();

		assertTrue(allFriendrequests.contains(f1));
	}

	/**
	 * Test case 2
	 */
	@Test
	public void test_insertFriendrequest_shouldOK() {

		User sender = new User("senderName", "sender@somemail.com");
		User receiver = new User("receiverName", "reciever@somemail.com");

		/*
		 * creating precondition (1), (2), (3)
		 */
		sender = userService.insertUser(sender);
		receiver = userService.insertUser(receiver);

		/*
		 * no operation neccessary to create initial condition
		 */

		/*
		 * user input
		 */
		Friendrequest friendrequest = new Friendrequest(sender, receiver);
		friendrequest = friendrequestService.insertFriendrequest(friendrequest);// "what needs to be shown"
																				// part(1)

		/*
		 * "what needs to be shown" part (2)
		 */
		assertTrue(friendrequest.getUserFrom().equals(sender) && friendrequest.getUserTo().equals(receiver));

		/*
		 * "what needs to be shown part" (3)
		 */
		assertTrue(friendrequestService.listFriendrequestsFromUser(sender.getId()).getItems()
				.contains(friendrequest));
		assertTrue(friendrequestService.listFriendrequestsToUser(receiver.getId()).getItems()
				.contains(friendrequest));

		/*
		 * "what is needed to be shown" part (4)
		 */
		User unauthorized = new User("unauth", "unauth");
		unauthorized = userService.insertUser(unauthorized);

		assertTrue(!friendrequestService.listFriendrequestsFromUser(unauthorized.getId()).getItems()
				.contains(unauthorized)
				&& !friendrequestService.listFriendrequestsToUser(unauthorized.getId()).getItems()
						.contains(unauthorized));

		/*
		 * showing post conditions
		 */
		assertTrue(userService.getUser(sender.getId()).equals(sender));
		assertTrue(userService.getUser(receiver.getId()).equals(receiver));
	}

	/**
	 * This test case has discovered a bug. See section 5.1.1 Sending a friend
	 * request.
	 */
	@Test(expected = Exception.class)
	public void test_insertFriendrequest_shouldThrowException() {

		User sender = new User("senderName", "sender@somemail.com");
		User receiver = new User("receiverName", "reciever@somemail.com");

		/*
		 * creating precondition (1), (2), (3)
		 */
		sender = userService.insertUser(sender);
		receiver = userService.insertUser(receiver);

		/*
		 * creating friendship between sender and receiver
		 */
		Friendship friendship = new Friendship(sender, receiver);
		friendship = friendshipService.insertFriendship(friendship);

		/*
		 * user input, the Exception should be thrown here. Sender and receiver
		 * are already friends, therefore the system should forbid the creation
		 * of Friendrequest between sender and receiver.
		 */
		Friendrequest friendrequest = new Friendrequest(sender, receiver);
		friendrequest = friendrequestService.insertFriendrequest(friendrequest);
	}

	/**
	 * Test case 1 and Test case 2, see A 2.2
	 */
	@Test
	public void test_deleteFriendrequest_shouldOK() {
		User sender = new User("user1", "email1");
		User receiver = new User("user2", "email2");

		/*
		 * creating preconditions (1) (2) (3)
		 */
		sender = userService.insertUser(sender);
		receiver = userService.insertUser(receiver);

		/*
		 * creating preconditions (4)
		 */
		Friendrequest f1 = new Friendrequest(sender, receiver);
		friendrequestService.insertFriendrequest(f1);

		/*
		 * user input
		 */
		friendrequestService.removeFriendrequest(f1.getId());

		List<Friendrequest> allFriendrequests = (List<Friendrequest>) friendrequestService
				.listFriendrequest().getItems();

		/*
		 * What needs to be shown (1), (2), and post condition (4)
		 */
		assertFalse(allFriendrequests.contains(f1));

		/*
		 * showing post conditions
		 */
		assertTrue(userService.getUser(sender.getId()).equals(sender));// (1)
		assertTrue(userService.getUser(receiver.getId()).equals(receiver));// (2)
		assertFalse(sender.equals(receiver));// (3)

	}

}
