package ru.pchelicam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.pchelicam.entity.dao.AdmHierarchy;

public interface AdmHierarchyRepository extends JpaRepository<AdmHierarchy, Long> {

    @Transactional
    void deleteByObjectId(Long objectId);

    boolean existsAdmHierarchyByObjectId(Long objectId);

}
