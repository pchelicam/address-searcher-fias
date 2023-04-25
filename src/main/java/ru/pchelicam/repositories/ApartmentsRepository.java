package ru.pchelicam.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.pchelicam.entities.dao.Apartments;

import java.util.List;
import java.util.Optional;

public interface ApartmentsRepository extends JpaRepository<Apartments, Long> {

    @Query("SELECT DISTINCT ap.objectId\n" +
            "FROM Apartments ap\n" +
            "WHERE ap.regionCode = :regionCode")
    Slice<Long> findUniqueObjectIds(@Param("regionCode") Short regionCode, Pageable pageable);

    List<Apartments> findByRegionCodeAndObjectIdOrderByApartmentEndDateDesc(Short regionCode, Long objectId);

    @Transactional
    void deleteByObjectId(Long objectId);

    Optional<Apartments> findByObjectId(Long objectId);

    boolean existsApartmentsByObjectId(Long objectId);

}
