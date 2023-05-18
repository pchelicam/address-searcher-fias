package ru.pchelicam.addresssearcher.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.pchelicam.addresssearcher.entity.dto.AddressSearcherConfigDTO;
import ru.pchelicam.addresssearcher.service.AddressSearcherService;

import javax.validation.Valid;
import java.util.List;

@RestController
public class ConfigController {

    private final AddressSearcherService addressSearcherService;

    public ConfigController(AddressSearcherService addressSearcherService) {
        this.addressSearcherService = addressSearcherService;
    }

    @PostMapping(value = "/config")
    public ResponseEntity<List<AddressSearcherConfigDTO>> setUpConfiguration(@Valid @RequestBody List<AddressSearcherConfigDTO> addressSearcherConfigDTO) {
        return new ResponseEntity<>(
                addressSearcherService.manageInputAddressSearcherConfigDTO(addressSearcherConfigDTO), HttpStatus.OK);
    }

}
