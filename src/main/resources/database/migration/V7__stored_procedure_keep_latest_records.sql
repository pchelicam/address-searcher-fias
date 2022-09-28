CREATE OR REPLACE PROCEDURE keep_latest_records (
    _region_code SMALLINT
)
LANGUAGE 'plpgsql'
AS $$
DECLARE
    table_row RECORD;
BEGIN

--    distinct_object_ids := (SELECT DISTINCT object_id FROM addr_objects WHERE region_code=_region_code);

--    FOR table_row IN
--        distinct_object_ids
--    LOOP
--        -- лучше сделать через java код
--
--    END LOOP;

END; $$