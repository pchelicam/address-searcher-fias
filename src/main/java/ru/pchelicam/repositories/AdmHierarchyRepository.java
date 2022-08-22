package ru.pchelicam.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pchelicam.entities.rc64.dao.AdmHierarchy;

public interface AdmHierarchyRepository extends JpaRepository<AdmHierarchy, Long> {
}
