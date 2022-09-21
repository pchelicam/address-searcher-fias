CREATE OR REPLACE PROCEDURE truncate_all_tables (
    _region_code SMALLINT
)
LANGUAGE 'plpgsql'
AS $$
BEGIN

    EXECUTE format('TRUNCATE house_types;', _region_code);

    EXECUTE format('TRUNCATE apartment_types;', _region_code);

    EXECUTE format('TRUNCATE reestr_objects_rc%s;', _region_code);

    EXECUTE format('TRUNCATE adm_hierarchy_rc%s;', _region_code);

    EXECUTE format('TRUNCATE addr_objects_rc%s;', _region_code);

    EXECUTE format('TRUNCATE houses_rc%s;', _region_code);

    EXECUTE format('TRUNCATE apartments_rc%s;', _region_code);

END; $$