package ru.pchelicam.addresssearcher.entity.dao;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;

@Getter
@Setter
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

}
