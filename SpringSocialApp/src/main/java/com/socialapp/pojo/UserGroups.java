package com.socialapp.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "user_groups")
@NamedQueries({
    @NamedQuery(name = "UserGroups.findAll", query = "SELECT g FROM UserGroups g"),
    @NamedQuery(name = "UserGroups.findById", query = "SELECT g FROM UserGroups g WHERE g.groupId = :groupId"),
    @NamedQuery(name = "UserGroups.findByName", query = "SELECT g FROM UserGroups g WHERE g.groupName LIKE :groupName"),
    @NamedQuery(name = "UserGroups.findByAdminId", query = "SELECT g FROM UserGroups g WHERE g.adminId = :adminId"),
    @NamedQuery(name = "UserGroups.deleteById", query = "DELETE FROM UserGroups g WHERE g.groupId = :groupId"),
    @NamedQuery(name = "UserGroups.findByIdWithMembers", query = "SELECT g FROM UserGroups g LEFT JOIN FETCH g.members WHERE g.groupId = :groupId")
})
public class UserGroups implements Serializable {

    private static long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private Integer groupId;

    @Column(name = "admin_id", nullable = false)
    private Integer adminId;

    @Column(name = "group_name", length = 255, nullable = false)
    private String groupName;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    // Constructor mặc định
    public UserGroups() {
    }

    // Constructor đầy đủ tham số
    public UserGroups(Integer groupId, Integer adminId, String groupName, Date createdAt) {
        this.groupId = groupId;
        this.adminId = adminId;
        this.groupName = groupName;
        this.createdAt = createdAt;
    }

    // Quan hệ với User (admin_id)
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "admin_id", referencedColumnName = "user_id", insertable = false, updatable = false, nullable = false)
    private User admin;

    // Quan hệ với GroupMembers (group_id)
//    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<GroupMembers> members;
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<GroupMembers> members = new ArrayList<>(); // Khởi tạo danh sách

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (groupId != null ? groupId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof UserGroups)) {
            return false;
        }
        UserGroups other = (UserGroups) object;
        if ((this.groupId == null && other.groupId != null) || (this.groupId != null && !this.groupId.equals(other.groupId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.socialapp.pojo.UserGroups[ groupId=" + groupId + " ]";
    }

    // Getter and Setter methods
    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public static void setSerialVersionUID(long aSerialVersionUID) {
        serialVersionUID = aSerialVersionUID;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public User getAdmin() {
        return admin;
    }

    public void setAdmin(User admin) {
        this.admin = admin;
    }

    public List<GroupMembers> getMembers() {
        return members;
    }

    public void setMembers(List<GroupMembers> members) {
        this.members = members;
    }
}
