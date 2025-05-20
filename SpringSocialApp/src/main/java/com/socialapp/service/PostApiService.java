package com.socialapp.service;

import com.socialapp.dto.PostDTO;
import com.socialapp.pojo.Comment;
import com.socialapp.pojo.Post;
import com.socialapp.pojo.User; // <<<< THÊM IMPORT CHO User POJO
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
     * @return A PostDTO object, or null if not found or deleted.
     */
    PostDTO getPostById(int id);

    /**
     * Add or update a post for the API. Requires the current authenticated user
     * for permission checks during update and for associating the user with a
     * new post.
     *
     * @param postFromRequest The post data from the request. For updates, this
     * should include postId.
     * @param currentUser The currently authenticated user performing the
     * action.
     * @return The saved or updated PostDTO object.
     * @throws SecurityException if the user is not authorized to update the
     * post.
     * @throws jakarta.persistence.EntityNotFoundException if the post to be
     * updated is not found.
     */
    PostDTO addOrUpdatePost(Post postFromRequest, User currentUser); // <<<< THAY ĐỔI SIGNATURE

    /**
     * Deletes a post by its ID for the API. Requires the current authenticated
     * user for permission checks.
     *
     * @param postId The ID of the post to delete.
     * @param currentUser The currently authenticated user performing the
     * action.
     * @return true if deletion was successful (post was found and user had
     * permission). (Hoặc có thể là void và ném Exception nếu thất bại)
     * @throws SecurityException if the user is not authorized to delete the
     * post.
     * @throws jakarta.persistence.EntityNotFoundException if the post to be
     * deleted is not found.
     */
    boolean deletePost(int postId, User currentUser); // <<<< THAY ĐỔI SIGNATURE

    /**
     * Gets comments for a post by its ID for the API. Only returns comments for
     * non-deleted posts and non-deleted comments.
     *
     * @param postId The post ID.
     * @return A list of Comment objects.
     */
    List<Comment> getCommentsByPostId(int postId);

    PostDTO toggleCommentLock(Integer postId, User currentUser);

    List<Post> getPostsByUserId(int userId);
}
