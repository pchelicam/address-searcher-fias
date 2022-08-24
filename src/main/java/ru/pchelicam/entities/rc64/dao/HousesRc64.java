package ru.pchelicam.entities.rc64.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "houses_rc64")
public class HousesRc64 {

    @Id
    @Column(name = "house_id")
    private Long houseId;

    @Column(name = "object_id")
    private Long objectId;

    @Column(name = "house_num")
    private String houseNum;

    @Column(name = "house_type")
    private Integer houseType;

    public Long getHouseId() {
        return houseId;
    }

    public void setHouseId(Long houseId) {
        this.houseId = houseId;
    }

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    public String getHouseNum() {
        return houseNum;
    }

    public void setHouseNum(String houseNum) {
        this.houseNum = houseNum;
    }

    public Integer getHouseType() {
        return houseType;
    }

    public void setHouseType(Integer houseType) {
        this.houseType = houseType;
    }
}
