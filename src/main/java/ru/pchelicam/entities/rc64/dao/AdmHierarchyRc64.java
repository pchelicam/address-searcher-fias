package ru.pchelicam.entities.rc64.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "adm_hierarchy_rc64")
public class AdmHierarchyRc64 {

    @Id
    @Column(name = "adm_h_id")
    private Long admHId;

    @Column(name = "object_id")
    private Long objectId;

    @Column(name = "parent_object_id")
    private Long parentObjectId;

    @Column(name = "full_path")
    private String fullPath;

    @Column(name = "region_code")
    private Short regionCode;

}
