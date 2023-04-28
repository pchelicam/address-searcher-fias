package ru.pchelicam.addresssearcher.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import ru.pchelicam.addresssearcher.entity.dao.HousesWithHouseTypeNames;

import java.util.List;

public interface HousesWithHouseTypeNamesRepository extends JpaRepository<HousesWithHouseTypeNames, Long> {

    List<HousesWithHouseTypeNames> getHousesWithHouseTypeNames(@Param("regionCode") Short regionCode, @Param("parentObjectId") Long parentObjectId);

}
