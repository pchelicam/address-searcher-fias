package ru.pchelicam.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "adm_hierarchy")
public class AdmHierarchy {

    @Id
    @Column(name = "adm_h_id")
    private Long admHId;

    @Column(name = "object_id")
    private Long objectId;

    @Column(name = "parent_object_id")
    private Long parentObjectId;

    @Column(name = "region_code")
    private Short regionCode;

    @Column(name = "full_path")
    private String fullPath;

    public AdmHierarchy() {
    }

    public AdmHierarchy(Long admHId, Long objectId, Long parentObjectId, Short regionCode, String fullPath) {
        this.admHId = admHId;
        this.objectId = objectId;
        this.parentObjectId = parentObjectId;
        this.regionCode = regionCode;
        this.fullPath = fullPath;
    }

    public Long getAdmHId() {
        return admHId;
    }

    public void setAdmHId(Long admHId) {
        this.admHId = admHId;
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

    public Short getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(Short regionCode) {
        this.regionCode = regionCode;
    }

    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

}
