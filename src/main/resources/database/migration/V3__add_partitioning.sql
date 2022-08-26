CREATE TABLE region_codes (
    region_code SMALLINT
);

DO
$$
BEGIN
   FOR i IN 1..99 LOOP
      INSERT INTO region_codes (region_code) VALUES (i);
   END LOOP;
END
$$;


CREATE OR REPLACE PROCEDURE add_partitioning()
LANGUAGE 'plpgsql'
AS $$
DECLARE
    table_row RECORD;
    table_n VARCHAR(100);
    constr_n VARCHAR(100);
    region_code VARCHAR(2);
BEGIN

    FOR table_row IN
        SELECT * FROM region_codes
    LOOP

        region_code := table_row.region_code;
        table_n := concat('reestr_objects_rc', region_code);
        constr_n := concat('reestr_objects_rc',region_code,'_check');
        EXECUTE format('CREATE TABLE %s ( CONSTRAINT %s CHECK(region_code = %s) ) INHERITS (%s);', table_n, constr_n, region_code, 'reestr_objects');

        table_n := concat('adm_hierarchy_rc', region_code);
        constr_n := concat('adm_hierarchy_rc',region_code,'_check');
        EXECUTE format('CREATE TABLE %s (CONSTRAINT %s CHECK(region_code = %s) ) INHERITS (%s);', table_n, constr_n, region_code, 'adm_hierarchy');


    END LOOP;
END; $$;

CALL add_partitioning();