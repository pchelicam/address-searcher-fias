package ru.pchelicam.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pchelicam.entities.dao.AddressSearcherConfig;

public interface AddressSearcherConfigRepository extends JpaRepository<AddressSearcherConfig, Integer> {

    AddressSearcherConfig findByPropertyName(String propertyName);
    
}
