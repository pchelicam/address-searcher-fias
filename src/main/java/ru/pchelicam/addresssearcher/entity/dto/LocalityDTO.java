package ru.pchelicam.addresssearcher.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class LocalityDTO {

    private Long id;

    private String localityName;

    private String localityType;

}
