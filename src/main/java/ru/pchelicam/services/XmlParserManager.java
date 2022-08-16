package ru.pchelicam.services;

import org.springframework.stereotype.Service;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Service
public class XmlParserManager {

    public void manageDataInsert() throws ParserConfigurationException, SAXException, IOException, SQLException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        XmlParserReestrObjects xmlParserReestrObjects = new XmlParserReestrObjects(System.getProperty("user.dir") + "/src/main/resources/database/insert_queries/insert_into_reestr_objects.sql");
        parser.parse(new File("F:/gar_xml/64/AS_REESTR_OBJECTS_20220725_84a6555e-6ca7-46cb-a9f0-7c8ec7d9f633.XML"), xmlParserReestrObjects);

        XmlParserAdmHierarchy xmlParserAdmHierarchy = new XmlParserAdmHierarchy(System.getProperty("user.dir") + "/src/main/resources/database/insert_queries/insert_into_adm_hierarchy.sql");
        parser.parse(new File("F:/gar_xml/64/AS_ADM_HIERARCHY_20220725_c8537b65-da27-4b22-8433-ee5fbade9b2b.XML"), xmlParserAdmHierarchy);
    }

    private static class XmlParserReestrObjects extends DefaultHandler {

        private Connection connection;
        private PreparedStatement preparedStatement;
        private int amountOfBatches;
        private final String fileName;

        public XmlParserReestrObjects(String fileName) throws SQLException, IOException {
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
            if (qName.equals("REESTR_OBJECTS"))
                return;
            String objectId = attributes.getValue("OBJECTID");
            String objectGUID = attributes.getValue("OBJECTGUID");
            try {
                preparedStatement.setLong(1, Long.parseLong(objectId));
                preparedStatement.setString(2, objectGUID);
                if (amountOfBatches == 80) {
                    preparedStatement.executeBatch();
                    amountOfBatches = 0;
                }
                preparedStatement.addBatch();
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

        public XmlParserAdmHierarchy(String fileName) throws SQLException, IOException {
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
            if (qName.equals("ITEMS"))
                return;
            String admHId = attributes.getValue("ID");
            String objectId = attributes.getValue("OBJECTID");
            String parentObjId = attributes.getValue("PARENTOBJID");
            String regionCode = attributes.getValue("REGIONCODE");
            String fullPath = attributes.getValue("PATH");
            try {
                preparedStatement.setLong(1, Long.parseLong(admHId));
                preparedStatement.setLong(2, Long.parseLong(objectId));
                preparedStatement.setLong(3, Long.parseLong(parentObjId));
                preparedStatement.setShort(4, Short.parseShort(regionCode));
                preparedStatement.setString(5, fullPath);
                if (amountOfBatches == 80) {
                    preparedStatement.executeBatch();
                    amountOfBatches = 0;
                }
                preparedStatement.addBatch();
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
