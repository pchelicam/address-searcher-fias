CREATE PROCEDURE delete_extra_data()
LANGUAGE 'plpgsql'
AS $$
BEGIN

    DELETE FROM house_types
    WHERE house_type_id IS NULL
    OR house_type_id in (4, 6, 7, 8, 9, 11, 12, 13, 14);

    DELETE FROM apartment_types
    WHERE apartment_type_id IS NULL;

    DELETE FROM reestr_objects_rc64
    WHERE object_id IS NULL OR object_guid IS NULL;

    DELETE FROM adm_hierarchy_rc64
    WHERE adm_h_id IS NULL OR object_id IS NULL;

    DELETE FROM addr_objects_rc64
    WHERE addr_obj_id IS NULL OR object_id IS NULL;

    DELETE FROM houses_rc64
    WHERE house_id IS NULL OR object_id IS NULL
    OR house_num IS NULL;

    DELETE FROM apartments_rc64
    WHERE apartment_id IS NULL OR object_id IS NULL
    OR apart_number IS NULL;

    ALTER TABLE house_types ADD PRIMARY KEY (house_type_id);

    ALTER TABLE apartment_types ADD PRIMARY KEY (apartment_type_id);

    ALTER TABLE reestr_objects_rc64 ADD PRIMARY KEY (object_id);

    ALTER TABLE adm_hierarchy_rc64 ADD PRIMARY KEY (adm_h_id);

    ALTER TABLE addr_objects_rc64 ADD PRIMARY KEY (addr_obj_id);

    ALTER TABLE houses_rc64 ADD PRIMARY KEY (house_id);

    ALTER TABLE apartments_rc64 ADD PRIMARY KEY (apartment_id);

END; $$


