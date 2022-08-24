package ru.pchelicam.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.pchelicam.entities.rc64.dao.AddressObjectsRc64;
import ru.pchelicam.entities.rc64.dao.ApartmentsWithApartmentTypeNames;
import ru.pchelicam.entities.rc64.dao.HousesWithHouseTypeNames;
import ru.pchelicam.entities.rc64.dto.ApartmentDTO;
import ru.pchelicam.entities.rc64.dto.HouseDTO;
import ru.pchelicam.entities.rc64.dto.LocalityDTO;
import ru.pchelicam.entities.rc64.dto.StreetDTO;
import ru.pchelicam.repositories.AddressObjectsRc64Repository;
import ru.pchelicam.repositories.ApartmentsWithApartmentTypeNamesRepository;
import ru.pchelicam.repositories.HousesWithHouseTypeNamesRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class AddressSearcherControllerRc64 {

    private final AddressObjectsRc64Repository addressObjectsRc64Repository;
    private final HousesWithHouseTypeNamesRepository housesWithHouseTypeNamesRepository;
    private final ApartmentsWithApartmentTypeNamesRepository apartmentsWithApartmentTypeNamesRepository;

    @Autowired
    public AddressSearcherControllerRc64(AddressObjectsRc64Repository addressObjectsRc64Repository,
                                         HousesWithHouseTypeNamesRepository housesWithHouseTypeNamesRepository,
                                         ApartmentsWithApartmentTypeNamesRepository apartmentsWithApartmentTypeNamesRepository) {
        this.addressObjectsRc64Repository = addressObjectsRc64Repository;
        this.housesWithHouseTypeNamesRepository = housesWithHouseTypeNamesRepository;
        this.apartmentsWithApartmentTypeNamesRepository = apartmentsWithApartmentTypeNamesRepository;
    }

    @GetMapping(value = "/64/locality")
    public List<LocalityDTO> getLocalitiesRc64(@RequestParam(name = "name") String localityName) {
        List<AddressObjectsRc64> addressObjectsRc64s = addressObjectsRc64Repository.findLocalityByName(localityName);
        return addressObjectsRc64s.stream().map(l ->
                        new LocalityDTO(l.getObjectId(), l.getAddressName(), l.getTypeName()))
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/64/street")
    public List<StreetDTO> getStreetsRc64(@RequestParam Long localityId, @RequestParam(name = "name") String streetName) {
        List<AddressObjectsRc64> addressObjectsRc64s = addressObjectsRc64Repository.findStreetByLocalityIdAndStreetName(localityId, streetName);
        return addressObjectsRc64s.stream().map(l ->
                        new StreetDTO(l.getObjectId(), l.getAddressName(), l.getTypeName()))
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/64/house")
    public List<HouseDTO> getHousesRc64(@RequestParam Long streetId) {
        List<HousesWithHouseTypeNames> housesWithHouseTypeNamesList = housesWithHouseTypeNamesRepository.getHousesWithHouseTypeNames(streetId);
        return housesWithHouseTypeNamesList.stream().map(h ->
                        new HouseDTO(h.getObjectId(), h.getObjectGUID(), h.getHouseNum(), h.getHouseTypeName()))
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/64/apartment")
    public List<ApartmentDTO> getApartmentsRc64(@RequestParam Long houseId) {
        List<ApartmentsWithApartmentTypeNames> apartmentsWithApartmentTypeNamesList = apartmentsWithApartmentTypeNamesRepository.getApartmentsWithApartmentTypeNames(houseId);
        return apartmentsWithApartmentTypeNamesList.stream().map(a ->
                        new ApartmentDTO(a.getObjectId(), a.getObjectGUID(), a.getApartmentNumber(), a.getApartmentTypeName()))
                .collect(Collectors.toList());
    }

}
