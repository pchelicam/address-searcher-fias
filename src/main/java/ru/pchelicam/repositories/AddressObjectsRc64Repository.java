package ru.pchelicam.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.pchelicam.entities.rc64.dao.AddressObjectsRc64;

import java.util.List;

public interface AddressObjectsRc64Repository extends JpaRepository<AddressObjectsRc64, Long> {

    @Query("SELECT ao\n" +
            "FROM AddressObjectsRc64 ao\n" +
            "WHERE LOWER(ao.addressName) LIKE LOWER(CONCAT('%', :name,'%'))\n" +
            "AND ao.objectLevel BETWEEN 1 AND 14\n" +
            "ORDER BY ao.objectLevel")
    List<AddressObjectsRc64> findLocalityByName(@Param("name") String localityName);

    @Query("SELECT ao\n" +
            "FROM AddressObjectsRc64 ao\n" +
            "JOIN AdmHierarchyRc64 ah\n" +
            "ON ao.objectId = ah.objectId\n" +
            "WHERE ah.parentObjectId = :parentObjectId\n" +
            "AND LOWER(ao.addressName) LIKE LOWER(CONCAT('%', :name,'%'))\n" +
            "ORDER BY ao.addressName")
    List<AddressObjectsRc64> findStreetByLocalityIdAndStreetName(@Param("parentObjectId") Long parentObjectId, @Param("name") String streetName);

}
