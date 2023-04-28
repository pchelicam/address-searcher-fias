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
@Table(name = "addr_objects")
public class AddressObjects {

    @Id
    @Column(name = "addr_obj_id")
    private Long addressObjectId;

    @Column(name = "object_id")
    private Long objectId;

    @Column(name = "addr_obj_name")
    private String addressObjectName;

    @Column(name = "type_name")
    private String typeName;

    @Column(name = "obj_level")
    private Short objectLevel;

    @Column(name = "addr_obj_end_date")
    private Date addressObjectEndDate;

    @Column(name = "region_code")
    private Short regionCode;

}
