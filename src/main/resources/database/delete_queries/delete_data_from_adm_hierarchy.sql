delete from fias.adm_hierarchy_rc64 where adm_h_id in (
select adm_h_id from (
select adm_h_id, object_id, adm_h_end_date, rank() OVER (partition by object_id order by adm_h_end_date DESC)
FROM fias.adm_hierarchy
WHERE region_code = 64
and object_id in (
select object_id from (
SELECT object_id, COUNT(*) as cnt
FROM fias.adm_hierarchy
WHERE region_code = 64
GROUP BY 1
) as f1 where cnt > 1
)

) as f2 where rank >1

)