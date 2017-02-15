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
 * These test cases are created for the needs of Bachelor thesis: "Model based testing of cloud based social networks"
 * @author Filip Rydzi (C) 
 *
 */

public class FriendshipServiceTest {

	private UserService userService;
	private FriendshipService friendshipService;
	private FriendrequestService friendrequestService;

	private EntityTransaction tx;
	private EntityManager mgr;

	@Before
	public void setUp() throws Exception {
		mgr = EMF.get().createEntityManager();
		tx = mgr.getTransaction();
		tx.begin();

		userService = new UserServiceImpl(mgr);
		friendshipService = new FriendshipServiceImpl(mgr);
		friendrequestService = new FriendrequestServiceImpl(mgr);
	}

	@After
	public void tearDown() throws Exception {
		userService = null;
		friendshipService = null;
		friendrequestService = null;

		tx.rollback();
		mgr.close();
	}

	/**
	 * Test case 1, see section 4.2.1 Accepting a friend request
	 */
	@Test(expected = EntityExistsException.class)
	public void test_createFriendshipFromFriendrequest_shouldThrowEntityExistsException() {
		
		// creating preconditions
		User sender = new User("senderName", "sender@somemail.com");
		User receiver = new User("receiverName", "reciever@somemail.com");
		sender = userService.insertUser(sender);
		receiver = userService.insertUser(receiver);
		Friendrequest f1 = new Friendrequest(sender, receiver);
		friendrequestService.insertFriendrequest(f1);

		//initial condition
		Friendship f = new Friendship(sender, receiver);
		friendshipService.insertFriendship(f);
		
		/*
		 * user input
		 * expected result (1) EntityExistsException should be thrown here
		 */
		friendshipService.createFriendshipFromFriendrequest(f1);
		

		// post conditions
		assertTrue(friendrequestService.getFriendrequest(f1.getId()) == null);
		assertTrue(userService.getUser(sender.getId()).equals(sender));
		assertTrue(userService.getUser(receiver.getId()).equals(receiver));
		assertFalse(sender.equals(receiver));

	}
	
	/**
	 * Test case 2, see section 4.2.1 Accepting a friend request
	 */
	@Test
	public void test_createFriendshipFromFriendrequest_shouldOK() {

		// creating preconditions
		User sender = new User("senderName", "sender@somemail.com");
		User receiver = new User("receiverName", "reciever@somemail.com");
		sender = userService.insertUser(sender);
		receiver = userService.insertUser(receiver);
		Friendrequest f1 = new Friendrequest(sender, receiver);
		friendrequestService.insertFriendrequest(f1);

		/*
		 * user input
		 */
		Friendship friendship = friendshipService.createFriendshipFromFriendrequest(f1);

		/*
		 * what is needed to be shown part (1)
		 */
		assertTrue((friendship.getUser1().equals(receiver) || friendship.getUser1().equals(sender))
				&& (friendship.getUser2().equals(receiver) || friendship.getUser2().equals(sender)));

		/*
		 * what is needed to be shown part (2),(3)
		 */
		List<Friendship> senderFriendships = (List<Friendship>) friendshipService.listFriendshipsOfUser(sender.getId()).getItems();
		List<Friendship> receiverFriendships = (List<Friendship>) friendshipService.listFriendshipsOfUser(receiver.getId()).getItems();
		
		assertTrue(senderFriendships.contains(friendship));
		assertTrue(receiverFriendships.contains(friendship));

		/*
		 * what is needed to be shown part (4)
		 */
		User unauthorized = new User("unauth", "unauth");
		unauthorized = userService.insertUser(unauthorized);
		
		assertFalse(friendshipService.listFriendshipsOfUser(unauthorized.getId()).getItems().contains(friendship));
		
		// post conditions
		assertTrue(friendrequestService.getFriendrequest(f1.getId()) == null);
		assertTrue(userService.getUser(sender.getId()).equals(sender));
		assertTrue(userService.getUser(receiver.getId()).equals(receiver));
		assertFalse(sender.equals(receiver));
	}

	/**
	 * Test case 1, see A.3.2
	 */
	@Test
	public void test_deleteFriendship_shouldOK() {
		/*
		 * creating preconditions
		 * (1) (2)
		 */
		User friendA = new User("user1", "email1");
		User friendB = new User("user2", "email2");

		friendA = userService.insertUser(friendA);
		friendB = userService.insertUser(friendB);

		/*
		 * creating preconditions
		 * (3)
		 */
		Friendship friendship = new Friendship(friendA, friendB);
		friendship = friendshipService.insertFriendship(friendship);

		/*
		 * user input
		 */
		friendshipService.removeFriendship(friendship.getId());
		
		/*
		 * What needs to be shown
		 * (1) (2)
		 * and Post condition (3)
		 */
		List<Friendship> allFriendships = (List<Friendship>) friendshipService.listFriendship().getItems();

		assertFalse(allFriendships.contains(friendship));
		
		/*
		 * showing post conditions
		 * (1) (2)
		 */
		assertTrue(userService.getUser(friendA.getId()).equals(friendA) && userService.getUser(friendB.getId()).equals(friendB));
		
	}
	
}
