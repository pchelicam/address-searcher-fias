CREATE OR REPLACE PROCEDURE delete_extra_data(
    _region_code SMALLINT
)
LANGUAGE 'plpgsql'
AS $$
DECLARE
    table_row RECORD;
    table_row_inner RECORD;
    curr_object_id BIGINT;
    curr_adm_h_id BIGINT;
BEGIN

    FOR table_row IN
        SELECT DISTINCT object_id
        FROM adm_hierarchy
        WHERE region_code = _region_code
    LOOP

        curr_object_id := table_row.object_id;
        FOR table_row_inner IN
            SELECT adm_h_id
            FROM adm_hierarchy
            WHERE region_code = _region_code AND object_id = curr_object_id
            ORDER BY adm_h_end_date DESC
            OFFSET 1
        LOOP
           curr_adm_h_id = table_row_inner.adm_h_id;

           EXECUTE format('DELETE FROM adm_hierarchy WHERE adm_h_id = %s;', curr_adm_h_id);

        END LOOP;

    END LOOP;

END; $$;