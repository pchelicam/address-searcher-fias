package ru.pchelicam.entities.rc64.dto;

public class ApartmentDTO {

    private Long id;

    private String apartmentGUID;

    private String apartmentNum;

    private String apartmentTypeName;

    public ApartmentDTO(Long id, String apartmentGUID, String apartmentNum, String apartmentTypeName) {
        this.id = id;
        this.apartmentGUID = apartmentGUID;
        this.apartmentNum = apartmentNum;
        this.apartmentTypeName = apartmentTypeName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApartmentGUID() {
        return apartmentGUID;
    }

    public void setApartmentGUID(String apartmentGUID) {
        this.apartmentGUID = apartmentGUID;
    }

    public String getApartmentNum() {
        return apartmentNum;
    }

    public void setApartmentNum(String apartmentNum) {
        this.apartmentNum = apartmentNum;
    }

    public String getApartmentTypeName() {
        return apartmentTypeName;
    }

    public void setApartmentTypeName(String apartmentTypeName) {
        this.apartmentTypeName = apartmentTypeName;
    }

}
