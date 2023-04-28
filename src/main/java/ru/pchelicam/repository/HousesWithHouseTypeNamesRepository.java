package ru.pchelicam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import ru.pchelicam.entity.dao.HousesWithHouseTypeNames;

import java.util.List;

public interface HousesWithHouseTypeNamesRepository extends JpaRepository<HousesWithHouseTypeNames, Long> {

    List<HousesWithHouseTypeNames> getHousesWithHouseTypeNames(@Param("regionCode") Short regionCode, @Param("parentObjectId") Long parentObjectId);

}
