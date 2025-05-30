package com.socialapp.service;

import com.socialapp.pojo.Comment;
import java.util.List;
import java.util.Map;

public interface CommentService {

    List<Comment> getComments(Map<String, String> params);

    Comment getCommentById(int id);

    Comment addOrUpdateComment(Comment comment);

    void deleteComment(int id);

    List<Comment> getCommentsByPostId(int postId);

    Comment createComment(int postId, int userId, String content);
}
