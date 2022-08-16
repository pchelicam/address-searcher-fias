package ru.pchelicam.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.pchelicam.entities.AdmHierarchy;

public interface AdmHierarchyRepository extends JpaRepository<AdmHierarchy, Long> {
}
