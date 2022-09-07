package ru.pchelicam.entities.dto;

import javax.validation.constraints.NotNull;

public class AddressSearcherConfigDTO {

    @NotNull
    private String name;

    @NotNull
    private String value;

    public AddressSearcherConfigDTO(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
