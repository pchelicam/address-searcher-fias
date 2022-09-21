CREATE OR REPLACE PROCEDURE before_full_import (
    _region_code SMALLINT
)
LANGUAGE 'plpgsql'
AS $$
BEGIN

    EXECUTE 'ALTER TABLE house_types '
            'DROP CONSTRAINT IF EXISTS house_types_pkey;';

    EXECUTE 'ALTER TABLE apartment_types '
            'DROP CONSTRAINT IF EXISTS apartment_types_pkey;';

    EXECUTE format('ALTER TABLE reestr_objects_rc%s '
                   'DROP CONSTRAINT IF EXISTS reestr_objects_rc%s_pkey;', _region_code, _region_code);

    EXECUTE format('ALTER TABLE adm_hierarchy_rc%s '
                   'DROP CONSTRAINT IF EXISTS adm_hierarchy_rc%s_pkey;', _region_code, _region_code);

    EXECUTE format('ALTER TABLE addr_objects_rc%s '
                   'DROP CONSTRAINT IF EXISTS addr_objects_rc%s_pkey;', _region_code, _region_code);

    EXECUTE format('ALTER TABLE houses_rc%s '
                   'DROP CONSTRAINT IF EXISTS houses_rc%s_pkey;', _region_code, _region_code);

    EXECUTE format('ALTER TABLE apartments_rc%s '
                   'DROP CONSTRAINT IF EXISTS apartments_rc%s_pkey;', _region_code, _region_code);

    EXECUTE format('DROP INDEX IF EXISTS i_addr_objects_rc%s_addr_name;', _region_code);

    EXECUTE format('DROP INDEX IF EXISTS i_adm_hierarchy_rc%s_parent_object_id;', _region_code);

END; $$
