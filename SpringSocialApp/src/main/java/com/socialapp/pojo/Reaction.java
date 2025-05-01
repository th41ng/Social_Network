/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.socialapp.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author DELL G15
 */
@Entity
@Table(name = "reactions")
@NamedQueries({
    @NamedQuery(name = "Reaction.findAll", query = "SELECT r FROM Reaction r"),
    @NamedQuery(name = "Reaction.findById", query = "SELECT r FROM Reaction r WHERE r.reactionId = :reactionId"),
    @NamedQuery(name = "Reaction.findByType", query = "SELECT r FROM Reaction r WHERE r.reactionType = :reactionType"),
    @NamedQuery(name = "Reaction.findByCreatedAt", query = "SELECT r FROM Reaction r WHERE r.createdAt = :createdAt")
})
public class Reaction implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "reaction_id")
    private Integer reactionId;

    @Basic(optional = false)
    @Column(name = "reaction_type")
    private String reactionType;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @ManyToOne(optional = false)
    @JsonIgnore
    private User userId;

    @JoinColumn(name = "post_id", referencedColumnName = "post_id")
    @ManyToOne(optional = false)
    @JsonIgnore
    private Post postId;

    @JoinColumn(name = "comment_id", referencedColumnName = "comment_id")
    @ManyToOne
    @JsonIgnore
    private Comment commentId;

    public Reaction() {
    }

    public Reaction(Integer reactionId) {
        this.reactionId = reactionId;
    }

    public Reaction(Integer reactionId, String reactionType, Date createdAt, User userId, Post postId) {
        this.reactionId = reactionId;
        this.reactionType = reactionType;
        this.createdAt = createdAt;
        this.userId = userId;
        this.postId = postId;
    }

    // Getters and Setters
    public Integer getReactionId() {
        return reactionId;
    }

    public void setReactionId(Integer reactionId) {
        this.reactionId = reactionId;
    }

    public String getReactionType() {
        return reactionType;
    }

    public void setReactionType(String reactionType) {
        this.reactionType = reactionType;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
    }

    public Post getPostId() {
        return postId;
    }

    public void setPostId(Post postId) {
        this.postId = postId;
    }

    public Comment getCommentId() {
        return commentId;
    }

    public void setCommentId(Comment commentId) {
        this.commentId = commentId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (reactionId != null ? reactionId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Reaction)) {
            return false;
        }
        Reaction other = (Reaction) object;
        return (this.reactionId != null || other.reactionId == null) && (this.reactionId == null || this.reactionId.equals(other.reactionId));
    }

    @Override
    public String toString() {
        return "com.socialapp.pojo.Reaction[ reactionId=" + reactionId + " ]";
    }
}
