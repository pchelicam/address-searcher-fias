package ru.pchelicam.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.pchelicam.entities.dao.Apartments;

public interface ApartmentsRepository extends JpaRepository<Apartments, Long> {

    @Transactional
    void deleteByObjectId(Long objectId);

    boolean existsApartmentsByObjectId(Long objectId);

}
