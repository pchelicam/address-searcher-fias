package ru.pchelicam.addresssearcher.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.pchelicam.addresssearcher.entity.dao.AddressObjects;

import java.util.List;

public interface AddressObjectsRepository extends JpaRepository<AddressObjects, Long> {

    @Query("SELECT ao\n" +
            "FROM AddressObjects ao\n" +
            "WHERE ao.regionCode = :regionCode\n" +
            "AND LOWER(ao.addressObjectName) LIKE LOWER(CONCAT('%', :name,'%'))\n" +
            "AND ao.objectLevel BETWEEN 1 AND 14\n" +
            "ORDER BY ao.objectLevel")
    List<AddressObjects> findLocalityByName(@Param("regionCode") Short regionCode,
                                            @Param("name") String localityName);

    @Query("SELECT ao\n" +
            "FROM AddressObjects ao\n" +
            "JOIN AdmHierarchy ah\n" +
            "ON ao.objectId = ah.objectId\n" +
            "WHERE ao.regionCode = :regionCode\n" +
            "AND ah.parentObjectId = :parentObjectId\n" +
            "AND LOWER(ao.addressObjectName) LIKE LOWER(CONCAT('%', :name,'%'))\n" +
            "ORDER BY ao.addressObjectName")
    List<AddressObjects> findStreetByLocalityIdAndStreetName(@Param("regionCode") Short regionCode,
                                                             @Param("parentObjectId") Long parentObjectId,
                                                             @Param("name") String streetName);

    @Transactional
    void deleteByObjectId(Long objectId);

    boolean existsAddressObjectsByObjectId(Long objectId);

}
