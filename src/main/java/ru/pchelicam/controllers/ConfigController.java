package ru.pchelicam.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.pchelicam.entities.dao.AddressObjects;
import ru.pchelicam.entities.dao.AddressSearcherConfig;
import ru.pchelicam.entities.dto.AddressSearcherConfigDTO;
import ru.pchelicam.entities.dto.LocalityDTO;
import ru.pchelicam.repositories.AddressSearcherConfigRepository;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ConfigController {

    private final AddressSearcherConfigRepository addressSearcherConfigRepository;

    @Autowired
    public ConfigController(AddressSearcherConfigRepository addressSearcherConfigRepository) {
        this.addressSearcherConfigRepository = addressSearcherConfigRepository;
    }

    @GetMapping(value = "/config")
    public List<AddressSearcherConfigDTO> setUpConfiguration(@Valid @RequestBody List<AddressSearcherConfigDTO> addressSearcherConfigDTO) {
        List<AddressSearcherConfig> addressSearcherConfigList = addressSearcherConfigDTO.stream()
                .map(prop -> new AddressSearcherConfig(prop.getName(), prop.getValue()))
                .collect(Collectors.toList());
        List<AddressSearcherConfig> addressSearcherConfigToSave = new ArrayList<>();
        addressSearcherConfigList.forEach(prop -> {
            AddressSearcherConfig addressSearcherConfig = addressSearcherConfigRepository.findByPropertyName(prop.getPropertyName());
            if (addressSearcherConfig != null) {
                addressSearcherConfig.setPropertyValue(prop.getPropertyValue());
                addressSearcherConfigToSave.add(addressSearcherConfig);
            } else {
                addressSearcherConfigToSave.add(prop);
            }
        });
        return addressSearcherConfigRepository.saveAllAndFlush(addressSearcherConfigToSave).stream()
                .map(prop -> new AddressSearcherConfigDTO(prop.getPropertyName(), prop.getPropertyValue()))
                .collect(Collectors.toList());
    }

}
