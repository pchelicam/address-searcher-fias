package ru.pchelicam.addresssearcher.service;

import org.springframework.stereotype.Service;
import ru.pchelicam.addresssearcher.aspect.NoLogging;
import ru.pchelicam.addresssearcher.entity.dao.AddressObjects;
import ru.pchelicam.addresssearcher.entity.dao.AddressSearcherConfig;
import ru.pchelicam.addresssearcher.entity.dao.ApartmentsWithApartmentTypeNames;
import ru.pchelicam.addresssearcher.entity.dao.HousesWithHouseTypeNames;
import ru.pchelicam.addresssearcher.entity.dto.AddressSearcherConfigDTO;
import ru.pchelicam.addresssearcher.entity.dto.ApartmentDTO;
import ru.pchelicam.addresssearcher.entity.dto.HouseDTO;
import ru.pchelicam.addresssearcher.entity.dto.LocalityDTO;
import ru.pchelicam.addresssearcher.entity.dto.StreetDTO;
import ru.pchelicam.addresssearcher.repository.AddressObjectsRepository;
import ru.pchelicam.addresssearcher.repository.AddressSearcherConfigRepository;
import ru.pchelicam.addresssearcher.repository.ApartmentsWithApartmentTypeNamesRepository;
import ru.pchelicam.addresssearcher.repository.HousesWithHouseTypeNamesRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressSearcherService {

    private final AddressObjectsRepository addressObjectsRepository;
    private final HousesWithHouseTypeNamesRepository housesWithHouseTypeNamesRepository;
    private final ApartmentsWithApartmentTypeNamesRepository apartmentsWithApartmentTypeNamesRepository;
    private final AddressSearcherConfigRepository addressSearcherConfigRepository;

    public AddressSearcherService(AddressObjectsRepository addressObjectsRepository,
                                  HousesWithHouseTypeNamesRepository housesWithHouseTypeNamesRepository,
                                  ApartmentsWithApartmentTypeNamesRepository apartmentsWithApartmentTypeNamesRepository,
                                  AddressSearcherConfigRepository addressSearcherConfigRepository) {
        this.addressObjectsRepository = addressObjectsRepository;
        this.housesWithHouseTypeNamesRepository = housesWithHouseTypeNamesRepository;
        this.apartmentsWithApartmentTypeNamesRepository = apartmentsWithApartmentTypeNamesRepository;
        this.addressSearcherConfigRepository = addressSearcherConfigRepository;
    }

    public List<LocalityDTO> findLocalityByName(Short regionCode, String localityName) {
        List<AddressObjects> addressObjects = addressObjectsRepository.findLocalityByName(regionCode, localityName);
        return addressObjects.stream().map(l ->
                        new LocalityDTO(l.getObjectId(), l.getAddressObjectName(), l.getTypeName()))
                .collect(Collectors.toList());
    }

    public List<StreetDTO> findStreetByLocalityIdAndStreetName(Short regionCode, Long localityId, String streetName) {
        List<AddressObjects> addressObjects = addressObjectsRepository.findStreetByLocalityIdAndStreetName(regionCode, localityId, streetName);
        return addressObjects.stream().map(l ->
                        new StreetDTO(l.getObjectId(), l.getAddressObjectName(), l.getTypeName()))
                .collect(Collectors.toList());
    }

    public List<HouseDTO> getHousesWithHouseTypeNames(Short regionCode, Long streetId) {
        List<HousesWithHouseTypeNames> housesWithHouseTypeNamesList = housesWithHouseTypeNamesRepository.getHousesWithHouseTypeNames(regionCode, streetId);
        return housesWithHouseTypeNamesList.stream().map(h ->
                        new HouseDTO(h.getObjectId(), h.getObjectGUID(), h.getHouseNum(), h.getHouseTypeName()))
                .collect(Collectors.toList());
    }

    public List<ApartmentDTO> getApartmentsWithApartmentTypeNames(Short regionCode, Long houseId) {
        List<ApartmentsWithApartmentTypeNames> apartmentsWithApartmentTypeNamesList = apartmentsWithApartmentTypeNamesRepository
                .getApartmentsWithApartmentTypeNames(regionCode, houseId);
        return apartmentsWithApartmentTypeNamesList.stream().map(a ->
                        new ApartmentDTO(a.getObjectId(), a.getObjectGUID(), a.getApartmentNumber(), a.getApartmentTypeName()))
                .collect(Collectors.toList());
    }

    public List<AddressSearcherConfigDTO> manageInputAddressSearcherConfigDTO(List<AddressSearcherConfigDTO> addressSearcherConfigDTO) {
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
