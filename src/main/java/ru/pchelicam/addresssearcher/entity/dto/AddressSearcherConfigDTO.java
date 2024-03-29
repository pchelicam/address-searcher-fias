package ru.pchelicam.addresssearcher.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Getter
@Setter
public class AddressSearcherConfigDTO {

    @NotNull
    private String name;

    @NotNull
    private String value;

}
