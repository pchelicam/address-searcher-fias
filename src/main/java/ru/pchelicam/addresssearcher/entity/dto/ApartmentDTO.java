package ru.pchelicam.addresssearcher.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ApartmentDTO {

    private Long id;

    private String apartmentGUID;

    private String apartmentNum;

    private String apartmentTypeName;

}
