package ru.pchelicam.entities.rc64.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "addr_objects_rc64")
public class AddressObjectsRc64 {

    @Id
    @Column(name = "addr_obj_id")
    private Long addressObjectId;

    @Column(name = "object_id")
    private Long objectId;

    @Column(name = "addr_name")
    private String addressName;

    @Column(name = "type_name")
    private String typeName;

    @Column(name = "obj_level")
    private Short objectLevel;

    @Column(name = "region_code")
    private Short regionCode;

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

    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
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

}
