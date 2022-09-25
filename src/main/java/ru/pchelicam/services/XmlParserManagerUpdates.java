package ru.pchelicam.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

@Service
public class XmlParserManagerUpdates {

    private final DataSource dataSource;
    private final AddressSearcherConfigRepository addressSearcherConfigRepository;

    @Autowired
    public XmlParserManagerUpdates(AddressSearcherConfigRepository addressSearcherConfigRepository, DataSource dataSource) {
        this.addressSearcherConfigRepository = addressSearcherConfigRepository;
        this.dataSource = dataSource;
    }

    public void manageUpdatingData(Short regionCode) throws IOException, SQLException, ParserConfigurationException, SAXException {
        String pathToXmlData = addressSearcherConfigRepository.findByPropertyName("path_to_xml_data_updates").getPropertyValue();

        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();

        XmlParserAddrObjects xmlParserAddrObjects = new XmlParserAddrObjects(
        new ClassPathResource("/database/update_queries/update_addr_objects.sql").getFile().getAbsolutePath(), regionCode);
        parser.parse(new File(pathToXmlData + "/" + regionCode + "/" + "AS_ADDR_OBJ_20220915_638c30c1-e1c1-4e96-81a1-0120b3f861f2.XML"), xmlParserAddrObjects);
    }


    private class XmlParserAddrObjects extends DefaultHandler {

        private Connection connection;
        private PreparedStatement preparedStatement;
        private final String fileName;
        private final Short regionCode;

        public XmlParserAddrObjects(String fileName, Short regionCode) throws SQLException, IOException {
            this.fileName = fileName;
            this.regionCode = regionCode;
            init();
        }

        private void init() throws SQLException, IOException {
            connection = XmlParserManagerUpdates.this.dataSource.getConnection();
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
            String addrObjId = attributes.getValue("ID");
            String objectId = attributes.getValue("OBJECTID");
            String addrName = attributes.getValue("NAME");
            String typeName = attributes.getValue("TYPENAME");
            String objLevel = attributes.getValue("LEVEL");
            try {
                if (objectId != null) {
                    preparedStatement.setLong(1, Long.parseLong(objectId));
                } else {
                    preparedStatement.setNull(1, Types.BIGINT);
                }
                preparedStatement.setString(2, addrName);
                preparedStatement.setString(3, typeName);
                if (objLevel != null) {
                    preparedStatement.setShort(4, Short.parseShort(objLevel));
                } else {
                    preparedStatement.setNull(4, Types.SMALLINT);
                }
                preparedStatement.setShort(5, regionCode);
                if (addrObjId != null) {
                    preparedStatement.setLong(6, Long.parseLong(addrObjId));
                } else {
                    preparedStatement.setNull(6, Types.BIGINT);
                }
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void endDocument() {
            try {
                preparedStatement.close();
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

    }

}
