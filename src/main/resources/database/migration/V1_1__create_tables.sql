CREATE TABLE house_types (
    house_type_id INTEGER,
    house_type_name VARCHAR(50),
    house_type_shortname VARCHAR(50)
);

CREATE TABLE apartment_types (
    apartment_type_id INTEGER,
    apartment_type_name VARCHAR(50),
    apartment_type_shortname VARCHAR(50)
);

CREATE TABLE reestr_objects  (
    object_id BIGINT,
    object_guid VARCHAR(36),
    reestr_objects_end_date DATE,
    region_code SMALLINT
);

CREATE TABLE adm_hierarchy (
    adm_h_id BIGINT,
    object_id BIGINT,
    parent_object_id BIGINT,
    full_path VARCHAR(150),
    region_code SMALLINT
);

CREATE TABLE addr_objects (
    addr_obj_id BIGINT,
    object_id BIGINT,
    addr_obj_name VARCHAR(250),
    type_name VARCHAR(50),
    obj_level SMALLINT,
    addr_obj_end_date DATE,
    region_code SMALLINT
);

CREATE TABLE houses (
    house_id BIGINT,
    object_id BIGINT,
    house_num VARCHAR(50),
    house_type INTEGER,
    region_code SMALLINT
);

CREATE TABLE apartments (
    apartment_id BIGINT,
    object_id BIGINT,
    apart_type INTEGER,
    apart_number VARCHAR(50),
    region_code SMALLINT
);
