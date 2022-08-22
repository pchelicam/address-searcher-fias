package ru.pchelicam.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.pchelicam.entities.rc64.dao.AddressObjectsRc64;

import java.util.List;

public interface AddressObjectsRc64Repository extends JpaRepository<AddressObjectsRc64, Long> {

    @Query(" SELECT CONCAT(' ', ao.typeName, ao.addressName)\n" +
            "FROM AddressObjectsRc64 ao\n" +
            "WHERE LOWER(ao.addressName) LIKE LOWER(CONCAT('%', :includeString,'%'))\n" +
            "AND ao.objectLevel IN (5, 6)")
    List<String> findLocalityByIncludeString(@Param("includeString") String includeString);

}
