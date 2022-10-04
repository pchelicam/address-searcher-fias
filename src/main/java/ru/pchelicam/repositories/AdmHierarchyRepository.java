package ru.pchelicam.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.pchelicam.entities.dao.AdmHierarchy;

import java.util.List;

public interface AdmHierarchyRepository extends JpaRepository<AdmHierarchy, Long> {

    @Query("SELECT DISTINCT ah.objectId\n" +
            "FROM AdmHierarchy ao\n" +
            "WHERE ah.regionCode = :regionCode")
    List<Long> findUniqueObjectIds(@Param("regionCode") Short regionCode);

    List<AdmHierarchy> findByRegionCodeAndObjectIdOrderByAdmHierarchyEndDateDesc(Short regionCode, Long objectId);

    @Transactional
    void deleteByObjectId(Long objectId);

}
