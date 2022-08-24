package ru.pchelicam.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import ru.pchelicam.entities.rc64.dao.ApartmentsWithApartmentTypeNames;

import java.util.List;

public interface ApartmentsWithApartmentTypeNamesRepository extends JpaRepository<ApartmentsWithApartmentTypeNames, Long> {

    List<ApartmentsWithApartmentTypeNames> getApartmentsWithApartmentTypeNames(@Param("parentObjectId") Long parentObjectId);

}
