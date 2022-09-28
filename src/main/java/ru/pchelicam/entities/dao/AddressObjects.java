package ru.pchelicam.entities.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;

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

    @Column(name = "region_code")
    private Short regionCode;

    @Column(name = "addr_obj_update_date")
    private Date addressObjectUpdateDate;

    public Long getAddressObjectId() {
        return addressObjectId;
    }

    public void setAddressObjectId(Long addressObjectId) {
        this.addressObjectId = addressObjectId;
    }

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    public String getAddressObjectName() {
        return addressObjectName;
    }

    public void setAddressObjectName(String addressObjectName) {
        this.addressObjectName = addressObjectName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Short getObjectLevel() {
        return objectLevel;
    }

    public void setObjectLevel(Short objectLevel) {
        this.objectLevel = objectLevel;
    }

    public Short getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(Short regionCode) {
        this.regionCode = regionCode;
    }

    public Date getAddressObjectUpdateDate() {
        return addressObjectUpdateDate;
    }

    public void setAddressObjectUpdateDate(Date addressObjectUpdateDate) {
        this.addressObjectUpdateDate = addressObjectUpdateDate;
    }

}
