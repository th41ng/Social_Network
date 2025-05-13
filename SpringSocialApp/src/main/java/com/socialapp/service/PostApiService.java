package com.socialapp.service;

import com.socialapp.dto.PostDTO;
import com.socialapp.pojo.Comment;
import com.socialapp.pojo.Post;
import java.util.List;
import java.util.Map;

/**
 * Service interface for handling post-related operations for the API.
 */
public interface PostApiService {

    /**
     * Gets the list of posts based on the provided filters for the API.
     *
     * @param params The parameters to filter the posts.
     * @return List of PostDTO objects.
     */
    List<PostDTO> getPosts(Map<String, String> params);

    /**
     * Gets a post by its ID for the API.
     *
     * @param id The ID of the post.
     * @return A PostDTO object.
     */
    PostDTO getPostById(int id);

    /**
     * Add or update a post for the API.
     *
     * @param post The post to add or update.
     * @return The saved or updated PostDTO object.
     */
    PostDTO addOrUpdatePost(Post post);

    /**
     * Deletes a post by its ID for the API.
     *
     * @param id The ID of the post to delete.
     */
    void deletePost(int id);

    /**
     * Gets comments for a post by its ID for the API.
     *
     * @param postId The post ID.
     * @return A list of Comment objects.
     */
    List<Comment> getCommentsByPostId(int postId);
}
