package ru.pchelicam.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import ru.pchelicam.entities.dao.AddressObjects;
import ru.pchelicam.entities.dao.AdmHierarchy;
import ru.pchelicam.entities.dao.Apartments;
import ru.pchelicam.entities.dao.Houses;
import ru.pchelicam.repositories.AddressObjectsRepository;
import ru.pchelicam.repositories.AddressSearcherConfigRepository;
import ru.pchelicam.repositories.AdmHierarchyRepository;
import ru.pchelicam.repositories.ApartmentsRepository;
import ru.pchelicam.repositories.HousesRepository;

import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
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
    private final HousesRepository housesRepository;
    private final ApartmentsRepository apartmentsRepository;
    private final DetectFileNameByMaskService detectFileNameByMaskService;
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    public XmlParserManagerUpdates(DataSource dataSource, AddressSearcherConfigRepository addressSearcherConfigRepository,
                                   AdmHierarchyRepository admHierarchyRepository, AddressObjectsRepository addressObjectsRepository,
                                   HousesRepository housesRepository, ApartmentsRepository apartmentsRepository,
                                   DetectFileNameByMaskService detectFileNameByMaskService) {
        this.dataSource = dataSource;
        this.addressSearcherConfigRepository = addressSearcherConfigRepository;
        this.admHierarchyRepository = admHierarchyRepository;
        this.addressObjectsRepository = addressObjectsRepository;
        this.housesRepository = housesRepository;
        this.apartmentsRepository = apartmentsRepository;
        this.detectFileNameByMaskService = detectFileNameByMaskService;
    }

    public void manageUpdatingData(Short regionCode) throws IOException, SQLException, ParserConfigurationException, SAXException {
        String pathToXmlData = addressSearcherConfigRepository.findByPropertyName("path_to_xml_data_updates").getPropertyValue();

        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();

        XmlParserAdmHierarchy xmlParserAdmHierarchy = new XmlParserAdmHierarchy(
                new ClassPathResource("/database/insert_queries/insert_into_adm_hierarchy.sql").getFile().getAbsolutePath(), regionCode);
        parser.parse(new File(pathToXmlData + "/" + regionCode + "/" +
                        detectFileNameByMaskService.detectFileNameByObjectName(pathToXmlData, "AS_ADM_HIERARCHY")),
                xmlParserAdmHierarchy);

        XmlParserAddrObjects xmlParserAddrObjects = new XmlParserAddrObjects(
                new ClassPathResource("/database/insert_queries/insert_into_addr_objects.sql").getFile().getAbsolutePath(), regionCode);
        parser.parse(new File(pathToXmlData + "/" + regionCode + "/" +
                        detectFileNameByMaskService.detectFileNameByObjectName(pathToXmlData, "AS_ADDR_OBJ")),
                xmlParserAddrObjects);

        XmlParserHouses xmlParserHouses = new XmlParserHouses(
                new ClassPathResource("/database/insert_queries/insert_into_houses.sql").getFile().getAbsolutePath(), regionCode);
        parser.parse(new File(pathToXmlData + "/" + regionCode + "/" +
                        detectFileNameByMaskService.detectFileNameByObjectName(pathToXmlData, "AS_HOUSES")),
                xmlParserHouses);

        XmlParserApartments xmlParserApartments = new XmlParserApartments(
                new ClassPathResource("/database/insert_queries/insert_into_apartments.sql").getFile().getAbsolutePath(), regionCode);
        parser.parse(new File(pathToXmlData + "/" + regionCode + "/" +
                        detectFileNameByMaskService.detectFileNameByObjectName(pathToXmlData, "AS_APARTMENTS")),
                xmlParserApartments);
    }


    private class XmlParserAdmHierarchy extends DefaultHandler {

        private Connection connection;
        private PreparedStatement preparedStatement;
        private final String fileName;
        private final Short regionCode;
        private final Map<Long, List<AdmHierarchy>> admHierarchyUpdates;
        private int amountOfBatches;

        public XmlParserAdmHierarchy(String fileName, Short regionCode) throws SQLException, IOException {
            this.fileName = fileName;
            this.regionCode = regionCode;
            admHierarchyUpdates = new HashMap<>();
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
                currentAdmHierarchy.setActual(true);
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
//            List<Long> admHierarchyToDelete = new ArrayList<>(admHierarchyUpdates.keySet());
//            admHierarchyToDelete.forEach(admHierarchyRepository::deleteByObjectId);
            admHierarchyUpdates.forEach((key, value) -> {
                AdmHierarchy admHierarchy = value
                        .stream().min((ah1, ah2) -> ah2.getAdmHierarchyEndDate().compareTo(ah1.getAdmHierarchyEndDate())).orElse(null);
                assert admHierarchy != null;
//                admHierarchy.setAdmHierarchyId(admHierarchyRepository.findByObjectId(admHierarchy.getObjectId()).getAdmHierarchyId());
                if (admHierarchyRepository.existsAdmHierarchyByObjectId(admHierarchy.getObjectId())) {
                    admHierarchyRepository.deleteByObjectId(admHierarchy.getObjectId());
                }
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
                    preparedStatement.setBoolean(6, admHierarchy.getActual());
                    preparedStatement.setShort(7, regionCode);
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
        private int amountOfBatches;

        public XmlParserAddrObjects(String fileName, Short regionCode) throws SQLException, IOException {
            this.fileName = fileName;
            this.regionCode = regionCode;
            addressObjectsUpdates = new HashMap<>();
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
//            List<Long> addressObjectsToDelete = new ArrayList<>(addressObjectsUpdates.keySet());
//            addressObjectsToDelete.forEach(addressObjectsRepository::deleteByObjectId);
            // execute query to update record

//            AddressObjects addressObject =
//                    addressObjectsUpdatesList
//                            .stream()
//                            .min((ao1, ao2) -> ao2.getAddressObjectEndDate().compareTo(ao1.getAddressObjectEndDate()))
//                            .orElse(null);
//
//            assert addressObject != null;
//            addressObjectsRepository.saveAndFlush(addressObject);

            addressObjectsUpdates.forEach((key, value) -> {
                AddressObjects addressObject = value
                        .stream().min((ao1, ao2) -> ao2.getAddressObjectEndDate().compareTo(ao1.getAddressObjectEndDate())).orElse(null);

                assert addressObject != null;
//                addressObject.setAddressObjectId(addressObjectsRepository.findByObjectId(addressObject.getObjectId()).getAddressObjectId());
                if (addressObjectsRepository.existsAddressObjectsByObjectId(addressObject.getObjectId())) {
                    addressObjectsRepository.deleteByObjectId(addressObject.getObjectId());
                }
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
        private final String fileName;
        private final Short regionCode;
        private final Map<Long, List<Houses>> housesUpdates;
        private int amountOfBatches;

        public XmlParserHouses(String fileName, Short regionCode) throws SQLException, IOException {
            this.fileName = fileName;
            this.regionCode = regionCode;
            housesUpdates = new HashMap<>();
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
            String houseEndDate = attributes.getValue("ENDDATE");
            if (objectId != null) {
                Houses currentHouse = new Houses();
                Long objectIdLongValue = Long.parseLong(objectId);
                currentHouse.setHouseId(houseId != null ? Long.parseLong(houseId) : null);
                currentHouse.setObjectId(objectIdLongValue);
                currentHouse.setHouseNum(houseNum);
                currentHouse.setHouseType(houseType != null ? Integer.parseInt(houseType) : null);
                try {
                    currentHouse.setHouseEndDate(houseEndDate != null
                            ? new Date(simpleDateFormat.parse(houseEndDate).getTime())
                            : new Date(0L));
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                currentHouse.setRegionCode(regionCode);

                List<Houses> currentHousesList = housesUpdates.get(objectIdLongValue);
                if (currentHousesList != null) {
                    currentHousesList.add(currentHouse);
                    housesUpdates.put(objectIdLongValue, currentHousesList);
                } else {
                    List<Houses> housesListToInsert = new ArrayList<>();
                    housesListToInsert.add(currentHouse);
                    housesUpdates.put(objectIdLongValue, housesListToInsert);
                }
            }
        }

        @Override
        public void endDocument() {
//            List<Long> housesToDelete = new ArrayList<>(housesUpdates.keySet());
//            housesToDelete.forEach(housesRepository::deleteByObjectId);

            housesUpdates.forEach((key, value) -> {
                Houses house = value
                        .stream().min((h1, h2) -> h2.getHouseEndDate().compareTo(h1.getHouseEndDate())).orElse(null);

                assert house != null;
//                house.setHouseId(housesRepository.findByObjectId(house.getObjectId()).getHouseId());
                if (housesRepository.existsHousesByObjectId(house.getObjectId())) {
                    housesRepository.deleteByObjectId(house.getObjectId());
                }
                try {
                    if (house.getHouseId() != null) {
                        preparedStatement.setLong(1, house.getHouseId());
                    } else {
                        preparedStatement.setNull(1, Types.BIGINT);
                    }
                    if (house.getObjectId() != null) {
                        preparedStatement.setLong(2, house.getObjectId());
                    } else {
                        preparedStatement.setNull(2, Types.BIGINT);
                    }
                    preparedStatement.setString(3, house.getHouseNum());
                    if (house.getHouseType() != null) {
                        preparedStatement.setInt(4, house.getHouseType());
                    } else {
                        preparedStatement.setNull(4, Types.INTEGER);
                    }
                    if (house.getHouseEndDate() != null) {
                        preparedStatement.setDate(5, house.getHouseEndDate());
                    } else {
                        preparedStatement.setDate(5, new Date(0L));
                    }
                    preparedStatement.setShort(6, regionCode);
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
            });
            try {
                preparedStatement.close();
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

    }

    private class XmlParserApartments extends DefaultHandler {

        private Connection connection;
        private PreparedStatement preparedStatement;
        private final String fileName;
        private final Short regionCode;
        private final Map<Long, List<Apartments>> apartmentsUpdates;
        private int amountOfBatches;

        public XmlParserApartments(String fileName, Short regionCode) throws SQLException, IOException {
            this.fileName = fileName;
            this.regionCode = regionCode;
            apartmentsUpdates = new HashMap<>();
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
            if (qName.equals("APARTMENTS"))
                return;
            String apartmentId = attributes.getValue("ID");
            String objectId = attributes.getValue("OBJECTID");
            String apartmentType = attributes.getValue("APARTTYPE");
            String apartmentNumber = attributes.getValue("NUMBER");
            String apartmentEndDate = attributes.getValue("ENDDATE");
            if (objectId != null) {
                Apartments currentApartment = new Apartments();
                Long objectIdLongValue = Long.parseLong(objectId);
                currentApartment.setApartmentId(apartmentId != null ? Long.parseLong(apartmentId) : null);
                currentApartment.setObjectId(objectIdLongValue);
                currentApartment.setApartmentType(apartmentType != null ? Integer.parseInt(apartmentType) : null);
                currentApartment.setApartmentNumber(apartmentNumber);
                currentApartment.setActual(true);
                try {
                    currentApartment.setApartmentEndDate(apartmentEndDate != null
                            ? new Date(simpleDateFormat.parse(apartmentEndDate).getTime())
                            : new Date(0L));
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                currentApartment.setRegionCode(regionCode);

                List<Apartments> currentApartmentsList = apartmentsUpdates.get(objectIdLongValue);
                if (currentApartmentsList != null) {
                    currentApartmentsList.add(currentApartment);
                    apartmentsUpdates.put(objectIdLongValue, currentApartmentsList);
                } else {
                    List<Apartments> apartmentsListToInsert = new ArrayList<>();
                    apartmentsListToInsert.add(currentApartment);
                    apartmentsUpdates.put(objectIdLongValue, apartmentsListToInsert);
                }
            }
        }

        @Override
        public void endDocument() {
//            List<Long> apartmentsToDelete = new ArrayList<>(apartmentsUpdates.keySet());
//            apartmentsToDelete.forEach(apartmentsRepository::deleteByObjectId);
            apartmentsUpdates.forEach((key, value) -> {
                Apartments apartment = value
                        .stream().min((ap1, ap2) -> ap2.getApartmentEndDate().compareTo(ap1.getApartmentEndDate())).orElse(null);
                assert apartment != null;
//                apartment.setApartmentId(apartmentsRepository.findByObjectId(apartment.getObjectId()).getApartmentId());
                if (apartmentsRepository.existsApartmentsByObjectId(apartment.getObjectId())) {
                    apartmentsRepository.deleteByObjectId(apartment.getObjectId());
                }
                try {
                    if (apartment.getApartmentId() != null) {
                        preparedStatement.setLong(1, apartment.getApartmentId());
                    } else {
                        preparedStatement.setNull(1, Types.BIGINT);
                    }
                    if (apartment.getObjectId() != null) {
                        preparedStatement.setLong(2, apartment.getObjectId());
                    } else {
                        preparedStatement.setNull(2, Types.BIGINT);
                    }
                    if (apartment.getApartmentType() != null) {
                        preparedStatement.setInt(3, apartment.getApartmentType());
                    } else {
                        preparedStatement.setNull(3, Types.INTEGER);
                    }
                    preparedStatement.setString(4, apartment.getApartmentNumber());
                    if (apartment.getApartmentEndDate() != null) {
                        preparedStatement.setDate(5, apartment.getApartmentEndDate());
                    } else {
                        preparedStatement.setDate(5, new Date(0L));
                    }
                    preparedStatement.setBoolean(6, apartment.getActual());
                    preparedStatement.setShort(7, regionCode);
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
