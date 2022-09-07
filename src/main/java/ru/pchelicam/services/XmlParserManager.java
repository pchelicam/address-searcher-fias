package ru.pchelicam.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import ru.pchelicam.repositories.AddressSearcherConfigRepository;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Objects;

@Service
public class XmlParserManager {

    private final AddressSearcherConfigRepository addressSearcherConfigRepository;

    @Autowired
    public XmlParserManager(AddressSearcherConfigRepository addressSearcherConfigRepository) {
        this.addressSearcherConfigRepository = addressSearcherConfigRepository;
    }

    public void manageDataInsert() throws ParserConfigurationException, SAXException, IOException, SQLException {
        String pathToXmlData = addressSearcherConfigRepository.findByPropertyName("path_to_xml_data").getPropertyValue();

        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        Short regionCode = parseRegionCode("E:/gar_xml/64");

        callBeforeFullImport(regionCode);
        XmlParserHouseTypes xmlParserHouseTypes = new XmlParserHouseTypes(
                Objects.requireNonNull(
                        XmlParserManager.class.getResource("/database/insert_queries/insert_into_house_types.sql")).getPath());
        parser.parse(new File("E:/gar_xml/AS_HOUSE_TYPES_20220725_c833a2ab-b3d4-4857-b18f-b39e9225354e.XML"), xmlParserHouseTypes);

        XmlParserApartmentTypes xmlParserApartmentTypes = new XmlParserApartmentTypes(
                Objects.requireNonNull(
                        XmlParserManager.class.getResource("/database/insert_queries/insert_into_apartment_types.sql")).getPath());
        parser.parse(new File("E:/gar_xml/AS_APARTMENT_TYPES_20220725_c296d158-0a36-4398-a1a5-d6a1f8b5a524.XML"), xmlParserApartmentTypes);

        XmlParserReestrObjects xmlParserReestrObjects = new XmlParserReestrObjects(
                Objects.requireNonNull(
                        XmlParserManager.class.getResource("/database/insert_queries/insert_into_reestr_objects.sql")).getPath(),
                regionCode);
        parser.parse(new File("E:/gar_xml/64/AS_REESTR_OBJECTS_20220725_84a6555e-6ca7-46cb-a9f0-7c8ec7d9f633.XML"), xmlParserReestrObjects);

        XmlParserAdmHierarchy xmlParserAdmHierarchy = new XmlParserAdmHierarchy(
                Objects.requireNonNull(
                        XmlParserManager.class.getResource("/database/insert_queries/insert_into_adm_hierarchy.sql")).getPath(),
                regionCode);
        parser.parse(new File("E:/gar_xml/64/AS_ADM_HIERARCHY_20220725_c8537b65-da27-4b22-8433-ee5fbade9b2b.XML"), xmlParserAdmHierarchy);

        XmlParserAddrObjects xmlParserAddrObjects = new XmlParserAddrObjects(
                Objects.requireNonNull(
                        XmlParserManager.class.getResource("/database/insert_queries/insert_into_addr_objects.sql")).getPath(),
                regionCode);
        parser.parse(new File("E:/gar_xml/64/AS_ADDR_OBJ_20220725_7a19fd48-8c12-47fc-bf9a-f9b8aae10360.XML"), xmlParserAddrObjects);

        XmlParserHouses xmlParserHouses = new XmlParserHouses(
                Objects.requireNonNull(
                        XmlParserManager.class.getResource("/database/insert_queries/insert_into_houses.sql")).getPath(),
                regionCode);
        parser.parse(new File("E:/gar_xml/64/AS_HOUSES_20220725_bd25d6b8-631f-43ac-84d4-af63279e3134.XML"), xmlParserHouses);

        XmlParserApartments xmlParserApartments = new XmlParserApartments(
                Objects.requireNonNull(
                        XmlParserManager.class.getResource("/database/insert_queries/insert_into_apartments.sql")).getPath(),
                regionCode);
        parser.parse(new File("E:/gar_xml/64/AS_APARTMENTS_20220725_02445abb-66df-40f7-83b3-6aec7e34b4d7.XML"), xmlParserApartments);

        callAfterFullImport(regionCode);
    }

    private void callBeforeFullImport(Short regionCode) throws IOException, SQLException {
        Connection connection = DBCPDataSource.getConnection();
        CallableStatement callableStatement = connection.prepareCall(
                readFile(Objects.requireNonNull(
                                XmlParserManager.class.getResource("/database/call_procedures_queries/call_before_full_import.sql"))
                        .getPath()));
        callableStatement.setShort(1, regionCode);
        callableStatement.execute();
        callableStatement.close();
        connection.close();
    }

    private void callAfterFullImport(Short regionCode) throws IOException, SQLException {
        Connection connection = DBCPDataSource.getConnection();
        CallableStatement callableStatement = connection.prepareCall(
                readFile(Objects.requireNonNull(
                                XmlParserManager.class.getResource("/database/call_procedures_queries/call_after_full_import.sql"))
                        .getPath()));
        callableStatement.setShort(1, regionCode);
        callableStatement.execute();
        callableStatement.close();
        connection.close();
    }

    private static String readFile(String path) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, StandardCharsets.UTF_8);
    }

    private Short parseRegionCode(String pathToXmlData) {
        return Short.parseShort(
                Paths.get(pathToXmlData)
                        .getFileName().toString());
    }

    // TODO: rename fields
    private static class XmlParserHouseTypes extends DefaultHandler {

        private Connection connection;
        private PreparedStatement preparedStatement;
        private int amountOfBatches;
        private final String fileName;

        public XmlParserHouseTypes(String fileName) throws SQLException, IOException {
            this.fileName = fileName;
            init();
        }

        private void init() throws SQLException, IOException {
            connection = DBCPDataSource.getConnection();
            preparedStatement = connection.prepareStatement(readFile(fileName));
            amountOfBatches = 0;
        }

        private static String readFile(String path) throws IOException {
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

    private static class XmlParserApartmentTypes extends DefaultHandler {

        private Connection connection;
        private PreparedStatement preparedStatement;
        private int amountOfBatches;
        private final String fileName;

        public XmlParserApartmentTypes(String fileName) throws SQLException, IOException {
            this.fileName = fileName;
            init();
        }

        private void init() throws SQLException, IOException {
            connection = DBCPDataSource.getConnection();
            preparedStatement = connection.prepareStatement(readFile(fileName));
            amountOfBatches = 0;
        }

        private static String readFile(String path) throws IOException {
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

    private static class XmlParserReestrObjects extends DefaultHandler {

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
            connection = DBCPDataSource.getConnection();
            preparedStatement = connection.prepareStatement(readFile(fileName).replace("XXX", regionCode.toString()));
            amountOfBatches = 0;
        }

        private static String readFile(String path) throws IOException {
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

    private static class XmlParserAdmHierarchy extends DefaultHandler {

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
            connection = DBCPDataSource.getConnection();
            preparedStatement = connection.prepareStatement(readFile(fileName).replace("XXX", regionCode.toString()));
            amountOfBatches = 0;
        }

        private static String readFile(String path) throws IOException {
            byte[] encoded = Files.readAllBytes(Paths.get(path));
            return new String(encoded, StandardCharsets.UTF_8);
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) {
            if (qName.equals("ITEMS"))
                return;
            String admHId = attributes.getValue("ID");
            String objectId = attributes.getValue("OBJECTID");
            String parentObjId = attributes.getValue("PARENTOBJID");
            //String regionCode = attributes.getValue("REGIONCODE");
            String fullPath = attributes.getValue("PATH");
            try {
                if (admHId != null) {
                    preparedStatement.setLong(1, Long.parseLong(admHId));
                } else {
                    preparedStatement.setNull(1, Types.BIGINT);
                }
                if (objectId != null) {
                    preparedStatement.setLong(2, Long.parseLong(objectId));
                } else {
                    preparedStatement.setNull(2, Types.BIGINT);
                }
                if (parentObjId != null) {
                    preparedStatement.setLong(3, Long.parseLong(parentObjId));
                } else {
                    preparedStatement.setNull(3, Types.BIGINT);
                }
                //preparedStatement.setShort(4, Short.parseShort(regionCode));
                preparedStatement.setString(4, fullPath);
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

    private static class XmlParserAddrObjects extends DefaultHandler {

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
            connection = DBCPDataSource.getConnection();
            preparedStatement = connection.prepareStatement(readFile(fileName).replace("XXX", regionCode.toString()));
            amountOfBatches = 0;
        }

        private static String readFile(String path) throws IOException {
            byte[] encoded = Files.readAllBytes(Paths.get(path));
            return new String(encoded, StandardCharsets.UTF_8);
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) {
            if (qName.equals("ADDRESSOBJECTS"))
                return;
            String addrObjId = attributes.getValue("ID");
            String objectId = attributes.getValue("OBJECTID");
            String addrName = attributes.getValue("NAME");
            String typeName = attributes.getValue("TYPENAME");
            String objLevel = attributes.getValue("LEVEL");
            try {
                if (addrObjId != null) {
                    preparedStatement.setLong(1, Long.parseLong(addrObjId));
                } else {
                    preparedStatement.setNull(1, Types.BIGINT);
                }
                if (objectId != null) {
                    preparedStatement.setLong(2, Long.parseLong(objectId));
                } else {
                    preparedStatement.setNull(2, Types.BIGINT);
                }
                preparedStatement.setString(3, addrName);
                preparedStatement.setString(4, typeName);
                if (objLevel != null) {
                    preparedStatement.setShort(5, Short.parseShort(objLevel));
                } else {
                    preparedStatement.setNull(5, Types.SMALLINT);
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

    private static class XmlParserHouses extends DefaultHandler {

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
            connection = DBCPDataSource.getConnection();
            preparedStatement = connection.prepareStatement(readFile(fileName).replace("XXX", regionCode.toString()));
            amountOfBatches = 0;
        }

        private static String readFile(String path) throws IOException {
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

    private static class XmlParserApartments extends DefaultHandler {

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
            connection = DBCPDataSource.getConnection();
            preparedStatement = connection.prepareStatement(readFile(fileName).replace("XXX", regionCode.toString()));
            amountOfBatches = 0;
        }

        private static String readFile(String path) throws IOException {
            byte[] encoded = Files.readAllBytes(Paths.get(path));
            return new String(encoded, StandardCharsets.UTF_8);
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) {
            if (qName.equals("APARTMENTS"))
                return;
            String apartmentId = attributes.getValue("ID");
            String objectId = attributes.getValue("OBJECTID");
            String apartType = attributes.getValue("APARTTYPE");
            String apartNumber = attributes.getValue("NUMBER");
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
                if (apartType != null) {
                    preparedStatement.setInt(3, Integer.parseInt(apartType));
                } else {
                    preparedStatement.setNull(3, Types.INTEGER);
                }
                preparedStatement.setString(4, apartNumber);
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
