/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.socialapp.pojo;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties.Admin;
/**
 *
 * @author DELL G15
 */
@Entity
@Table(name = "surveys")
@NamedQueries({
    @NamedQuery(name = "Survey.findAll", query = "SELECT s FROM Survey s"),
    @NamedQuery(name = "Survey.findById", query = "SELECT s FROM Survey s WHERE s.surveyId = :surveyId"),
    @NamedQuery(name = "Survey.findByTitle", query = "SELECT s FROM Survey s WHERE s.title = :title"),
    @NamedQuery(name = "Survey.findByCreatedAt", query = "SELECT s FROM Survey s WHERE s.createdAt = :createdAt")
})
public class Survey implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "survey_id")
    private Integer surveyId;

    @Basic(optional = false)
    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "is_multiple_choice")
    private Boolean isMultipleChoice;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @JoinColumn(name = "admin_id", referencedColumnName = "admin_id")
    @ManyToOne(optional = false)
    private Admin adminId;

    @JoinColumn(name = "post_id", referencedColumnName = "post_id")
    @ManyToOne(optional = false)
    private Post postId;

    public Survey() {
    }

    public Survey(Integer surveyId) {
        this.surveyId = surveyId;
    }

    public Survey(Integer surveyId, String title, String description, Boolean isMultipleChoice, Date createdAt, Admin adminId, Post postId) {
        this.surveyId = surveyId;
        this.title = title;
        this.description = description;
        this.isMultipleChoice = isMultipleChoice;
        this.createdAt = createdAt;
        this.adminId = adminId;
        this.postId = postId;
    }

    // Getters and Setters
    public Integer getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(Integer surveyId) {
        this.surveyId = surveyId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsMultipleChoice() {
        return isMultipleChoice;
    }

    public void setIsMultipleChoice(Boolean isMultipleChoice) {
        this.isMultipleChoice = isMultipleChoice;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Admin getAdminId() {
        return adminId;
    }

    public void setAdminId(Admin adminId) {
        this.adminId = adminId;
    }

    public Post getPostId() {
        return postId;
    }

    public void setPostId(Post postId) {
        this.postId = postId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (surveyId != null ? surveyId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Survey)) {
            return false;
        }
        Survey other = (Survey) object;
        return (this.surveyId != null || other.surveyId == null) && (this.surveyId == null || this.surveyId.equals(other.surveyId));
    }

    @Override
    public String toString() {
        return "com.socialapp.pojo.Survey[ surveyId=" + surveyId + " ]";
    }
}