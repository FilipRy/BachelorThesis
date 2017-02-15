package com.filip.dressfriend.friendrequest;

import com.filip.dressfriend.Friendrequest;
import com.google.api.server.spi.response.CollectionResponse;

public interface FriendrequestService {

	public Friendrequest insertFriendrequest(Friendrequest friendrequest);
	
	public CollectionResponse<Friendrequest> listFriendrequest();
	public CollectionResponse<Friendrequest> listFriendrequestsFromUser(Long userId);
	public CollectionResponse<Friendrequest> listFriendrequestsToUser(Long userId);
	
	public boolean isFriendrequestSent(Long user1Id, Long user2Id);
	
	public Friendrequest getFriendrequest(Long id);
	
	public void removeFriendrequest(Long id);
	
}
