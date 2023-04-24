DELETE FROM fias.addr_objects_rcXXX
WHERE addr_obj_id IN (
    SELECT addr_obj_id FROM (
        SELECT addr_obj_id, object_id, addr_obj_end_date, ROW_NUMBER() OVER (PARTITION BY object_id ORDER BY addr_obj_end_date DESC)
        FROM fias.addr_objects
        WHERE region_code = XXX
        AND object_id IN (
            SELECT object_id FROM (
                SELECT object_id, COUNT(*) AS cnt
                FROM fias.addr_objects
                WHERE region_code = XXX
                GROUP BY 1
            ) AS f1 WHERE cnt > 1
        )
    ) AS f2 WHERE ROW_NUMBER > 1
);

DELETE FROM fias.adm_hierarchy_rcXXX
WHERE adm_h_id IN (
    SELECT adm_h_id FROM (
        SELECT adm_h_id, object_id, adm_h_end_date, ROW_NUMBER() OVER (PARTITION BY object_id ORDER BY adm_h_end_date DESC)
        FROM fias.adm_hierarchy
        WHERE region_code = XXX
        AND object_id IN (
            SELECT object_id FROM (
                SELECT object_id, COUNT(*) AS cnt
                FROM fias.adm_hierarchy
                WHERE region_code = XXX
                GROUP BY 1
            ) AS f1 WHERE cnt > 1
        )
    ) AS f2 WHERE ROW_NUMBER > 1
);

DELETE FROM fias.houses_rcXXX
WHERE house_id IN (
    SELECT house_id FROM (
        SELECT house_id, object_id, house_end_date, ROW_NUMBER() OVER (PARTITION BY object_id ORDER BY house_end_date DESC)
        FROM fias.houses
        WHERE region_code = XXX
        AND object_id IN (
            SELECT object_id FROM (
                SELECT object_id, COUNT(*) AS cnt
                FROM fias.houses
                WHERE region_code = XXX
                GROUP BY 1
            ) AS f1 WHERE cnt > 1
        )
    ) AS f2 WHERE ROW_NUMBER > 1
);

DELETE FROM fias.apartments_rcXXX
WHERE apartment_id IN (
    SELECT apartment_id FROM (
        SELECT apartment_id, object_id, apart_end_date, ROW_NUMBER() OVER (PARTITION BY object_id ORDER BY apart_end_date DESC)
        FROM fias.apartments
        WHERE region_code = XXX
        AND object_id IN (
            SELECT object_id FROM (
                SELECT object_id, COUNT(*) AS cnt
                FROM fias.apartments
                WHERE region_code = XXX
                GROUP BY 1
            ) AS f1 WHERE cnt > 1
        )
    ) AS f2 WHERE ROW_NUMBER > 1
);