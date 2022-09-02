package ru.pchelicam.entities.dto;

public class StreetDTO {

    private Long id;

    private String streetName;

    private String streetType;

    public StreetDTO(Long id, String streetName, String streetType) {
        this.id = id;
        this.streetName = streetName;
        this.streetType = streetType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getStreetType() {
        return streetType;
    }

    public void setStreetType(String streetType) {
        this.streetType = streetType;
    }
}
