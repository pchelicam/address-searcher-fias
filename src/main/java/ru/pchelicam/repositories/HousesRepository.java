package ru.pchelicam.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.pchelicam.entities.dao.Houses;

public interface HousesRepository extends JpaRepository<Houses, Long> {

    @Transactional
    void deleteByObjectId(Long objectId);

    boolean existsHousesByObjectId(Long objectId);

}
