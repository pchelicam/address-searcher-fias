package ru.pchelicam.addresssearcher.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.pchelicam.addresssearcher.entity.dto.ApartmentDTO;
import ru.pchelicam.addresssearcher.entity.dto.HouseDTO;
import ru.pchelicam.addresssearcher.entity.dto.LocalityDTO;
import ru.pchelicam.addresssearcher.entity.dto.StreetDTO;
import ru.pchelicam.addresssearcher.service.AddressSearcherService;

import java.util.List;

@RestController
public class AddressSearcherController {

    private final AddressSearcherService addressSearcherService;

    public AddressSearcherController(AddressSearcherService addressSearcherService) {
        this.addressSearcherService = addressSearcherService;
    }

    @GetMapping(value = "/locality")
    public ResponseEntity<List<LocalityDTO>> getLocalities(@RequestParam(name = "regionCode") Short regionCode,
                                                           @RequestParam(name = "name") String localityName) {
        return new ResponseEntity<>(
                addressSearcherService.findLocalityByName(regionCode, localityName), HttpStatus.OK);
    }

    @GetMapping(value = "/street")
    public ResponseEntity<List<StreetDTO>> getStreets(@RequestParam(name = "regionCode") Short regionCode,
                                                      @RequestParam Long localityId,
                                                      @RequestParam(name = "name") String streetName) {
        return new ResponseEntity<>(
                addressSearcherService.findStreetByLocalityIdAndStreetName(regionCode, localityId, streetName), HttpStatus.OK);
    }

    @GetMapping(value = "/house")
    public ResponseEntity<List<HouseDTO>> getHouses(@RequestParam(name = "regionCode") Short regionCode, @RequestParam Long streetId) {
        return new ResponseEntity<>(
                addressSearcherService.getHousesWithHouseTypeNames(regionCode, streetId), HttpStatus.OK);
    }

    @GetMapping(value = "/apartment")
    public ResponseEntity<List<ApartmentDTO>> getApartments(@RequestParam(name = "regionCode") Short regionCode, @RequestParam Long houseId) {
        return new ResponseEntity<>(
                addressSearcherService.getApartmentsWithApartmentTypeNames(regionCode, houseId), HttpStatus.OK);
    }

}
