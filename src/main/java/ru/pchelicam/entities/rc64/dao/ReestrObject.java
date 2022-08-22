package ru.pchelicam.entities.rc64.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "reestr_objects_rc64")
public class ReestrObject {

    @Id
    @Column(name = "object_id")
    private Long objectId;

    @Column(name = "object_guid")
    private String objectGUID;

    public ReestrObject() {
    }

    public ReestrObject(Long objectId, String objectGUID) {
        this.objectId = objectId;
        this.objectGUID = objectGUID;
    }

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    public String getObjectGUID() {
        return objectGUID;
    }

    public void setObjectGUID(String objectGUID) {
        this.objectGUID = objectGUID;
    }

}
