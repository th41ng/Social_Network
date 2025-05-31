package com.socialapp.repository;

import com.socialapp.pojo.Comment;
import com.socialapp.pojo.Post;
import java.util.List;
import java.util.Map;

public interface PostRepository {

    List<Post> getPosts(Map<String, String> params);

    List<Post> getPostsByUserId(int userId);

    Post getPostById(int id);

    Post addOrUpdatePost(Post post);

    void deletePost(int id);

    List<Comment> getCommentsByPostId(int postId);

    long countPosts(Map<String, String> params);

    int countPostsCreatedToday();
}
