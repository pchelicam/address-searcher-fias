package ru.pchelicam.entities.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;

@Entity
@Table(name = "adm_hierarchy")
public class AdmHierarchy {

    @Id
    @Column(name = "adm_h_id")
    private Long admHierarchyId;

    @Column(name = "object_id")
    private Long objectId;

    @Column(name = "parent_object_id")
    private Long parentObjectId;

    @Column(name = "full_path")
    private String fullPath;

    @Column(name = "adm_h_end_date")
    private Date admHierarchyEndDate;

    @Column(name = "is_actual")
    private Boolean isActual;

    @Column(name = "region_code")
    private Short regionCode;

    public Long getAdmHierarchyId() {
        return admHierarchyId;
    }

    public void setAdmHierarchyId(Long admHierarchyId) {
        this.admHierarchyId = admHierarchyId;
    }

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    public Long getParentObjectId() {
        return parentObjectId;
    }

    public void setParentObjectId(Long parentObjectId) {
        this.parentObjectId = parentObjectId;
    }

    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    public Date getAdmHierarchyEndDate() {
        return admHierarchyEndDate;
    }

    public void setAdmHierarchyEndDate(Date admHierarchyEndDate) {
        this.admHierarchyEndDate = admHierarchyEndDate;
    }

    public Boolean getActual() {
        return isActual;
    }

    public void setActual(Boolean actual) {
        isActual = actual;
    }

    public Short getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(Short regionCode) {
        this.regionCode = regionCode;
    }

}
