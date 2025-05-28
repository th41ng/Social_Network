package com.socialapp.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.socialapp.configs.UserRole;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@NamedQueries({
    @NamedQuery(name = "User.findAll", query = "SELECT u FROM User u"),
    @NamedQuery(name = "User.findById", query = "SELECT u FROM User u WHERE u.id = :id"),
    @NamedQuery(name = "User.findByEmail", query = "SELECT u FROM User u WHERE u.email = :email"),
    @NamedQuery(name = "User.findByUsername", query = "SELECT u FROM User u WHERE u.username = :username"),})
public class User implements Serializable {

    private static long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "user_id")
    private Integer id;

    @Column(name = "username")
    private String username;

    @Basic(optional = true)
    @Column(name = "student_id", nullable = true)
    private String studentId;

    @Basic(optional = false)
    @Column(name = "email")
    private String email;

    @Basic(optional = false)
    @Column(name = "password")
    private String password;

    @Enumerated(EnumType.STRING) // Lưu giá trị Enum dưới dạng chuỗi
    @Column(name = "role", nullable = false)
    private UserRole role;

    @Column(name = "avatar")
    private String avatar;

    @Column(name = "cover_image")
    private String coverImage;

    @Basic(optional = false)
    @Column(name = "full_name")
    private String fullName;

    @Column(name = "is_verified")
    private Boolean isVerified = false;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

    @Column(name = "last_password_change")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastPasswordChange;

    @Column(name = "is_locked")
    private Boolean isLocked = false;

    // Quan hệ với Comment
    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<Comment> commentSet;

    // Quan hệ với Post
    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<Post> postSet;

    // Quan hệ với UserGroups (admin)
    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<UserGroups> managedGroups;

    // Quan hệ với GroupMembers (user)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<GroupMembers> groupMemberships;

    public User() {
    }

    public User(Integer id) {
        this.id = id;
    }

    public User(Integer id, String username, String studentId, String email, String password, UserRole role, String avatar, String coverImage, String fullName, Boolean isVerified, Date createdAt, Date lastPasswordChange, Boolean isLocked) {
        this.id = id;
        this.username = username;
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

    // Getter và Setter
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

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
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

    public List<UserGroups> getManagedGroups() {
        return managedGroups;
    }

    public void setManagedGroups(List<UserGroups> managedGroups) {
        this.managedGroups = managedGroups;
    }

    public List<GroupMembers> getGroupMemberships() {
        return groupMemberships;
    }

    public void setGroupMemberships(List<GroupMembers> groupMemberships) {
        this.groupMemberships = groupMemberships;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof User)) {
            return false;
        }
        User other = (User) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return "com.socialapp.pojo.User[ id=" + id + " ]";
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }
}
