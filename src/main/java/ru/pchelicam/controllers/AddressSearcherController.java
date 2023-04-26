package ru.pchelicam.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;
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
import ru.pchelicam.services.XmlParserManager;
import ru.pchelicam.services.XmlParserManagerUpdates;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
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
    public ResponseEntity<List<LocalityDTO>> getLocalities(@RequestParam(name = "regionCode") Short regionCode, @RequestParam(name = "name") String localityName) {
        List<AddressObjects> addressObjects = addressObjectsRepository.findLocalityByName(regionCode, localityName);
        return new ResponseEntity<>(addressObjects.stream().map(l ->
                        new LocalityDTO(l.getObjectId(), l.getAddressObjectName(), l.getTypeName()))
                .collect(Collectors.toList()), HttpStatus.OK);
    }

    @GetMapping(value = "/street")
    public ResponseEntity<List<StreetDTO>> getStreets(@RequestParam(name = "regionCode") Short regionCode,
                                      @RequestParam Long localityId,
                                      @RequestParam(name = "name") String streetName) {
        List<AddressObjects> addressObjects = addressObjectsRepository.findStreetByLocalityIdAndStreetName(regionCode, localityId, streetName);
        return new ResponseEntity<>(addressObjects.stream().map(l ->
                        new StreetDTO(l.getObjectId(), l.getAddressObjectName(), l.getTypeName()))
                .collect(Collectors.toList()), HttpStatus.OK);
    }

    @GetMapping(value = "/house")
    public ResponseEntity<List<HouseDTO>> getHouses(@RequestParam(name = "regionCode") Short regionCode, @RequestParam Long streetId) {
        List<HousesWithHouseTypeNames> housesWithHouseTypeNamesList = housesWithHouseTypeNamesRepository.getHousesWithHouseTypeNames(regionCode, streetId);
        return new ResponseEntity<>(housesWithHouseTypeNamesList.stream().map(h ->
                        new HouseDTO(h.getObjectId(), h.getObjectGUID(), h.getHouseNum(), h.getHouseTypeName()))
                .collect(Collectors.toList()), HttpStatus.OK);
    }

    @GetMapping(value = "/apartment")
    public ResponseEntity<List<ApartmentDTO>> getApartments(@RequestParam(name = "regionCode") Short regionCode, @RequestParam Long houseId) {
        List<ApartmentsWithApartmentTypeNames> apartmentsWithApartmentTypeNamesList = apartmentsWithApartmentTypeNamesRepository
                .getApartmentsWithApartmentTypeNames(regionCode, houseId);
        return new ResponseEntity<>(apartmentsWithApartmentTypeNamesList.stream().map(a ->
                        new ApartmentDTO(a.getObjectId(), a.getObjectGUID(), a.getApartmentNumber(), a.getApartmentTypeName()))
                .collect(Collectors.toList()), HttpStatus.OK);
    }

}
