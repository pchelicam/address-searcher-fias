package ru.pchelicam.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pchelicam.entities.rc64.dao.AdmHierarchyRc64;

public interface AdmHierarchyRepository extends JpaRepository<AdmHierarchyRc64, Long> {
}
