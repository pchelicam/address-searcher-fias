DELETE FROM adm_hierarchy
WHERE region_code = ?
AND adm_h_id IN (
    SELECT adm_h_id FROM (
        SELECT adm_h_id, object_id, adm_h_end_date, RANK() OVER (PARTITION BY object_id ORDER BY adm_h_end_date DESC)
        FROM adm_hierarchy
        WHERE region_code = ?
        AND object_id IN (
            SELECT object_id FROM (
                SELECT object_id, COUNT(*) AS cnt
                FROM fias.adm_hierarchy
                WHERE region_code = ?
                GROUP BY 1
            ) AS f1 WHERE cnt > 1
        )
    ) AS f2 WHERE rank > 1
)




DELETE FROM fias.addr_objects
WHERE region_code = 64
AND addr_obj_id IN (
    SELECT addr_obj_id FROM (
        SELECT addr_obj_id, object_id, addr_obj_end_date, RANK() OVER (PARTITION BY object_id ORDER BY addr_obj_end_date DESC)
        FROM fias.addr_objects
        WHERE region_code = 64
        AND object_id IN (
            SELECT object_id FROM (
                SELECT object_id, COUNT(*) AS cnt
                FROM fias.addr_objects
                WHERE region_code = 64
                GROUP BY 1
            ) AS f1 WHERE cnt > 1
        )
    ) AS f2 WHERE rank > 1
)


DELETE FROM fias.houses
WHERE region_code = 64
AND house_id IN (
    SELECT house_id FROM (
        SELECT house_id, object_id, house_end_date, RANK() OVER (PARTITION BY object_id ORDER BY house_end_date DESC)
        FROM fias.houses
        WHERE region_code = 64
        AND object_id IN (
            SELECT object_id FROM (
                SELECT object_id, COUNT(*) AS cnt
                FROM fias.houses
                WHERE region_code = 64
                GROUP BY 1
            ) AS f1 WHERE cnt > 1
        )
    ) AS f2 WHERE rank > 1
)


DELETE FROM fias.apartments
WHERE region_code = 64
AND apartment_id IN (
    SELECT apartment_id FROM (
        SELECT apartment_id, object_id, apart_end_date, RANK() OVER (PARTITION BY object_id ORDER BY apart_end_date DESC)
        FROM fias.apartments
        WHERE region_code = 64
        AND object_id IN (
            SELECT object_id FROM (
                SELECT object_id, COUNT(*) AS cnt
                FROM fias.apartments
                WHERE region_code = 64
                GROUP BY 1
            ) AS f1 WHERE cnt > 1
        )
    ) AS f2 WHERE rank > 1
)
