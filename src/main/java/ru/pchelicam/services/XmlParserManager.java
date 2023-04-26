package ru.pchelicam.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
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
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@Service
public class XmlParserManager {

    private final DataSource dataSource;
    private final AddressSearcherConfigRepository addressSearcherConfigRepository;
    private final AdmHierarchyRepository admHierarchyRepository;
    private final AddressObjectsRepository addressObjectsRepository;
    private final HousesRepository housesRepository;
    private final ApartmentsRepository apartmentsRepository;
    private final DetectFileNameByMaskService detectFileNameByMaskService;
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    public XmlParserManager(AddressSearcherConfigRepository addressSearcherConfigRepository, DataSource dataSource,
                            AdmHierarchyRepository admHierarchyRepository, AddressObjectsRepository addressObjectsRepository,
                            HousesRepository housesRepository, ApartmentsRepository apartmentsRepository,
                            DetectFileNameByMaskService detectFileNameByMaskService) {
        this.addressSearcherConfigRepository = addressSearcherConfigRepository;
        this.dataSource = dataSource;
        this.admHierarchyRepository = admHierarchyRepository;
        this.addressObjectsRepository = addressObjectsRepository;
        this.housesRepository = housesRepository;
        this.apartmentsRepository = apartmentsRepository;
        this.detectFileNameByMaskService = detectFileNameByMaskService;
    }

    public void manageDataInsert(Short regionCode) throws ParserConfigurationException, SAXException, IOException, SQLException {
        String pathToXmlData = addressSearcherConfigRepository.findByPropertyName("path_to_xml_data").getPropertyValue();

        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();

        XmlParserHouseTypes xmlParserHouseTypes = new XmlParserHouseTypes(
                new ClassPathResource("database/insert_queries/insert_into_house_types.sql").getFile().getAbsolutePath());
        parser.parse(new File(pathToXmlData + "/" +
                        detectFileNameByMaskService.detectFileNameByObjectName(pathToXmlData, "AS_HOUSE_TYPES")),
                xmlParserHouseTypes);

        XmlParserApartmentTypes xmlParserApartmentTypes = new XmlParserApartmentTypes(
                new ClassPathResource("/database/insert_queries/insert_into_apartment_types.sql").getFile().getAbsolutePath());
        parser.parse(new File(pathToXmlData + "/" +
                        detectFileNameByMaskService.detectFileNameByObjectName(pathToXmlData, "AS_APARTMENT_TYPES")),
                xmlParserApartmentTypes);

        XmlParserReestrObjects xmlParserReestrObjects = new XmlParserReestrObjects(
                new ClassPathResource("/database/insert_queries/insert_into_reestr_objects.sql").getFile().getAbsolutePath(), regionCode);
        parser.parse(new File(pathToXmlData + "/" + regionCode + "/" +
                        detectFileNameByMaskService.detectFileNameByObjectName(pathToXmlData + "/" + regionCode, "AS_REESTR_OBJECTS")),
                xmlParserReestrObjects);

        XmlParserAdmHierarchy xmlParserAdmHierarchy = new XmlParserAdmHierarchy(
                new ClassPathResource("/database/insert_queries/insert_into_adm_hierarchy.sql").getFile().getAbsolutePath(), regionCode);
        parser.parse(new File(pathToXmlData + "/" + regionCode + "/" +
                        detectFileNameByMaskService.detectFileNameByObjectName(pathToXmlData + "/" + regionCode, "AS_ADM_HIERARCHY")),
                xmlParserAdmHierarchy);

        XmlParserAddrObjects xmlParserAddrObjects = new XmlParserAddrObjects(
                new ClassPathResource("/database/insert_queries/insert_into_addr_objects.sql").getFile().getAbsolutePath(), regionCode);
        parser.parse(new File(pathToXmlData + "/" + regionCode + "/" +
                        detectFileNameByMaskService.detectFileNameByObjectName(pathToXmlData + "/" + regionCode, "AS_ADDR_OBJ")),
                xmlParserAddrObjects);

        XmlParserHouses xmlParserHouses = new XmlParserHouses(
                new ClassPathResource("/database/insert_queries/insert_into_houses.sql").getFile().getAbsolutePath(), regionCode);
        parser.parse(new File(pathToXmlData + "/" + regionCode + "/" +
                        detectFileNameByMaskService.detectFileNameByObjectName(pathToXmlData + "/" + regionCode, "AS_HOUSES")),
                xmlParserHouses);

        XmlParserApartments xmlParserApartments = new XmlParserApartments(
                new ClassPathResource("/database/insert_queries/insert_into_apartments.sql").getFile().getAbsolutePath(), regionCode);
        parser.parse(new File(pathToXmlData + "/" + regionCode + "/" +
                        detectFileNameByMaskService.detectFileNameByObjectName(pathToXmlData + "/" + regionCode, "AS_APARTMENTS")),
                xmlParserApartments);

        callAfterFullImport(regionCode);
//
        //       callDeleteExtraData(regionCode);
//        keepOnlyLatestUpdates(regionCode);
//
//        callAfterFullImport(regionCode);
    }

    public void manageReloadingData(Short regionCode) throws ParserConfigurationException, SAXException, IOException, SQLException, URISyntaxException {
        String pathToXmlData = addressSearcherConfigRepository.findByPropertyName("path_to_xml_data").getPropertyValue();

        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();

        callBeforeFullImport(regionCode);

        callTruncateAllTables(regionCode);

        XmlParserHouseTypes xmlParserHouseTypes = new XmlParserHouseTypes(
                new ClassPathResource("database/insert_queries/insert_into_house_types.sql").getFile().getAbsolutePath());
        parser.parse(new File(pathToXmlData + "/" +
                        detectFileNameByMaskService.detectFileNameByObjectName(pathToXmlData, "AS_HOUSE_TYPES")),
                xmlParserHouseTypes);

        XmlParserApartmentTypes xmlParserApartmentTypes = new XmlParserApartmentTypes(
                new ClassPathResource("/database/insert_queries/insert_into_apartment_types.sql").getFile().getAbsolutePath());
        parser.parse(new File(pathToXmlData + "/" +
                        detectFileNameByMaskService.detectFileNameByObjectName(pathToXmlData, "AS_APARTMENT_TYPES")),
                xmlParserApartmentTypes);

        XmlParserReestrObjects xmlParserReestrObjects = new XmlParserReestrObjects(
                new ClassPathResource("/database/insert_queries/insert_into_reestr_objects.sql").getFile().getAbsolutePath(), regionCode);
        parser.parse(new File(pathToXmlData + "/" + regionCode + "/" +
                        detectFileNameByMaskService.detectFileNameByObjectName(pathToXmlData + "/" + regionCode, "AS_REESTR_OBJECTS")),
                xmlParserReestrObjects);

        XmlParserAdmHierarchy xmlParserAdmHierarchy = new XmlParserAdmHierarchy(
                new ClassPathResource("/database/insert_queries/insert_into_adm_hierarchy.sql").getFile().getAbsolutePath(), regionCode);
        parser.parse(new File(pathToXmlData + "/" + regionCode + "/" +
                        detectFileNameByMaskService.detectFileNameByObjectName(pathToXmlData + "/" + regionCode, "AS_ADM_HIERARCHY")),
                xmlParserAdmHierarchy);

        XmlParserAddrObjects xmlParserAddrObjects = new XmlParserAddrObjects(
                new ClassPathResource("/database/insert_queries/insert_into_addr_objects.sql").getFile().getAbsolutePath(), regionCode);
        parser.parse(new File(pathToXmlData + "/" + regionCode + "/" +
                        detectFileNameByMaskService.detectFileNameByObjectName(pathToXmlData + "/" + regionCode, "AS_ADDR_OBJ")),
                xmlParserAddrObjects);

        XmlParserHouses xmlParserHouses = new XmlParserHouses(
                new ClassPathResource("/database/insert_queries/insert_into_houses.sql").getFile().getAbsolutePath(), regionCode);
        parser.parse(new File(pathToXmlData + "/" + regionCode + "/" +
                        detectFileNameByMaskService.detectFileNameByObjectName(pathToXmlData + "/" + regionCode, "AS_HOUSES")),
                xmlParserHouses);

        XmlParserApartments xmlParserApartments = new XmlParserApartments(
                new ClassPathResource("/database/insert_queries/insert_into_apartments.sql").getFile().getAbsolutePath(), regionCode);
        parser.parse(new File(pathToXmlData + "/" + regionCode + "/" +
                        detectFileNameByMaskService.detectFileNameByObjectName(pathToXmlData + "/" + regionCode, "AS_APARTMENTS")),
                xmlParserApartments);

        callAfterFullImport(regionCode);
    }

    private void callTruncateAllTables(Short regionCode) throws IOException, SQLException {
        Connection connection = dataSource.getConnection();
        Resource resource = new ClassPathResource("/database/call_procedures_queries/call_truncate_all_tables.sql");
        byte[] bytes = Files.readAllBytes(Paths.get(resource.getURI()));
        CallableStatement callableStatement = connection.prepareCall(new String(bytes));
        callableStatement.setShort(1, regionCode);
        callableStatement.execute();
        callableStatement.close();
        connection.close();
    }

    private void callBeforeFullImport(Short regionCode) throws IOException, SQLException {
        Connection connection = dataSource.getConnection();
        Resource resource = new ClassPathResource("database/call_procedures_queries/call_before_full_import.sql");
        byte[] bytes = Files.readAllBytes(Paths.get(resource.getURI()));

        CallableStatement callableStatement = connection.prepareCall(new String(bytes));
        callableStatement.setShort(1, regionCode);
        callableStatement.execute();
        callableStatement.close();
        connection.close();
    }

    private void callAfterFullImport(Short regionCode) throws IOException, SQLException {
        Connection connection = dataSource.getConnection();
        Resource resource = new ClassPathResource("/database/delete_queries/delete_extra_data_to_leave_only_actual_records.sql");
        byte[] bytes = Files.readAllBytes(Paths.get(resource.getURI()));
        Statement callableStatement = connection.createStatement();
        callableStatement.execute(new String(bytes).replaceAll("XXX", regionCode.toString()));
        callableStatement.close();
        connection.close();
    }

    private String readFile(String path) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, StandardCharsets.UTF_8);
    }

    private class XmlParserHouseTypes extends DefaultHandler {

        private Connection connection;
        private PreparedStatement preparedStatement;
        private int amountOfBatches;
        private final String fileName;

        public XmlParserHouseTypes(String fileName) throws SQLException, IOException {
            this.fileName = fileName;
            init();
        }

        private void init() throws SQLException, IOException {
            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement(readFile(fileName));
            amountOfBatches = 0;
        }

        private String readFile(String path) throws IOException {
            byte[] encoded = Files.readAllBytes(Paths.get(path));
            return new String(encoded, StandardCharsets.UTF_8);
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) {
            if (qName.equals("HOUSETYPES"))
                return;
            String houseTypeId = attributes.getValue("ID");
            String houseTypeName = attributes.getValue("NAME");
            String houseTypeShortName = attributes.getValue("SHORTNAME");
            try {
                if (houseTypeId != null) {
                    preparedStatement.setInt(1, Integer.parseInt(houseTypeId));
                } else {
                    preparedStatement.setNull(1, Types.INTEGER);
                }
                preparedStatement.setString(2, houseTypeName);
                preparedStatement.setString(3, houseTypeShortName);
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

    private class XmlParserApartmentTypes extends DefaultHandler {

        private Connection connection;
        private PreparedStatement preparedStatement;
        private int amountOfBatches;
        private final String fileName;

        public XmlParserApartmentTypes(String fileName) throws SQLException, IOException {
            this.fileName = fileName;
            init();
        }

        private void init() throws SQLException, IOException {
            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement(readFile(fileName));
            amountOfBatches = 0;
        }

        private String readFile(String path) throws IOException {
            byte[] encoded = Files.readAllBytes(Paths.get(path));
            return new String(encoded, StandardCharsets.UTF_8);
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) {
            if (qName.equals("APARTMENTTYPES"))
                return;
            String apartmentTypeId = attributes.getValue("ID");
            String apartmentTypeName = attributes.getValue("NAME");
            String apartmentTypeShortName = attributes.getValue("SHORTNAME");
            try {
                if (apartmentTypeId != null) {
                    preparedStatement.setInt(1, Integer.parseInt(apartmentTypeId));
                } else {
                    preparedStatement.setNull(1, Types.INTEGER);
                }
                preparedStatement.setString(2, apartmentTypeName);
                preparedStatement.setString(3, apartmentTypeShortName);
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

    private class XmlParserReestrObjects extends DefaultHandler {

        private Connection connection;
        private PreparedStatement preparedStatement;
        private int amountOfBatches;
        private final String fileName;
        private final Short regionCode;

        public XmlParserReestrObjects(String fileName, Short regionCode) throws SQLException, IOException {
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
            if (qName.equals("REESTR_OBJECTS"))
                return;
            String objectId = attributes.getValue("OBJECTID");
            String objectGUID = attributes.getValue("OBJECTGUID");
            try {
                if (objectId != null) {
                    preparedStatement.setLong(1, Long.parseLong(objectId));
                } else {
                    preparedStatement.setNull(1, Types.BIGINT);
                }
                preparedStatement.setString(2, objectGUID);
                preparedStatement.setShort(3, regionCode);
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

    private class XmlParserAdmHierarchy extends DefaultHandler {

        private Connection connection;
        private PreparedStatement preparedStatement;
        private int amountOfBatches;
        private final String fileName;
        private final Short regionCode;

        public XmlParserAdmHierarchy(String fileName, Short regionCode) throws SQLException, IOException {
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
            if (qName.equals("ITEMS"))
                return;
            String admHierarchyId = attributes.getValue("ID");
            String objectId = attributes.getValue("OBJECTID");
            String parentObjectId = attributes.getValue("PARENTOBJID");
            String fullPath = attributes.getValue("PATH");
            String admHierarchyEndDate = attributes.getValue("ENDDATE");
            String isActual = attributes.getValue("ISACTUAL");
            try {
                if (admHierarchyId != null) {
                    preparedStatement.setLong(1, Long.parseLong(admHierarchyId));
                } else {
                    preparedStatement.setNull(1, Types.BIGINT);
                }
                if (objectId != null) {
                    preparedStatement.setLong(2, Long.parseLong(objectId));
                } else {
                    preparedStatement.setNull(2, Types.BIGINT);
                }
                if (parentObjectId != null) {
                    preparedStatement.setLong(3, Long.parseLong(parentObjectId));
                } else {
                    preparedStatement.setNull(3, Types.BIGINT);
                }
                preparedStatement.setString(4, fullPath);
                if (admHierarchyEndDate != null) {
                    preparedStatement.setDate(5, new Date(simpleDateFormat.parse(admHierarchyEndDate).getTime()));
                } else {
                    preparedStatement.setDate(5, new Date(0L));
                }
                if (isActual != null) {
                    preparedStatement.setBoolean(6, isActual.equals("1"));
                } else {
                    preparedStatement.setBoolean(6, false);
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
            } catch (SQLException | ParseException e) {
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

    private class XmlParserAddrObjects extends DefaultHandler {

        private Connection connection;
        private PreparedStatement preparedStatement;
        private int amountOfBatches;
        private final String fileName;
        private final Short regionCode;

        public XmlParserAddrObjects(String fileName, Short regionCode) throws SQLException, IOException {
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
            if (qName.equals("ADDRESSOBJECTS"))
                return;
            String addressObjectId = attributes.getValue("ID");
            String objectId = attributes.getValue("OBJECTID");
            String addressObjectName = attributes.getValue("NAME");
            String typeName = attributes.getValue("TYPENAME");
            String objectLevel = attributes.getValue("LEVEL");
            String addressObjectEndDate = attributes.getValue("ENDDATE");
            try {
                if (addressObjectId != null) {
                    preparedStatement.setLong(1, Long.parseLong(addressObjectId));
                } else {
                    preparedStatement.setNull(1, Types.BIGINT);
                }
                if (objectId != null) {
                    preparedStatement.setLong(2, Long.parseLong(objectId));
                } else {
                    preparedStatement.setNull(2, Types.BIGINT);
                }
                preparedStatement.setString(3, addressObjectName);
                preparedStatement.setString(4, typeName);
                if (objectLevel != null) {
                    preparedStatement.setShort(5, Short.parseShort(objectLevel));
                } else {
                    preparedStatement.setNull(5, Types.SMALLINT);
                }
                if (addressObjectEndDate != null) {
                    preparedStatement.setDate(6, new Date(simpleDateFormat.parse(addressObjectEndDate).getTime()));
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
            } catch (SQLException | ParseException e) {
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
            String houseEndDate = attributes.getValue("ENDDATE");
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
                if (houseEndDate != null) {
                    preparedStatement.setDate(5, new Date(simpleDateFormat.parse(houseEndDate).getTime()));
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
            } catch (SQLException | ParseException e) {
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

    private class XmlParserApartments extends DefaultHandler {

        private Connection connection;
        private PreparedStatement preparedStatement;
        private int amountOfBatches;
        private final String fileName;
        private final Short regionCode;

        public XmlParserApartments(String fileName, Short regionCode) throws SQLException, IOException {
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
            if (qName.equals("APARTMENTS"))
                return;
            String apartmentId = attributes.getValue("ID");
            String objectId = attributes.getValue("OBJECTID");
            String apartmentType = attributes.getValue("APARTTYPE");
            String apartmentNumber = attributes.getValue("NUMBER");
            String apartmentEndDate = attributes.getValue("ENDDATE");
            String isActual = attributes.getValue("ISACTUAL");
            try {
                if (apartmentId != null) {
                    preparedStatement.setLong(1, Long.parseLong(apartmentId));
                } else {
                    preparedStatement.setNull(1, Types.BIGINT);
                }
                if (objectId != null) {
                    preparedStatement.setLong(2, Long.parseLong(objectId));
                } else {
                    preparedStatement.setNull(2, Types.BIGINT);
                }
                if (apartmentType != null) {
                    preparedStatement.setInt(3, Integer.parseInt(apartmentType));
                } else {
                    preparedStatement.setNull(3, Types.INTEGER);
                }
                preparedStatement.setString(4, apartmentNumber);
                if (apartmentEndDate != null) {
                    preparedStatement.setDate(5, new Date(simpleDateFormat.parse(apartmentEndDate).getTime()));
                } else {
                    preparedStatement.setDate(5, new Date(0L));
                }
                if (isActual != null) {
                    preparedStatement.setBoolean(6, isActual.equals("1"));
                } else {
                    preparedStatement.setBoolean(6, false);
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
            } catch (SQLException | ParseException e) {
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
