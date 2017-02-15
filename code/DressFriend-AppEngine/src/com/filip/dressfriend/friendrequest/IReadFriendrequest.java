package com.filip.dressfriend.friendrequest;

import com.filip.dressfriend.Friendrequest;
import com.google.api.server.spi.response.CollectionResponse;

public interface IReadFriendrequest {

	public abstract CollectionResponse<Friendrequest> listFriendrequest();

	public abstract CollectionResponse<Friendrequest> listFriendrequestsFromUser(Long userId);

	public abstract CollectionResponse<Friendrequest> listFriendrequestsToUser(Long userId);

	public abstract boolean isFriendrequestSent(Long user1Id, Long user2Id);

	public abstract Friendrequest getFriendrequest(Long id);

}