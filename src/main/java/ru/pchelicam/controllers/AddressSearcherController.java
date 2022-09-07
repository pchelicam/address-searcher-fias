package ru.pchelicam.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.pchelicam.entities.dao.AddressObjects;
import ru.pchelicam.entities.dao.ApartmentsWithApartmentTypeNames;
import ru.pchelicam.entities.dao.HousesWithHouseTypeNames;
import ru.pchelicam.entities.dto.ApartmentDTO;
import ru.pchelicam.entities.dto.HouseDTO;
import ru.pchelicam.entities.dto.LocalityDTO;
import ru.pchelicam.entities.dto.StreetDTO;
import ru.pchelicam.repositories.AddressObjectsRepository;
import ru.pchelicam.repositories.ApartmentsWithApartmentTypeNamesRepository;
import ru.pchelicam.repositories.HousesWithHouseTypeNamesRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class AddressSearcherController {

    private final AddressObjectsRepository addressObjectsRepository;
    private final HousesWithHouseTypeNamesRepository housesWithHouseTypeNamesRepository;
    private final ApartmentsWithApartmentTypeNamesRepository apartmentsWithApartmentTypeNamesRepository;

    @Autowired
    public AddressSearcherController(AddressObjectsRepository addressObjectsRepository,
                                     HousesWithHouseTypeNamesRepository housesWithHouseTypeNamesRepository,
                                     ApartmentsWithApartmentTypeNamesRepository apartmentsWithApartmentTypeNamesRepository) {
        this.addressObjectsRepository = addressObjectsRepository;
        this.housesWithHouseTypeNamesRepository = housesWithHouseTypeNamesRepository;
        this.apartmentsWithApartmentTypeNamesRepository = apartmentsWithApartmentTypeNamesRepository;
    }

    @GetMapping(value = "/locality")
    public List<LocalityDTO> getLocalities(@RequestParam(name = "regionCode") Short regionCode, @RequestParam(name = "name") String localityName) {
        List<AddressObjects> addressObjects = addressObjectsRepository.findLocalityByName(regionCode, localityName);
        return addressObjects.stream().map(l ->
                        new LocalityDTO(l.getObjectId(), l.getAddressName(), l.getTypeName()))
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/street")
    public List<StreetDTO> getStreets(    @RequestParam(name = "regionCode") Short regionCode,
                                          @RequestParam Long localityId,
                                          @RequestParam(name = "name") String streetName) {
        List<AddressObjects> addressObjects = addressObjectsRepository.findStreetByLocalityIdAndStreetName(regionCode, localityId, streetName);
        return addressObjects.stream().map(l ->
                        new StreetDTO(l.getObjectId(), l.getAddressName(), l.getTypeName()))
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/house")
    public List<HouseDTO> getHouses(@RequestParam(name = "regionCode") Short regionCode, @RequestParam Long streetId) {
        List<HousesWithHouseTypeNames> housesWithHouseTypeNamesList = housesWithHouseTypeNamesRepository.getHousesWithHouseTypeNames(regionCode, streetId);
        return housesWithHouseTypeNamesList.stream().map(h ->
                        new HouseDTO(h.getObjectId(), h.getObjectGUID(), h.getHouseNum(), h.getHouseTypeName()))
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/apartment")
    public List<ApartmentDTO> getApartments(@RequestParam(name = "regionCode") Short regionCode, @RequestParam Long houseId) {
        List<ApartmentsWithApartmentTypeNames> apartmentsWithApartmentTypeNamesList = apartmentsWithApartmentTypeNamesRepository
                .getApartmentsWithApartmentTypeNames(regionCode, houseId);
        return apartmentsWithApartmentTypeNamesList.stream().map(a ->
                        new ApartmentDTO(a.getObjectId(), a.getObjectGUID(), a.getApartmentNumber(), a.getApartmentTypeName()))
                .collect(Collectors.toList());
    }

//    @GetMapping(value = "/createdb")
//    public ResponseEntity<?> dropDBAndCreateNewDB() {
//        Flyway.configure()
//                .configuration(flyway.getConfiguration())
//                .load()
//                .migrate();
//        return ResponseEntity.noContent().build();
//    }

}
