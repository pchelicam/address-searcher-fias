package ru.pchelicam.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.pchelicam.entities.rc64.dao.HousesRc64;

import java.util.List;

public interface HousesRc64Repository extends JpaRepository<HousesRc64, Long> {

//    @Query("SELECT h\n" +
//            "FROM HousesRc64 h\n" +
//            "JOIN AdmHierarchyRc64 ah\n" +
//            "ON h.objectId = ah.objectId\n" +
//            "WHERE ah.parentObjectId = :parentObjectId")
//    List<HousesRc64> findHouseNumsByStreetId(@Param("parentObjectId") Long parentObjectId);

}
