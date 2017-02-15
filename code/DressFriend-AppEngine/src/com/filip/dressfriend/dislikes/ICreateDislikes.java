package com.filip.dressfriend.dislikes;

import com.filip.dressfriend.Dislikes;
import com.filip.dressfriend.exception.DislikeAlreadyExistsException;
import com.filip.dressfriend.exception.LikeAlreadyExistsException;
import com.filip.dressfriend.exception.UnauthorizedDisLikeException;

public interface ICreateDislikes {

	public abstract Dislikes insertDislikes(Dislikes dislikes) throws LikeAlreadyExistsException,
			DislikeAlreadyExistsException, UnauthorizedDisLikeException;

}