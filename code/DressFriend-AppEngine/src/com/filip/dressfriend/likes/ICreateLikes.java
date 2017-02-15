package com.filip.dressfriend.likes;

import com.filip.dressfriend.Likes;
import com.filip.dressfriend.exception.DislikeAlreadyExistsException;
import com.filip.dressfriend.exception.LikeAlreadyExistsException;
import com.filip.dressfriend.exception.UnauthorizedDisLikeException;

public interface ICreateLikes {

	public abstract Likes insertLikes(Likes likes) throws DislikeAlreadyExistsException,
			UnauthorizedDisLikeException, LikeAlreadyExistsException;

}