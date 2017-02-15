package com.filip.dressfriend.comment;

import com.filip.dressfriend.Comment;
import com.google.api.server.spi.response.CollectionResponse;

public interface IReadComment {

	public abstract CollectionResponse<Comment> listNewCommentsOfPhoto(Long photoId, int clientCommentsCount,
			int clientCommentsHashCode);

	public abstract int getPhotoCommentsCount(Long photo_id);

}