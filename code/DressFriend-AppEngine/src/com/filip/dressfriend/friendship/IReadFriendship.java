package com.filip.dressfriend.friendship;

import com.filip.dressfriend.Friendship;
import com.google.api.server.spi.response.CollectionResponse;

public interface IReadFriendship {

	public abstract CollectionResponse<Friendship> listFriendship();

	public abstract CollectionResponse<Friendship> listFriendshipsOfUser(Long userId);

	public abstract boolean areUsersFriends(Long user1Id, Long user2Id);

}