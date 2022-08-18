CREATE SCHEMA IF NOT EXISTS fias;

CREATE TABLE reestr_objects  (
    object_id BIGINT,
    object_guid VARCHAR(36)
);

CREATE TABLE adm_hierarchy (
    adm_h_id BIGINT,
    object_id BIGINT,
    parent_object_id BIGINT,
    region_code SMALLINT,
    full_path VARCHAR(150) NOT NULL
);

CREATE TABLE addr_objects (
    addr_obj_id BIGINT,
    object_id BIGINT,
    addr_name VARCHAR(250),
    type_name VARCHAR(50)
);

CREATE TABLE house_types (
    house_type_id INTEGER,
    house_type_name VARCHAR(50),
    house_type_shortname VARCHAR(50)
);

CREATE TABLE houses (
    house_id BIGINT,
    object_id BIGINT,
    house_num VARCHAR(50),
    house_type INTEGER
);

CREATE TABLE apartment_types (
    apartment_type_id INTEGER,
    apartment_type_name VARCHAR(50),
    apartment_type_shortname VARCHAR(50)
);

CREATE TABLE apartments (
    apartment_id BIGINT,
    object_id BIGINT,
    apart_type INTEGER,
    apart_number VARCHAR(50)
);
