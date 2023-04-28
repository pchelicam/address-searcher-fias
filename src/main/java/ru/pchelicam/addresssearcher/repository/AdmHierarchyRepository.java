package ru.pchelicam.addresssearcher.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.pchelicam.addresssearcher.entity.dao.AdmHierarchy;

public interface AdmHierarchyRepository extends JpaRepository<AdmHierarchy, Long> {

    @Transactional
    void deleteByObjectId(Long objectId);

    boolean existsAdmHierarchyByObjectId(Long objectId);

}
