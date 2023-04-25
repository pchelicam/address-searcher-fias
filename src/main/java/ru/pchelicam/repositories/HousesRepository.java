package ru.pchelicam.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.pchelicam.entities.dao.Houses;

import java.util.List;
import java.util.Optional;

public interface HousesRepository extends JpaRepository<Houses, Long> {

    @Query("SELECT DISTINCT h.objectId\n" +
            "FROM Houses h\n" +
            "WHERE h.regionCode = :regionCode")
    Slice<Long> findUniqueObjectIds(@Param("regionCode") Short regionCode, Pageable page);

    List<Houses> findByRegionCodeAndObjectIdOrderByHouseEndDateDesc(Short regionCode, Long objectId);

    @Transactional
    void deleteByObjectId(Long objectId);

    Optional<Houses> findByObjectId(Long objectId);

    boolean existsHousesByObjectId(Long objectId);

}
