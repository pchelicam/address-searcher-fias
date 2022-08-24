package ru.pchelicam.entities.rc64.dto;

public class HouseDTO {

    private Long id;

    private String houseNum;

    private String houseTypeName;

    public HouseDTO(Long id, String houseNum, String houseTypeName) {
        this.id = id;
        this.houseNum = houseNum;
        this.houseTypeName = houseTypeName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
