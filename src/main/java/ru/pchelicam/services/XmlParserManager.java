package ru.pchelicam.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
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
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class XmlParserManager {

    private final DataSource dataSource;
    private final AddressSearcherConfigRepository addressSearcherConfigRepository;
    private final AddressObjectsRepository addressObjectsRepository;

    @Autowired
    public XmlParserManager(AddressSearcherConfigRepository addressSearcherConfigRepository, DataSource dataSource,
                            AddressObjectsRepository addressObjectsRepository) {
        this.addressSearcherConfigRepository = addressSearcherConfigRepository;
        this.dataSource = dataSource;
        this.addressObjectsRepository = addressObjectsRepository;
    }

    public void manageDataInsert(Short regionCode) throws ParserConfigurationException, SAXException, IOException, SQLException {
        String pathToXmlData = addressSearcherConfigRepository.findByPropertyName("path_to_xml_data").getPropertyValue();

        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();

//        XmlParserHouseTypes xmlParserHouseTypes = new XmlParserHouseTypes(
//                new ClassPathResource("database/insert_queries/insert_into_house_types.sql").getFile().getAbsolutePath());
//        parser.parse(new File(pathToXmlData + "/" + "AS_HOUSE_TYPES_20220725_c833a2ab-b3d4-4857-b18f-b39e9225354e.XML"), xmlParserHouseTypes);
//
//        XmlParserApartmentTypes xmlParserApartmentTypes = new XmlParserApartmentTypes(
//                new ClassPathResource("/database/insert_queries/insert_into_apartment_types.sql").getFile().getAbsolutePath());
//        parser.parse(new File(pathToXmlData + "/" + "AS_APARTMENT_TYPES_20220725_c296d158-0a36-4398-a1a5-d6a1f8b5a524.XML"), xmlParserApartmentTypes);
//
//        XmlParserReestrObjects xmlParserReestrObjects = new XmlParserReestrObjects(
//                new ClassPathResource("/database/insert_queries/insert_into_reestr_objects.sql").getFile().getAbsolutePath(), regionCode);
//        parser.parse(new File(pathToXmlData + "/" + regionCode + "/" + "AS_REESTR_OBJECTS_20220725_84a6555e-6ca7-46cb-a9f0-7c8ec7d9f633.XML"), xmlParserReestrObjects);
//
//        XmlParserAdmHierarchy xmlParserAdmHierarchy = new XmlParserAdmHierarchy(
//                new ClassPathResource("/database/insert_queries/insert_into_adm_hierarchy.sql").getFile().getAbsolutePath(), regionCode);
//        parser.parse(new File(pathToXmlData + "/" + regionCode + "/" + "AS_ADM_HIERARCHY_20220725_c8537b65-da27-4b22-8433-ee5fbade9b2b.XML"), xmlParserAdmHierarchy);

        XmlParserAddrObjects xmlParserAddrObjects = new XmlParserAddrObjects(
                new ClassPathResource("/database/insert_queries/insert_into_addr_objects.sql").getFile().getAbsolutePath(), regionCode);
        parser.parse(new File(pathToXmlData + "/" + regionCode + "/" + "AS_ADDR_OBJ_20220725_7a19fd48-8c12-47fc-bf9a-f9b8aae10360.XML"), xmlParserAddrObjects);

        keepOnlyLatestUpdates(regionCode);

//        XmlParserHouses xmlParserHouses = new XmlParserHouses(
//                new ClassPathResource("/database/insert_queries/insert_into_houses.sql").getFile().getAbsolutePath(), regionCode);
//        parser.parse(new File(pathToXmlData + "/" + regionCode + "/" + "AS_HOUSES_20220725_bd25d6b8-631f-43ac-84d4-af63279e3134.XML"), xmlParserHouses);
//
//        XmlParserApartments xmlParserApartments = new XmlParserApartments(
//                new ClassPathResource("/database/insert_queries/insert_into_apartments.sql").getFile().getAbsolutePath(), regionCode);
//        parser.parse(new File(pathToXmlData + "/" + regionCode + "/" + "AS_APARTMENTS_20220725_02445abb-66df-40f7-83b3-6aec7e34b4d7.XML"), xmlParserApartments);
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
        parser.parse(new File(pathToXmlData + "/" + "AS_HOUSE_TYPES_20220725_c833a2ab-b3d4-4857-b18f-b39e9225354e.XML"), xmlParserHouseTypes);

        XmlParserApartmentTypes xmlParserApartmentTypes = new XmlParserApartmentTypes(
                new ClassPathResource("/database/insert_queries/insert_into_apartment_types.sql").getFile().getAbsolutePath());
        parser.parse(new File(pathToXmlData + "/" + "AS_APARTMENT_TYPES_20220725_c296d158-0a36-4398-a1a5-d6a1f8b5a524.XML"), xmlParserApartmentTypes);

        XmlParserReestrObjects xmlParserReestrObjects = new XmlParserReestrObjects(
                new ClassPathResource("/database/insert_queries/insert_into_reestr_objects.sql").getFile().getAbsolutePath(), regionCode);
        parser.parse(new File(pathToXmlData + "/" + regionCode + "/" + "AS_REESTR_OBJECTS_20220725_84a6555e-6ca7-46cb-a9f0-7c8ec7d9f633.XML"), xmlParserReestrObjects);

        XmlParserAdmHierarchy xmlParserAdmHierarchy = new XmlParserAdmHierarchy(
                new ClassPathResource("/database/insert_queries/insert_into_adm_hierarchy.sql").getFile().getAbsolutePath(), regionCode);
        parser.parse(new File(pathToXmlData + "/" + regionCode + "/" + "AS_ADM_HIERARCHY_20220725_c8537b65-da27-4b22-8433-ee5fbade9b2b.XML"), xmlParserAdmHierarchy);

        XmlParserAddrObjects xmlParserAddrObjects = new XmlParserAddrObjects(
                new ClassPathResource("/database/insert_queries/insert_into_addr_objects.sql").getFile().getAbsolutePath(), regionCode);
        parser.parse(new File(pathToXmlData + "/" + regionCode + "/" + "AS_ADDR_OBJ_20220725_7a19fd48-8c12-47fc-bf9a-f9b8aae10360.XML"), xmlParserAddrObjects);

        XmlParserHouses xmlParserHouses = new XmlParserHouses(
                new ClassPathResource("/database/insert_queries/insert_into_houses.sql").getFile().getAbsolutePath(), regionCode);
        parser.parse(new File(pathToXmlData + "/" + regionCode + "/" + "AS_HOUSES_20220725_bd25d6b8-631f-43ac-84d4-af63279e3134.XML"), xmlParserHouses);

        XmlParserApartments xmlParserApartments = new XmlParserApartments(
                new ClassPathResource("/database/insert_queries/insert_into_apartments.sql").getFile().getAbsolutePath(), regionCode);
        parser.parse(new File(pathToXmlData + "/" + regionCode + "/" + "AS_APARTMENTS_20220725_02445abb-66df-40f7-83b3-6aec7e34b4d7.XML"), xmlParserApartments);

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

    private void callBeforeFullImport(Short regionCode) throws IOException, SQLException, URISyntaxException {
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
        Resource resource = new ClassPathResource("/database/call_procedures_queries/call_after_full_import.sql");
        byte[] bytes = Files.readAllBytes(Paths.get(resource.getURI()));
        CallableStatement callableStatement = connection.prepareCall(new String(bytes));
        callableStatement.setShort(1, regionCode);
        callableStatement.execute();
        callableStatement.close();
        connection.close();
    }

    private void keepOnlyLatestUpdates(Short regionCode) throws SQLException, IOException {
//        Connection connection = XmlParserManager.this.dataSource.getConnection();
//        PreparedStatement preparedStatement = connection.prepareStatement(readFile(
//                new ClassPathResource("database/select_queries/select_distinct_object_id_from_addr_objects.sql").getFile().getAbsolutePath()));
//        preparedStatement.setShort(1, regionCode);
//        ResultSet resultSet = preparedStatement.executeQuery();
//        PreparedStatement preparedStatementToDelete = connection.prepareStatement(readFile(
//                new ClassPathResource("database/delete_queries/delete_from_addr_objects_by_addr_obj_id.sql").getFile().getAbsolutePath()));
//        int amountOfBatches = 0;
//        while (resultSet.next()) {
//            long currentObjectId = resultSet.getLong("object_id");
//            PreparedStatement preparedStatementTmp = connection.prepareStatement(readFile(
//                    new ClassPathResource("database/select_queries/select_addr_object_id_from_addr_objects_order_by_addr_obj_update_date.sql")
//                            .getFile().getAbsolutePath()));
//            preparedStatementTmp.setShort(1, regionCode);
//            preparedStatementTmp.setLong(2, currentObjectId);
//            ResultSet resultSetTmp = preparedStatementTmp.executeQuery();
//            resultSetTmp.next();
//            while (resultSetTmp.next()) {
//                long currentAddressObjectId = resultSetTmp.getLong("addr_obj_id");
//                preparedStatementToDelete.setLong(1, currentAddressObjectId);
//                if (amountOfBatches == 80) {
//                    preparedStatementToDelete.executeBatch();
//                    preparedStatementToDelete.clearBatch();
//                    amountOfBatches = 0;
//                }
//                preparedStatementToDelete.addBatch();
//                preparedStatement.clearParameters();
//                amountOfBatches++;
//            }
//        }
        List<Long> uniqueObjectIdsList = addressObjectsRepository.findUniqueObjectIds(regionCode);
        uniqueObjectIdsList.forEach(ob -> {
            List<AddressObjects> allAddressObjectUpdates =
                    addressObjectsRepository
                            .findByRegionCodeAndObjectIdOrderByAddressObjectUpdateDateDesc(regionCode, ob);
            if (allAddressObjectUpdates.size() > 1) {
                addressObjectsRepository.deleteAllById(allAddressObjectUpdates.stream().skip(1L)
                        .map(AddressObjects::getAddressObjectId).collect(Collectors.toList()));
                //allAddressObjectUpdates.stream().skip(1L).forEach(up -> addressObjectsRepository.deleteById(up.getAddressObjectId()));
            }
        });
    }

    private String readFile(String path) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, StandardCharsets.UTF_8);
    }

    // TODO: rename fields
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
            connection = XmlParserManager.this.dataSource.getConnection();
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
            connection = XmlParserManager.this.dataSource.getConnection();
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
            connection = XmlParserManager.this.dataSource.getConnection();
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
            connection = XmlParserManager.this.dataSource.getConnection();
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
            connection = XmlParserManager.this.dataSource.getConnection();
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
            String addrObjId = attributes.getValue("ID");
            String objectId = attributes.getValue("OBJECTID");
            String addrObjName = attributes.getValue("NAME");
            String typeName = attributes.getValue("TYPENAME");
            String objLevel = attributes.getValue("LEVEL");
            //String prevId = attributes.getValue("PREVID");
            String addrObjUpdateDate = attributes.getValue("UPDATEDATE");
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

            //if (prevId == null || prevId.equals("0")) {
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
                preparedStatement.setString(3, addrObjName);
                preparedStatement.setString(4, typeName);
                if (objLevel != null) {
                    preparedStatement.setShort(5, Short.parseShort(objLevel));
                } else {
                    preparedStatement.setNull(5, Types.SMALLINT);
                }
//                if (prevId != null) {
//                    preparedStatement.setLong(6, Long.parseLong(prevId));
//                } else {
//                    preparedStatement.setNull(6, Types.BIGINT);
//                }
                if (addrObjUpdateDate != null) {
                    preparedStatement.setDate(6, new Date(formatter.parse(addrObjUpdateDate).getTime()));
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
//            } else {
//                try {
//                    PreparedStatement preparedStatementForSelect =
//                            connection.prepareStatement(
//                                    readFile("E:/Flex/address-searcher-fias/src/main/resources/database/select_queries/select_from_addr_objects_primary_and_next_ids.sql")
//                                            .replace("XXX", regionCode.toString()));
//                    preparedStatementForSelect.setLong(1, Long.parseLong(prevId));
//                    ResultSet resultSet = preparedStatementForSelect.executeQuery();
//
//                    if (!resultSet.next()) { // nothing return after execution query
//                        PreparedStatement preparedStatementForInsert = connection.prepareStatement(
//                                readFile("E:/Flex/address-searcher-fias/src/main/resources/database/insert_queries/insert_into_addr_objects_primary_and_next_ids.sql")
//                                        .replace("XXX", regionCode.toString()));
//                        if (addrObjId != null) {
//                            preparedStatementForInsert.setLong(1, Long.parseLong(addrObjId));
//                        } else {
//                            preparedStatementForInsert.setNull(1, Types.BIGINT);
//                        }
//                        preparedStatementForInsert.setLong(2, Long.parseLong(prevId));
//                        preparedStatementForInsert.executeUpdate();
//
//                        PreparedStatement preparedStatementForUpdate = connection.prepareStatement(
//                                readFile("E:/Flex/address-searcher-fias/src/main/resources/database/update_queries/update_addr_objects.sql")
//                                        .replace("XXX", regionCode.toString()));
//                        if (objectId != null) {
//                            preparedStatementForUpdate.setLong(1, Long.parseLong(objectId));
//                        } else {
//                            preparedStatementForUpdate.setNull(1, Types.BIGINT);
//                        }
//                        preparedStatementForUpdate.setString(2, addrName);
//                        preparedStatementForUpdate.setString(3, typeName);
//                        if (objLevel != null) {
//                            preparedStatementForUpdate.setShort(4, Short.parseShort(objLevel));
//                        } else {
//                            preparedStatementForUpdate.setNull(4, Types.SMALLINT);
//                        }
//                        preparedStatementForUpdate.setShort(5, regionCode);
//                        if (addrObjId != null) {
//                            preparedStatementForUpdate.setLong(6, Long.parseLong(prevId));
//                        } else {
//                            preparedStatementForUpdate.setNull(6, Types.BIGINT);
//                        }
//                        preparedStatementForUpdate.executeUpdate();
//
//                    } else {
//                        long primaryId = resultSet.getLong("primary_id");
//                        PreparedStatement preparedStatementForInsert = connection.prepareStatement(
//                                readFile("E:/Flex/address-searcher-fias/src/main/resources/database/insert_queries/insert_into_addr_objects_primary_and_next_ids.sql")
//                                        .replace("XXX", regionCode.toString()));
//                        if (addrObjId != null) {
//                            preparedStatementForInsert.setLong(1, Long.parseLong(addrObjId));
//                        } else {
//                            preparedStatementForInsert.setNull(1, Types.BIGINT);
//                        }
//                        preparedStatementForInsert.setLong(2, primaryId);
//                        preparedStatementForInsert.executeUpdate();
//
//                        PreparedStatement preparedStatementForUpdate = connection.prepareStatement(
//                                readFile("E:/Flex/address-searcher-fias/src/main/resources/database/update_queries/update_addr_objects.sql")
//                                        .replace("XXX", regionCode.toString()));
//                        if (objectId != null) {
//                            preparedStatementForUpdate.setLong(1, Long.parseLong(objectId));
//                        } else {
//                            preparedStatementForUpdate.setNull(1, Types.BIGINT);
//                        }
//                        preparedStatementForUpdate.setString(2, addrName);
//                        preparedStatementForUpdate.setString(3, typeName);
//                        if (objLevel != null) {
//                            preparedStatementForUpdate.setShort(4, Short.parseShort(objLevel));
//                        } else {
//                            preparedStatementForUpdate.setNull(4, Types.SMALLINT);
//                        }
//                        preparedStatementForUpdate.setShort(5, regionCode);
//                        if (addrObjId != null) {
//                            preparedStatementForUpdate.setLong(6, primaryId);
//                        } else {
//                            preparedStatementForUpdate.setNull(6, Types.BIGINT);
//                        }
//                        preparedStatementForUpdate.executeUpdate();
//                    }
//                } catch (SQLException | IOException e) {
//                    throw new RuntimeException(e);
//                }
//            }
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
            connection = XmlParserManager.this.dataSource.getConnection();
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
            connection = XmlParserManager.this.dataSource.getConnection();
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
