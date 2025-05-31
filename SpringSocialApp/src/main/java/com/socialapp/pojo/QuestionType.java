package com.socialapp.pojo; 

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * @author DELL G15 
 */
@Entity
@Table(name = "question_types")
@NamedQueries({
    @NamedQuery(name = "QuestionType.findAll", query = "SELECT qt FROM QuestionType qt"),
    @NamedQuery(name = "QuestionType.findByTypeId", query = "SELECT qt FROM QuestionType qt WHERE qt.typeId = :typeId"),
    @NamedQuery(name = "QuestionType.findByTypeName", query = "SELECT qt FROM QuestionType qt WHERE qt.typeName = :typeName")
})
public class QuestionType implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    @Basic(optional = false)
    @Column(name = "type_id")
    private Integer typeId;

    @Basic(optional = false)
    @Column(name = "type_name", length = 100) 
    private String typeName;

    

    public QuestionType() {
    }

    public QuestionType(Integer typeId) {
        this.typeId = typeId;
    }

    public QuestionType(Integer typeId, String typeName) {
        this.typeId = typeId;
        this.typeName = typeName;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (typeId != null ? typeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof QuestionType)) {
            return false;
        }
        QuestionType other = (QuestionType) object;
        if ((this.typeId == null && other.typeId != null) || (this.typeId != null && !this.typeId.equals(other.typeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.socialapp.pojo.QuestionType[ typeId=" + typeId + ", typeName=" + typeName + " ]";
    }
}
