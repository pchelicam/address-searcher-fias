package ru.pchelicam.entities.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;

@Entity
@Table(name = "apartments")
public class Apartments {

    @Id
    @Column(name = "apartment_id")
    private Long apartmentId;

    @Column(name = "object_id")
    private Long objectId;

    @Column(name = "apart_type")
    private Integer apartmentType;

    @Column(name = "apart_number")
    private String apartmentNumber;

    @Column(name = "apart_end_date")
    private Date apartmentEndDate;

    @Column(name = "region_code")
    private Short regionCode;

    public Long getApartmentId() {
        return apartmentId;
    }

    public void setApartmentId(Long apartmentId) {
        this.apartmentId = apartmentId;
    }

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    public Integer getApartmentType() {
        return apartmentType;
    }

    public void setApartmentType(Integer apartmentType) {
        this.apartmentType = apartmentType;
    }

    public String getApartmentNumber() {
        return apartmentNumber;
    }

    public void setApartmentNumber(String apartmentNumber) {
        this.apartmentNumber = apartmentNumber;
    }

    public Date getApartmentEndDate() {
        return apartmentEndDate;
    }

    public void setApartmentEndDate(Date apartmentEndDate) {
        this.apartmentEndDate = apartmentEndDate;
    }

    public Short getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(Short regionCode) {
        this.regionCode = regionCode;
    }
}
