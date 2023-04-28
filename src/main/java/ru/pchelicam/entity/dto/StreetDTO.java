package ru.pchelicam.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class StreetDTO {

    private Long id;

    private String streetName;

    private String streetType;

}
