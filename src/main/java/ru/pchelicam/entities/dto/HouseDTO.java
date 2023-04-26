package ru.pchelicam.entities.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class HouseDTO {

    private Long id;

    private String houseGUID;

    private String houseNum;

    private String houseTypeName;

}
