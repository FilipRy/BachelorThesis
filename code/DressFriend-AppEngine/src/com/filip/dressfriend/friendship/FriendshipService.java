package com.filip.dressfriend.friendship;

import com.filip.dressfriend.Friendrequest;
import com.filip.dressfriend.Friendship;
import com.google.api.server.spi.response.CollectionResponse;

public interface FriendshipService {
	
	
	/**
	 * This inserts a new entity into App Engine datastore. If the entity
	 * already exists in the datastore, an exception is thrown.
	 * @param friendship
	 *            the entity to be inserted.
	 * @return The inserted entity.
	 */
	public Friendship insertFriendship(Friendship friendship);
	/**
	 * 
	 * @param fr
	 *            the Friendrequest from which the new Friendship should be
	 *            created.
	 * @return The inserted entity.
	 */
	public Friendship createFriendshipFromFriendrequest(Friendrequest fr);

	
	/**
	 * @return A CollectionResponse class containing the list of all entities
	 *         persisted and a cursor to the next page.
	 */
	public CollectionResponse<Friendship> listFriendship();
	public CollectionResponse<Friendship> listFriendshipsOfUser(Long userId);

	public boolean areUsersFriends(Long user1Id, Long user2Id);

	/**
	 * @param id
	 *            the primary key of the entity to be deleted.
	 */
	public void removeFriendship(Long id);

}