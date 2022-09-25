CREATE TABLE address_searcher_config (
    property_id SERIAL PRIMARY KEY,
    property_name VARCHAR(100),
    property_value VARCHAR(100)
);

INSERT INTO address_searcher_config (property_name, property_value) VALUES ('path_to_xml_data', 'E:/gar_xml/');

INSERT INTO address_searcher_config (property_name, property_value) VALUES ('path_to_xml_data_updates', 'E:/gar_delta_xml/');