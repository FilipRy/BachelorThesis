package com.filip.dressfriend.dislikes;

import com.filip.dressfriend.Dislikes;
import com.filip.dressfriend.exception.DislikeAlreadyExistsException;
import com.filip.dressfriend.exception.LikeAlreadyExistsException;
import com.filip.dressfriend.exception.UnauthorizedDisLikeException;
import com.google.api.server.spi.response.CollectionResponse;

public interface DislikesService {
	
	public Dislikes insertDislikes(Dislikes dislikes) throws LikeAlreadyExistsException, DislikeAlreadyExistsException, UnauthorizedDisLikeException;
	
	public CollectionResponse<Dislikes> listDislikesOfPhoto (Long photoId, int clientDislikesCount, int clientDislikesHashCode);
	
	public Dislikes getUserDislikeAtPost(Long postId, Long userId);
	public Long getPhotoDislikesCount(Long photo_id);
	
	/*
	 * returns whether there is a dislike by user with ID = user_id at post with ID = postId 
	 */
	public boolean existsPostsDislikesByUser(Long user_id, Long postId);
	
	
}
