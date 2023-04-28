package ru.pchelicam.entity.dao;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;

@Getter
@Setter
@Entity
@Table(name = "apartments")
public class Apartments {

    @Id
    @Column(name = "apartment_id")
    private Long apartmentId;

    @Column(name = "object_id")
    private Long objectId;

    @Column(name = "apart_type")
    private Integer apartmentType;

    @Column(name = "apart_number")
    private String apartmentNumber;

    @Column(name = "apart_end_date")
    private Date apartmentEndDate;

    @Column(name = "is_actual")
    private Boolean isActual;

    @Column(name = "region_code")
    private Short regionCode;

}
