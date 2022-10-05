package ru.pchelicam.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.pchelicam.entities.dao.Houses;

import java.util.List;

public interface HousesRepository extends JpaRepository<Houses, Long> {

    @Query("SELECT DISTINCT h.objectId\n" +
            "FROM Houses h\n" +
            "WHERE h.regionCode = :regionCode")
    List<Long> findUniqueObjectIds(@Param("regionCode") Short regionCode);

    List<Houses> findByRegionCodeAndObjectIdOrderByHouseEndDateDesc(Short regionCode, Long objectId);

    @Transactional
    void deleteByObjectId(Long objectId);

}
