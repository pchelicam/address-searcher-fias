CREATE OR REPLACE PROCEDURE after_full_import (
    _region_code SMALLINT
)
LANGUAGE 'plpgsql'
AS $$
BEGIN

    DELETE FROM house_types
    WHERE house_type_id IS NULL
    OR house_type_id in (4, 6, 7, 9, 11, 12, 13, 14);

    DELETE FROM apartment_types
    WHERE apartment_type_id IS NULL;

    EXECUTE format('DELETE FROM reestr_objects_rc%s '
                   'WHERE object_id IS NULL OR object_guid IS NULL;', _region_code);

    EXECUTE format('DELETE FROM adm_hierarchy_rc%s '
                   'WHERE adm_h_id IS NULL OR object_id IS NULL;', _region_code);

    EXECUTE format('DELETE FROM addr_objects_rc%s '
                   'WHERE addr_obj_id IS NULL OR object_id IS NULL;', _region_code);

    EXECUTE format('DELETE FROM houses_rc%s '
                   'WHERE house_id IS NULL OR object_id IS NULL OR house_num IS NULL;', _region_code);

    EXECUTE format('DELETE FROM apartments_rc%s '
                   'WHERE apartment_id IS NULL OR object_id IS NULL OR apart_number IS NULL;', _region_code);

    EXECUTE 'ALTER TABLE house_types ADD PRIMARY KEY (house_type_id);';

    EXECUTE 'ALTER TABLE apartment_types ADD PRIMARY KEY (apartment_type_id);';

    EXECUTE format('ALTER TABLE reestr_objects_rc%s ADD PRIMARY KEY (object_id);', _region_code);

    EXECUTE format('ALTER TABLE adm_hierarchy_rc%s ADD PRIMARY KEY (adm_h_id);', _region_code);

    EXECUTE format('ALTER TABLE addr_objects_rc%s ADD PRIMARY KEY (addr_obj_id);', _region_code);

    EXECUTE format('ALTER TABLE houses_rc%s ADD PRIMARY KEY (house_id);', _region_code);

    EXECUTE format('ALTER TABLE apartments_rc%s ADD PRIMARY KEY (apartment_id);', _region_code);

    EXECUTE format('CREATE INDEX i_addr_objects_rc%s_addr_name '
                   'ON addr_objects_rc%s '
                   'USING btree ( LOWER( addr_name ) );', _region_code, _region_code);

    EXECUTE format('CREATE INDEX i_adm_hierarchy_rc%s_parent_object_id '
                   'ON adm_hierarchy_rc%s '
                   'USING btree ( parent_object_id );', _region_code, _region_code);

END; $$



