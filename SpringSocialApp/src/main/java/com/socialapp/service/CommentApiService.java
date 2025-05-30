package com.socialapp.service;

import com.socialapp.dto.CommentDTO;
import com.socialapp.pojo.User;

public interface CommentApiService {

    CommentDTO createCommentApi(int postId, String content, User currentUser);

    CommentDTO updateCommentApi(Integer commentId, String content, User currentUser);

    boolean deleteCommentApi(Integer commentId, User currentUser);

}
