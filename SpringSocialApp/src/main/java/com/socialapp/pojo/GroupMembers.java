/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.socialapp.pojo;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;
/**
 *
 * @author DELL G15
 */
@Entity
@Table(name = "group_members")
public class GroupMembers implements Serializable{
    private static long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_member_id")
    private Integer groupMemberId;

    @Column(name = "group_id", nullable = false)
    private Integer groupId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "joined_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date joinedAt;
    
    //Quan hệ
    @ManyToOne
    @JoinColumn(name = "group_id", referencedColumnName = "group_id", nullable = false)
    private UserGroups group; // Quan hệ với UserGroups (group_id)

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private Users user; // Quan hệ với Users (user_id)
    
    // Constructor mặc định
    public GroupMembers() {
    }

    // Constructor đầy đủ tham số
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
        if (!(object instanceof Users)) {
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
     * @return the groupMemberId
     */
    public Integer getGroupMemberId() {
        return groupMemberId;
    }

    /**
     * @param groupMemberId the groupMemberId to set
     */
    public void setGroupMemberId(Integer groupMemberId) {
        this.groupMemberId = groupMemberId;
    }

    /**
     * @return the groupId
     */
    public Integer getGroupId() {
        return groupId;
    }

    /**
     * @param groupId the groupId to set
     */
    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    /**
     * @return the userId
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * @return the joinedAt
     */
    public Date getJoinedAt() {
        return joinedAt;
    }

    /**
     * @param joinedAt the joinedAt to set
     */
    public void setJoinedAt(Date joinedAt) {
        this.joinedAt = joinedAt;
    }

    /**
     * @return the group
     */
    public UserGroups getGroup() {
        return group;
    }

    /**
     * @param group the group to set
     */
    public void setGroup(UserGroups group) {
        this.group = group;
    }

    /**
     * @return the user
     */
    public Users getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(Users user) {
        this.user = user;
    }

    
    
    
    
    
    
}
