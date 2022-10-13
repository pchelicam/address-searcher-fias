SELECT adm_h_id
FROM adm_hierarchy
WHERE region_code = ? AND object_id = ?
ORDER BY adm_h_end_date DESC