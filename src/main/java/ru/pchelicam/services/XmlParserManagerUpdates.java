package ru.pchelicam.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import ru.pchelicam.entities.dao.AddressObjects;
import ru.pchelicam.entities.dao.AdmHierarchy;
import ru.pchelicam.repositories.AddressObjectsRepository;
import ru.pchelicam.repositories.AddressSearcherConfigRepository;
import ru.pchelicam.repositories.AdmHierarchyRepository;

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
    private final AdmHierarchyRepository admHierarchyRepository;
    private final AddressObjectsRepository addressObjectsRepository;
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    public XmlParserManagerUpdates(AddressSearcherConfigRepository addressSearcherConfigRepository, DataSource dataSource,
                                   AdmHierarchyRepository admHierarchyRepository, AddressObjectsRepository addressObjectsRepository) {
        this.addressSearcherConfigRepository = addressSearcherConfigRepository;
        this.dataSource = dataSource;
        this.admHierarchyRepository = admHierarchyRepository;
        this.addressObjectsRepository = addressObjectsRepository;
    }

    public void manageUpdatingData(Short regionCode) throws IOException, SQLException, ParserConfigurationException, SAXException {
        String pathToXmlData = addressSearcherConfigRepository.findByPropertyName("path_to_xml_data_updates").getPropertyValue();

        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();

        XmlParserAdmHierarchy xmlParserAdmHierarchy = new XmlParserAdmHierarchy (
                new ClassPathResource("/database/insert_queries/insert_into_adm_hierarchy.sql").getFile().getAbsolutePath(), regionCode);
        parser.parse(new File(pathToXmlData + "/" + regionCode + "/" + "AS_ADM_HIERARCHY_20220915_6c98192e-239e-4d50-921e-709cbeded838.XML"),
                xmlParserAdmHierarchy);

        XmlParserAddrObjects xmlParserAddrObjects = new XmlParserAddrObjects(
                new ClassPathResource("/database/insert_queries/insert_into_addr_objects.sql").getFile().getAbsolutePath(), regionCode);
        parser.parse(new File(pathToXmlData + "/" + regionCode + "/" + "AS_ADDR_OBJ_20220915_638c30c1-e1c1-4e96-81a1-0120b3f861f2.XML"),
                xmlParserAddrObjects);
    }


    private class XmlParserAdmHierarchy extends DefaultHandler {

        private Connection connection;
        private PreparedStatement preparedStatement;
        private final String fileName;
        private final Short regionCode;
        private final Map<Long, List<AdmHierarchy>> admHierarchyUpdates;

        public XmlParserAdmHierarchy(String fileName, Short regionCode) throws SQLException, IOException {
            this.fileName = fileName;
            this.regionCode = regionCode;
            admHierarchyUpdates = new HashMap<>();
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
            if (qName.equals("ITEMS"))
                return;
            String admHierarchyId = attributes.getValue("ID");
            String objectId = attributes.getValue("OBJECTID");
            String parentObjectId = attributes.getValue("PARENTOBJID");
            String fullPath = attributes.getValue("PATH");
            String admHierarchyEndDate = attributes.getValue("ENDDATE");

            if (objectId != null) {
                AdmHierarchy currentAdmHierarchy = new AdmHierarchy();
                Long objectIdLongValue = Long.parseLong(objectId);
                currentAdmHierarchy.setAdmHierarchyId(admHierarchyId != null ? Long.parseLong(admHierarchyId) : null);
                currentAdmHierarchy.setObjectId(objectIdLongValue);
                currentAdmHierarchy.setParentObjectId(parentObjectId != null ? Long.parseLong(parentObjectId) : null);
                currentAdmHierarchy.setFullPath(fullPath);
                try {
                    currentAdmHierarchy.setAdmHierarchyEndDate(admHierarchyEndDate != null
                            ? new Date(simpleDateFormat.parse(admHierarchyEndDate).getTime())
                            : new Date(0L));
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                currentAdmHierarchy.setRegionCode(regionCode);

                List<AdmHierarchy> currentAdmHierarchyList = admHierarchyUpdates.get(objectIdLongValue);
                if (currentAdmHierarchyList != null) {
                    currentAdmHierarchyList.add(currentAdmHierarchy);
                    admHierarchyUpdates.put(objectIdLongValue, currentAdmHierarchyList);
                } else {
                    List<AdmHierarchy> admHierarchyListToInsert = new ArrayList<>();
                    admHierarchyListToInsert.add(currentAdmHierarchy);
                    admHierarchyUpdates.put(objectIdLongValue, admHierarchyListToInsert);
                }
            }

        }

        @Override
        public void endDocument() {
            List<Long> admHierarchyToDelete = new ArrayList<>(admHierarchyUpdates.keySet());
            admHierarchyToDelete.forEach(admHierarchyRepository::deleteByObjectId);
            admHierarchyUpdates.forEach((key, value) -> {
                AdmHierarchy admHierarchy = value
                        .stream().min((ah1, ah2) -> ah2.getAdmHierarchyEndDate().compareTo(ah1.getAdmHierarchyEndDate())).orElse(null);
                assert admHierarchy != null;
                try {
                    if (admHierarchy.getAdmHierarchyId() != null) {
                        preparedStatement.setLong(1, admHierarchy.getAdmHierarchyId());
                    } else {
                        preparedStatement.setNull(1, Types.BIGINT);
                    }
                    if (admHierarchy.getObjectId() != null) {
                        preparedStatement.setLong(2, admHierarchy.getObjectId());
                    } else {
                        preparedStatement.setNull(2, Types.BIGINT);
                    }
                    if (admHierarchy.getParentObjectId() != null) {
                        preparedStatement.setLong(3, admHierarchy.getParentObjectId());
                    } else {
                        preparedStatement.setNull(3, Types.BIGINT);
                    }
                    preparedStatement.setString(4, admHierarchy.getFullPath());
                    if (admHierarchy.getAdmHierarchyEndDate() != null) {
                        preparedStatement.setDate(5, admHierarchy.getAdmHierarchyEndDate());
                    } else {
                        preparedStatement.setDate(5, new Date(0L));
                    }
                    preparedStatement.setShort(6, regionCode);
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
                try {
                    currentAddressObject.setAddressObjectEndDate(addressObjectEndDate != null
                            ? new Date(simpleDateFormat.parse(addressObjectEndDate).getTime())
                            : new Date(0L));
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                currentAddressObject.setRegionCode(regionCode);

                List<AddressObjects> currentAddressObjectsList = addressObjectsUpdates.get(objectIdLongValue);
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
            addressObjectsToDelete.forEach(addressObjectsRepository::deleteByObjectId);
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

    private class XmlParserHouses extends DefaultHandler {

        private Connection connection;
        private PreparedStatement preparedStatement;
        private int amountOfBatches;
        private final String fileName;
        private final Short regionCode;

        public XmlParserHouses(String fileName, Short regionCode) throws SQLException, IOException {
            this.fileName = fileName;
            this.regionCode = regionCode;
            init();
        }

        private void init() throws SQLException, IOException {
            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement(readFile(fileName).replace("XXX", regionCode.toString()));
            amountOfBatches = 0;
        }

        private String readFile(String path) throws IOException {
            byte[] encoded = Files.readAllBytes(Paths.get(path));
            return new String(encoded, StandardCharsets.UTF_8);
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) {
            if (qName.equals("HOUSES"))
                return;
            String houseId = attributes.getValue("ID");
            String objectId = attributes.getValue("OBJECTID");
            String houseNum = attributes.getValue("HOUSENUM");
            String houseType = attributes.getValue("HOUSETYPE");
            try {
                if (houseId != null) {
                    preparedStatement.setLong(1, Long.parseLong(houseId));
                } else {
                    preparedStatement.setNull(1, Types.BIGINT);
                }
                if (objectId != null) {
                    preparedStatement.setLong(2, Long.parseLong(objectId));
                } else {
                    preparedStatement.setNull(2, Types.BIGINT);
                }
                preparedStatement.setString(3, houseNum);
                if (houseType != null) {
                    preparedStatement.setInt(4, Integer.parseInt(houseType));
                } else {
                    preparedStatement.setNull(4, Types.INTEGER);
                }
                preparedStatement.setShort(5, regionCode);
                if (amountOfBatches == 80) {
                    preparedStatement.executeBatch();
                    preparedStatement.clearBatch();
                    amountOfBatches = 0;
                }
                preparedStatement.addBatch();
                preparedStatement.clearParameters();
                amountOfBatches++;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void endDocument() {
            try {
                preparedStatement.executeBatch();
                preparedStatement.close();
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

    }

}
