package ru.pchelicam.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pchelicam.entities.rc64.dao.ReestrObject;

public interface ReestrObjectRepository extends JpaRepository<ReestrObject, Long> {

}
