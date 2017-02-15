package com.filip.dressfriend.dislikes;

import com.filip.dressfriend.Dislikes;
import com.google.api.server.spi.response.CollectionResponse;

public interface IReadDislikes {

	public abstract CollectionResponse<Dislikes> listDislikesOfPhoto(Long photoId, int clientDislikesCount,
			int clientDislikesHashCode);

	public abstract Dislikes getUserDislikeAtPost(Long postId, Long userId);

	public abstract Long getPhotoDislikesCount(Long photo_id);

	public abstract boolean existsPostsDislikesByUser(Long user_id, Long postId);

}