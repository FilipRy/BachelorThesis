package com.filip.dressfriend.likes;

import com.filip.dressfriend.Likes;
import com.google.api.server.spi.response.CollectionResponse;

public interface IReadLikes {

	public abstract CollectionResponse<Likes> listLikesOfPhoto(Long photoId, int clientLikesCount,
			int hashCode);

	public abstract Likes getUserLikeAtPost(Long postId, Long userId);

	public abstract Long getPhotoLikesCount(Long photo_id);

	/**
	 * The test case LikesServiceTest::test_insertLikes_shouldThrowException has
	 * discovered a bug in this method. This method checks whether there is a
	 * like on photo (with ID = photoId) by user (with ID = user_id), but it
	 * should check whether there is a like by a user within a post.
	 */
	public abstract boolean existsPostsLikesByUser(Long user_id, Long photoId);

}