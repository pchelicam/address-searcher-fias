package ru.pchelicam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.pchelicam.entity.dao.Houses;

public interface HousesRepository extends JpaRepository<Houses, Long> {

    @Transactional
    void deleteByObjectId(Long objectId);

    boolean existsHousesByObjectId(Long objectId);

}
