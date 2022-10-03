package ru.pchelicam.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import ru.pchelicam.entities.dao.AddressObjects;
import ru.pchelicam.repositories.AddressObjectsRepository;
import ru.pchelicam.repositories.AddressSearcherConfigRepository;

import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class XmlParserManagerUpdates {

    private final DataSource dataSource;
    private final AddressSearcherConfigRepository addressSearcherConfigRepository;
    private final AddressObjectsRepository addressObjectsRepository;
    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    public XmlParserManagerUpdates(AddressSearcherConfigRepository addressSearcherConfigRepository, DataSource dataSource,
                                   AddressObjectsRepository addressObjectsRepository) {
        this.addressSearcherConfigRepository = addressSearcherConfigRepository;
        this.dataSource = dataSource;
        this.addressObjectsRepository = addressObjectsRepository;
    }

    public void manageUpdatingData(Short regionCode) throws IOException, SQLException, ParserConfigurationException, SAXException {
        String pathToXmlData = addressSearcherConfigRepository.findByPropertyName("path_to_xml_data_updates").getPropertyValue();

        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();

        XmlParserAddrObjects xmlParserAddrObjects = new XmlParserAddrObjects(
                new ClassPathResource("/database/insert_queries/insert_into_addr_objects.sql").getFile().getAbsolutePath(), regionCode);
        parser.parse(new File(pathToXmlData + "/" + regionCode + "/" + "AS_ADDR_OBJ_20220915_638c30c1-e1c1-4e96-81a1-0120b3f861f2.XML"),
                xmlParserAddrObjects);
    }


    private class XmlParserAddrObjects extends DefaultHandler {

        private Connection connection;
        private PreparedStatement preparedStatement;
        private final String fileName;
        private final Short regionCode;
        private final Map<Long, List<AddressObjects>> addressObjectsUpdates;

        public XmlParserAddrObjects(String fileName, Short regionCode) throws SQLException, IOException {
            this.fileName = fileName;
            this.regionCode = regionCode;
            addressObjectsUpdates = new HashMap<>();
            init();
        }

        private void init() throws SQLException, IOException {
            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement(readFile(fileName).replace("XXX", regionCode.toString()));
        }

        private String readFile(String path) throws IOException {
            byte[] encoded = Files.readAllBytes(Paths.get(path));
            return new String(encoded, StandardCharsets.UTF_8);
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) {
            if (qName.equals("ADDRESSOBJECTS"))
                return;
            String addressObjectId = attributes.getValue("ID");
            String objectId = attributes.getValue("OBJECTID");
            String addressObjectName = attributes.getValue("NAME");
            String typeName = attributes.getValue("TYPENAME");
            String objectLevel = attributes.getValue("LEVEL");
            String addressObjectEndDate = attributes.getValue("ENDDATE");
            if (objectId != null) {
                AddressObjects currentAddressObject = new AddressObjects();
                Long objectIdLongValue = Long.parseLong(objectId);
                currentAddressObject.setAddressObjectId(addressObjectId != null ? Long.parseLong(addressObjectId) : null);
                currentAddressObject.setObjectId(objectIdLongValue);
                currentAddressObject.setAddressObjectName(addressObjectName);
                currentAddressObject.setTypeName(typeName);
                currentAddressObject.setObjectLevel(objectLevel != null ? Short.parseShort(objectLevel) : null);
                currentAddressObject.setRegionCode(regionCode);
                try {
                    currentAddressObject.setAddressObjectEndDate(addressObjectEndDate != null
                            ? new Date(formatter.parse(addressObjectEndDate).getTime())
                            : new Date(0L));
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                List<AddressObjects> currentAddressObjectsList = addressObjectsUpdates.get(Long.parseLong(objectId));
                if (currentAddressObjectsList != null) {
                    currentAddressObjectsList.add(currentAddressObject);
                    addressObjectsUpdates.put(objectIdLongValue, currentAddressObjectsList);
                } else {
                    List<AddressObjects> addressObjectsListToInsert = new ArrayList<>();
                    addressObjectsListToInsert.add(currentAddressObject);
                    addressObjectsUpdates.put(objectIdLongValue, addressObjectsListToInsert);
                }
            }
        }

        @Override
        public void endDocument() {
            List<Long> addressObjectsToDelete = new ArrayList<>(addressObjectsUpdates.keySet());
            addressObjectsToDelete.forEach(XmlParserManagerUpdates.this.addressObjectsRepository::deleteByObjectId);
            addressObjectsUpdates.forEach((key, value) -> {
                AddressObjects addressObject = value
                        .stream().min((ao1, ao2) -> ao2.getAddressObjectEndDate().compareTo(ao1.getAddressObjectEndDate())).orElse(null);
                assert addressObject != null;
                try {
                    if (addressObject.getAddressObjectId() != null) {
                        preparedStatement.setLong(1, addressObject.getAddressObjectId());
                    } else {
                        preparedStatement.setNull(1, Types.BIGINT);
                    }
                    if (addressObject.getObjectId() != null) {
                        preparedStatement.setLong(2, addressObject.getObjectId());
                    } else {
                        preparedStatement.setNull(2, Types.BIGINT);
                    }
                    preparedStatement.setString(3, addressObject.getAddressObjectName());
                    preparedStatement.setString(4, addressObject.getTypeName());
                    if (addressObject.getObjectLevel() != null) {
                        preparedStatement.setShort(5, addressObject.getObjectLevel());
                    } else {
                        preparedStatement.setNull(5, Types.SMALLINT);
                    }
                    if (addressObject.getAddressObjectEndDate() != null) {
                        preparedStatement.setDate(6, addressObject.getAddressObjectEndDate());
                    } else {
                        preparedStatement.setDate(6, new Date(0L));
                    }
                    preparedStatement.setShort(7, regionCode);
                    preparedStatement.executeUpdate();
                    preparedStatement.clearParameters();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            try {
                preparedStatement.close();
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
