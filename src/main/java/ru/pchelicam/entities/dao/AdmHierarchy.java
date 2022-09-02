package ru.pchelicam.entities.dao;

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

    @Column(name = "full_path")
    private String fullPath;

    @Column(name = "region_code")
    private Short regionCode;

}
