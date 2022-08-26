package ru.pchelicam.controllers;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

//@RestController
public class FlywayController {
//
//    @Autowired
//    Flyway flyway;
//
//    @GetMapping(value = "/createschema")
//    public ResponseEntity<?> foo() {
//        Flyway.configure()
//                // apply/use the default (Spring) flyway configiration
//                .configuration(flyway.getConfiguration())
//                // use the passed schema
////                .schemas(schema)
////                .defaultSchema(schema)
//                // get a Flyway instance
//                .load()
//                // run the migration
//                .migrate();
//        return ResponseEntity.noContent().build();
//    }
}

