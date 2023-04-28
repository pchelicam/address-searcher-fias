package ru.pchelicam.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.pchelicam.entity.dao.AddressSearcherConfig;
import ru.pchelicam.entity.dto.AddressSearcherConfigDTO;
import ru.pchelicam.repository.AddressSearcherConfigRepository;

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

    @PostMapping(value = "/config")
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
