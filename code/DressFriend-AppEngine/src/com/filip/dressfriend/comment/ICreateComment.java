package com.filip.dressfriend.comment;

import com.filip.dressfriend.Comment;
import com.filip.dressfriend.exception.UnAuthorziedCommentException;

public interface ICreateComment {

	public abstract Comment insertComment(Comment comment) throws UnAuthorziedCommentException;

}