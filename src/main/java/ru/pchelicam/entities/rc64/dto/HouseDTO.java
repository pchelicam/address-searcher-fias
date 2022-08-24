package ru.pchelicam.entities.rc64.dto;

public class HouseDTO {

    private Long id;

    private String houseGUID;

    private String houseNum;

    private String houseTypeName;

    public HouseDTO(Long id, String houseGUID, String houseNum, String houseTypeName) {
        this.id = id;
        this.houseGUID = houseGUID;
        this.houseNum = houseNum;
        this.houseTypeName = houseTypeName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHouseGUID() {
        return houseGUID;
    }

    public void setHouseGUID(String houseGUID) {
        this.houseGUID = houseGUID;
    }

    public String getHouseNum() {
        return houseNum;
    }

    public void setHouseNum(String houseNum) {
        this.houseNum = houseNum;
    }

    public String getHouseTypeName() {
        return houseTypeName;
    }

    public void setHouseTypeName(String houseTypeName) {
        this.houseTypeName = houseTypeName;
    }

}
