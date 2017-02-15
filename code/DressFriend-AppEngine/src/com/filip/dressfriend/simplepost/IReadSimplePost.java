package com.filip.dressfriend.simplepost;

import com.filip.dressfriend.SimplePost;
import com.google.api.server.spi.response.CollectionResponse;

public interface IReadSimplePost {

	public abstract CollectionResponse<SimplePost> listSimplePost();

	public abstract CollectionResponse<SimplePost> listSimplePostsOfUser(Long userId);

	public abstract CollectionResponse<SimplePost> listSimplePostsForUser(Long userId);

	public abstract SimplePost getSimplePost(Long id);

}