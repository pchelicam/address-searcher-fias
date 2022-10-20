CREATE OR REPLACE PROCEDURE delete_extra_data2 (
    _region_code SMALLINT
)
LANGUAGE 'plpgsql'
AS $$
BEGIN

--        EXECUTE format('DELETE FROM adm_hierarchy '
--                       'WHERE region_code = ? '
--                        AND adm_h_id IN (
--                            SELECT adm_h_id FROM (
--                                SELECT adm_h_id, object_id, adm_h_end_date, RANK() OVER (PARTITION BY object_id ORDER BY adm_h_end_date DESC)
--                                FROM fias.adm_hierarchy
--                                WHERE region_code = ?
--                                AND object_id IN (
--                                    SELECT object_id FROM (
--                                        SELECT object_id, COUNT(*) AS cnt
--                                        FROM fias.adm_hierarchy
--                                        WHERE region_code = ?
--                                        GROUP BY 1
--                                    ) AS f1 WHERE cnt > 1
--                                )
--                            ) AS f2 WHERE rank > 1
--                        )', _region_code, _region_code);

END; $$;