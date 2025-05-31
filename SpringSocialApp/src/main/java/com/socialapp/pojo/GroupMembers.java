package com.socialapp.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "group_members")  
@NamedQueries({
    @NamedQuery(name = "GroupMembers.findAll", query = "SELECT gm FROM GroupMembers gm"),
    @NamedQuery(name = "GroupMembers.findByGroupId", query = "SELECT gm FROM GroupMembers gm WHERE gm.group.groupId = :groupId"),
    @NamedQuery(name = "GroupMembers.findByUserId", query = "SELECT gm FROM GroupMembers gm WHERE gm.user.id = :id"),
    @NamedQuery(name = "GroupMembers.findByGroupAndUserId", query = "SELECT gm FROM GroupMembers gm WHERE gm.group.groupId = :groupId AND gm.user.id = :id"),
    @NamedQuery(name = "GroupMembers.deleteByGroupAndUserId", query = "DELETE FROM GroupMembers gm WHERE gm.group.groupId = :groupId AND gm.user.id = :id")
})
       
public class GroupMembers implements Serializable{
    private static long serialVersionUID = 1L;
    
     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_member_id")
    private Integer groupMemberId;

    @Column(name = "group_id", nullable = false, insertable = false, updatable = false)
    private Integer groupId;

    @Column(name = "user_id", nullable = false, insertable = false, updatable = false)
    private Integer userId;

    @Column(name = "joined_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date joinedAt;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "group_id", referencedColumnName = "group_id", nullable = false)
    private UserGroups group;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user;
    
    public GroupMembers() {
    }

    public GroupMembers(Integer groupMemberId, Integer groupId, Integer userId, Date joinedAt) {
        this.groupMemberId = groupMemberId;
        this.groupId = groupId;
        this.userId = userId;
        this.joinedAt = joinedAt;
    }

 
     @Override
    public int hashCode() {
        int hash = 0;
        hash += (userId != null ? userId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof User)) {
            return false;
        }
        GroupMembers other = (GroupMembers) object;
        if ((this.groupMemberId == null && other.groupMemberId != null) || (this.groupMemberId != null && !this.groupMemberId.equals(other.groupMemberId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.socialapp.pojo.GroupMembers[ userId=" + groupMemberId + " ]";
    }
    
    

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

  
    public static void setSerialVersionUID(long aSerialVersionUID) {
        serialVersionUID = aSerialVersionUID;
    }

  
    public Integer getGroupMemberId() {
        return groupMemberId;
    }

 
    public void setGroupMemberId(Integer groupMemberId) {
        this.groupMemberId = groupMemberId;
    }

    public Integer getGroupId() {
        return groupId;
    }

   
    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

  
    public Integer getUserId() {
        return userId;
    }

   
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Date getJoinedAt() {
        return joinedAt;
    }

   
    public void setJoinedAt(Date joinedAt) {
        this.joinedAt = joinedAt;
    }

 
    public UserGroups getGroup() {
        return group;
    }

  
    public void setGroup(UserGroups group) {
        this.group = group;
    }

  
    public User getUser() {
        return user;
    }


    public void setUser(User user) {
        this.user = user;
    }

}