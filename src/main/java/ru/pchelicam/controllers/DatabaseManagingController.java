package ru.pchelicam.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;
import ru.pchelicam.services.XmlParserManager;
import ru.pchelicam.services.XmlParserManagerUpdates;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;

@RestController
public class DatabaseManagingController {

    private final XmlParserManager xmlParserManager;
    private final XmlParserManagerUpdates xmlParserManagerUpdates;

    @Autowired
    public DatabaseManagingController(XmlParserManager xmlParserManager, XmlParserManagerUpdates xmlParserManagerUpdates) {
        this.xmlParserManager = xmlParserManager;
        this.xmlParserManagerUpdates = xmlParserManagerUpdates;
    }

    @GetMapping(value = "/database/import")
    public ResponseEntity<?> importData(@RequestParam(name = "regionCode") Short regionCode) {
        try {
            xmlParserManager.manageDataInsert(regionCode);
        } catch (ParserConfigurationException | SAXException | IOException | SQLException e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(null, HttpStatus.CREATED);
    }

    @GetMapping(value = "/database/reload")
    public ResponseEntity<?> reloadData(@RequestParam(name = "regionCode") Short regionCode) {
        try {
            xmlParserManager.manageReloadingData(regionCode);
        } catch (ParserConfigurationException | SAXException | IOException | SQLException | URISyntaxException e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(null, HttpStatus.CREATED);
    }

    @GetMapping(value = "/database/update")
    public ResponseEntity<?> updateRecords(@RequestParam(name = "regionCode") Short regionCode) {
        try {
            xmlParserManagerUpdates.manageUpdatingData(regionCode);
        } catch (IOException | SQLException | ParserConfigurationException | SAXException e) {
            e.printStackTrace();
            return new ResponseEntity<>("An error occurred while updating. Maybe you should reload the data to database.\n" +
                    "Error message: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(null, HttpStatus.CREATED);
    }

}
