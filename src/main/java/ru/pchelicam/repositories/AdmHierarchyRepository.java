package ru.pchelicam.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.pchelicam.entities.dao.AdmHierarchy;

import java.util.List;
import java.util.Optional;

public interface AdmHierarchyRepository extends JpaRepository<AdmHierarchy, Long> {

    @Query("SELECT DISTINCT ah.objectId\n" +
            "FROM AdmHierarchy ah\n" +
            "WHERE ah.regionCode = :regionCode")
    Slice<Long> findUniqueObjectIds(@Param("regionCode") Short regionCode, Pageable pageable);

    List<AdmHierarchy> findByRegionCodeAndObjectIdOrderByAdmHierarchyEndDateDesc(Short regionCode, Long objectId);

    @Transactional
    void deleteByObjectId(Long objectId);

    Optional<AdmHierarchy> findByObjectId(Long objectId);

    boolean existsAdmHierarchyByObjectId(Long objectId);

}
