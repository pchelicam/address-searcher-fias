package ru.pchelicam.addresssearcher.service;

import org.springframework.stereotype.Service;
import ru.pchelicam.addresssearcher.aspect.NoLogging;

import java.io.File;
import java.util.Objects;
import java.util.stream.Stream;

@Service
public class DetectFileNameByMaskService {

    @NoLogging
    public String detectFileNameByObjectName(String pathToFolder, String objectName) {
        return Stream.of(Objects.requireNonNull(new File(pathToFolder).listFiles()))
                .filter(f -> !f.isDirectory())
                .filter(f -> f.getName().contains(objectName))
                .findFirst()
                .map(File::getName)
                .orElse(null);
    }

}
