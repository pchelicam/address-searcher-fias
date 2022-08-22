package ru.pchelicam.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.pchelicam.repositories.AddressObjectsRc64Repository;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@RestController
public class AddressSearcherControllerRc64 {

    private final AddressObjectsRc64Repository addressObjectsRc64Repository;

    @Autowired
    public AddressSearcherControllerRc64(AddressObjectsRc64Repository addressObjectsRc64Repository) {
        this.addressObjectsRc64Repository = addressObjectsRc64Repository;
    }

    @GetMapping(value = "/addr")
    public List<String> getResult (@RequestParam String includeString) throws UnsupportedEncodingException {
        //String decodedWord = URLDecoder.decode(includeString, StandardCharsets.UTF_8.toString());
        return addressObjectsRc64Repository.findLocalityByIncludeString(includeString);
//        List<String> l = new ArrayList<>();
//        l.add(includeString);
//        return l;
    }

}
