package ru.pchelicam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import ru.pchelicam.entity.dao.ApartmentsWithApartmentTypeNames;

import java.util.List;

public interface ApartmentsWithApartmentTypeNamesRepository extends JpaRepository<ApartmentsWithApartmentTypeNames, Long> {

    List<ApartmentsWithApartmentTypeNames> getApartmentsWithApartmentTypeNames(@Param("regionCode") Short regionCode,
                                                                               @Param("parentObjectId") Long parentObjectId);

}
