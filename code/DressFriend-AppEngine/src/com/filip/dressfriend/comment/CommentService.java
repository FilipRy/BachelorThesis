package com.filip.dressfriend.comment;

import com.filip.dressfriend.Comment;
import com.google.api.server.spi.response.CollectionResponse;

public interface CommentService extends ICreateComment {
	
	public CollectionResponse<Comment> listNewCommentsOfPhoto(Long photoId, int clientCommentsCount, int clientCommentsHashCode);
	
	public int getPhotoCommentsCount(Long photo_id);
	
}
