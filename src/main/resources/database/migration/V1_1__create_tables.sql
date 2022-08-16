CREATE SCHEMA IF NOT EXISTS fias;

CREATE TABLE reestr_objects  (
    object_id BIGINT NOT NULL,
    object_guid VARCHAR(36) NOT NULL
);

CREATE TABLE adm_hierarchy (
    adm_h_id BIGINT NOT NULL,
    object_id BIGINT NOT NULL,
    parent_object_id BIGINT,
    region_code SMALLINT,
    full_path VARCHAR(150) NOT NULL
);

CREATE TABLE addr_objects (
    addr_obj_id BIGINT NOT NULL,
    object_id BIGINT NOT NULL,
    addr_name VARCHAR(250) NOT NULL,
    type_name VARCHAR(50) NOT NULL
);

CREATE TABLE house_types (
    house_type_id INTEGER NOT NULL,
    house_type_name VARCHAR(50) NOT NULL,
    house_type_shortname VARCHAR(50)
);

CREATE TABLE houses (
    house_id BIGINT NOT NULL,
    object_id BIGINT NOT NULL,
    house_num SMALLINT,
    house_type INTEGER
);

CREATE TABLE apartment_types (
    apartment_type_id INTEGER NOT NULL,
    apartment_type_name VARCHAR(50) NOT NULL,
    apartment_type_shortname VARCHAR(50)
);

CREATE TABLE apartments (
    apartment_id BIGINT NOT NULL,
    object_id BIGINT NOT NULL,
    apart_type INTEGER NOT NULL,
    apart_number VARCHAR(50) NOT NULL
);
