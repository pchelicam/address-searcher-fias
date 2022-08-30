CREATE OR REPLACE PROCEDURE before_full_import (
    _region_code IN SMALLINT
)
LANGUAGE 'plpgsql'
AS $$
BEGIN

    EXECUTE format('ALTER TABLE reestr_objects_rc%s'
                   'DROP CONSTRAINT object_id;', _region_code);

    EXECUTE format('ALTER TABLE adm_hierarchy_rc%s'
                   'DROP CONSTRAINT adm_h_id;', _region_code);

    EXECUTE format('ALTER TABLE addr_objects_rc%s'
                   'DROP CONSTRAINT addr_obj_id;', _region_code);

    EXECUTE format('ALTER TABLE houses_rc%s'
                   'DROP CONSTRAINT house_id;', _region_code);

    EXECUTE format('ALTER TABLE apartments_rc%s'
                   'DROP CONSTRAINT apartment_id;', _region_code);

    EXECUTE format('DROP INDEX i_addr_objects_rc%s_addr_name;', _region_code);

    EXECUTE format('DROP INDEX i_adm_hierarchy_rc%s_parent_object_id;', _region_code);

END; $$
