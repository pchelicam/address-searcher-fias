package ru.pchelicam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pchelicam.entity.dao.AddressSearcherConfig;

public interface AddressSearcherConfigRepository extends JpaRepository<AddressSearcherConfig, Integer> {

    AddressSearcherConfig findByPropertyName(String propertyName);
    
}
