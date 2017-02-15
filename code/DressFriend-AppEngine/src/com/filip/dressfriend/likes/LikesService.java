package com.filip.dressfriend.likes;

import com.filip.dressfriend.Likes;
import com.filip.dressfriend.exception.DislikeAlreadyExistsException;
import com.filip.dressfriend.exception.LikeAlreadyExistsException;
import com.filip.dressfriend.exception.UnauthorizedDisLikeException;
import com.google.api.server.spi.response.CollectionResponse;

public interface LikesService {
	
	public Likes insertLikes(Likes likes) throws DislikeAlreadyExistsException, UnauthorizedDisLikeException, LikeAlreadyExistsException;
	
	public CollectionResponse<Likes> listLikesOfPhoto(Long photoId, int clientLikesCount, int hashCode);

	
	public Likes getUserLikeAtPost(Long postId, Long userId);
	public Long getPhotoLikesCount(Long photo_id);
	
	/*
	 * returns whether there is a like by user with ID = user_id at post with ID = postId 
	 */
	public boolean existsPostsLikesByUser(Long user_id, Long postId);
	
	
}
