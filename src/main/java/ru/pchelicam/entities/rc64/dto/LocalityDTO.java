package ru.pchelicam.entities.rc64.dto;

public class LocalityDTO {

    private Long id;

    private String localityName;

    private String localityType;

    public LocalityDTO(Long id, String localityName, String localityType) {
        this.id = id;
        this.localityName = localityName;
        this.localityType = localityType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLocalityName() {
        return localityName;
    }

    public void setLocalityName(String localityName) {
        this.localityName = localityName;
    }

    public String getLocalityType() {
        return localityType;
    }

    public void setLocalityType(String localityType) {
        this.localityType = localityType;
    }

}
