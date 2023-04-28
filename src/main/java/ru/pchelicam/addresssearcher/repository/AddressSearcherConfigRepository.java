package ru.pchelicam.addresssearcher.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pchelicam.addresssearcher.entity.dao.AddressSearcherConfig;

public interface AddressSearcherConfigRepository extends JpaRepository<AddressSearcherConfig, Integer> {

    AddressSearcherConfig findByPropertyName(String propertyName);
    
}
