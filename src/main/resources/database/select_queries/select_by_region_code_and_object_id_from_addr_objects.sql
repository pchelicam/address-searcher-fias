SELECT addr_obj_id
FROM addr_objects
WHERE region_code = ? AND object_id = ?
ORDER BY addr_obj_end_date DESC