package com.filip.dressfriend.friendship;

import com.filip.dressfriend.Friendrequest;
import com.filip.dressfriend.Friendship;

public interface ICreateFriendship {

	public abstract Friendship insertFriendship(Friendship friendship);

	public abstract Friendship createFriendshipFromFriendrequest(Friendrequest fr);

}