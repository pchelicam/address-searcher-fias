package ru.pchelicam.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import ru.pchelicam.entities.rc64.dao.HousesWithHouseTypeNames;

import java.util.List;

public interface HousesWithHouseTypeNamesRepository extends JpaRepository<HousesWithHouseTypeNames, Long> {

    List<HousesWithHouseTypeNames> getHousesWithHouseTypeNames(@Param("parentObjectId") Long parentObjectId);

}
