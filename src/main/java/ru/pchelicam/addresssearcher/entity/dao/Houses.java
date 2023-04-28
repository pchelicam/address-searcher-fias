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
@Table(name = "houses")
public class Houses {

    @Id
    @Column(name = "house_id")
    private Long houseId;

    @Column(name = "object_id")
    private Long objectId;

    @Column(name = "house_num")
    private String houseNum;

    @Column(name = "house_type")
    private Integer houseType;

    @Column(name = "house_end_date")
    private Date houseEndDate;

    @Column(name = "region_code")
    private Short regionCode;

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

    public Date getHouseEndDate() {
        return houseEndDate;
    }

    public void setHouseEndDate(Date houseEndDate) {
        this.houseEndDate = houseEndDate;
    }

    public Short getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(Short regionCode) {
        this.regionCode = regionCode;
    }

}
