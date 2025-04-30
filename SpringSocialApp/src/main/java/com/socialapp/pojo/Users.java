/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.socialapp.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 *
 * @author DELL G15
 */

@Entity
@Table(name = "users")
@NamedQueries({
    @NamedQuery(name = "User.findAll", query = "SELECT u FROM User u"),
    @NamedQuery(name = "User.findById", query = "SELECT u FROM User u WHERE u.id = :id"),
    @NamedQuery(name = "User.findByEmail", query = "SELECT u FROM User u WHERE u.email = :email"),
    @NamedQuery(name = "User.findByUsername", query = "SELECT u FROM User u WHERE u.username = :username")
})
public class Users implements Serializable {

    private static long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "user_id")
    private Integer id;

    @Basic(optional = false)
    @Column(name = "student_id")
    private String studentId;

    @Basic(optional = false)
    @Column(name = "email")
    private String email;

    @Basic(optional = false)
    @Column(name = "password")
    private String password;

    @Column(name = "role")
    private String role;

    @Column(name = "avatar")
    private String avatar;

    @Column(name = "cover_image")
    private String coverImage;

    @Basic(optional = false)
    @Column(name = "full_name")
    private String fullName;

    @Column(name = "is_verified")
    private Boolean isVerified;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "last_password_change")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastPasswordChange;

    @Column(name = "is_locked")
    private Boolean isLocked;

    @OneToMany(mappedBy = "userId")
    @JsonIgnore
    private Set<Comment> commentSet;

    @OneToMany(mappedBy = "userId")
    @JsonIgnore
    private Set<Post> postSet;
    
    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserGroups> managedGroups; // Quan hệ với UserGroups (admin_id)

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupMembers> groupMemberships; // Quan hệ với GroupMembers (user_id)
    
    public Users() {
    }

   
    public Users(Integer id) {
        this.id = id;
    }

    
    public Users(Integer id, String studentId, String email, String password, String role, String avatar, String coverImage, String fullName, Boolean isVerified, Date createdAt, Date lastPasswordChange, Boolean isLocked) {
        this.id = id;
        this.studentId = studentId;
        this.email = email;
        this.password = password;
        this.role = role;
        this.avatar = avatar;
        this.coverImage = coverImage;
        this.fullName = fullName;
        this.isVerified = isVerified;
        this.createdAt = createdAt;
        this.lastPasswordChange = lastPasswordChange;
        this.isLocked = isLocked;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Boolean getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getLastPasswordChange() {
        return lastPasswordChange;
    }

    public void setLastPasswordChange(Date lastPasswordChange) {
        this.lastPasswordChange = lastPasswordChange;
    }

    public Boolean getIsLocked() {
        return isLocked;
    }

    public void setIsLocked(Boolean isLocked) {
        this.isLocked = isLocked;
    }

    public Set<Comment> getCommentSet() {
        return commentSet;
    }

    public void setCommentSet(Set<Comment> commentSet) {
        this.commentSet = commentSet;
    }

    public Set<Post> getPostSet() {
        return postSet;
    }

    public void setPostSet(Set<Post> postSet) {
        this.postSet = postSet;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Users)) {
            return false;
        }
        Users other = (Users) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return "com.socialapp.pojo.User[ id=" + id + " ]";
    }

    /**
     * @return the serialVersionUID
     */
    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    /**
     * @param aSerialVersionUID the serialVersionUID to set
     */
    public static void setSerialVersionUID(long aSerialVersionUID) {
        serialVersionUID = aSerialVersionUID;
    }

    /**
     * @return the managedGroups
     */
    public List<UserGroups> getManagedGroups() {
        return managedGroups;
    }

    /**
     * @param managedGroups the managedGroups to set
     */
    public void setManagedGroups(List<UserGroups> managedGroups) {
        this.managedGroups = managedGroups;
    }

    /**
     * @return the groupMemberships
     */
    public List<GroupMembers> getGroupMemberships() {
        return groupMemberships;
    }

    /**
     * @param groupMemberships the groupMemberships to set
     */
    public void setGroupMemberships(List<GroupMembers> groupMemberships) {
        this.groupMemberships = groupMemberships;
    }

}
