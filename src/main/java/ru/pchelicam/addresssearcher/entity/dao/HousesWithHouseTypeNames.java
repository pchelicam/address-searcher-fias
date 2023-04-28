package ru.pchelicam.addresssearcher.entity.dao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SqlResultSetMapping;

@SqlResultSetMapping(
        name = "houseWithHouseTypeNamesMapping",
        classes = {
                @ConstructorResult(
                        targetClass = HousesWithHouseTypeNames.class,
                        columns = {
                                @ColumnResult(name = "object_id", type = Long.class),
                                @ColumnResult(name = "object_guid", type = String.class),
                                @ColumnResult(name = "house_num", type = String.class),
                                @ColumnResult(name = "house_type_name", type = String.class)
                        }
                )
        }
)
@NamedNativeQuery(
        name = "HousesWithHouseTypeNames.getHousesWithHouseTypeNames",
        query = "SELECT h.object_id, ro.object_guid, h.house_num, ht.house_type_name\n" +
                "FROM {h-schema}houses h\n" +
                "JOIN {h-schema}reestr_objects ro\n" +
                "ON h.object_id = ro.object_id\n" +
                "JOIN {h-schema}adm_hierarchy ah\n" +
                "ON h.object_id = ah.object_id\n" +
                "JOIN {h-schema}house_types ht\n" +
                "ON h.house_type = ht.house_type_id\n" +
                "WHERE h.region_code = :regionCode\n" +
                "AND ro.region_code = :regionCode\n" +
                "AND ah.region_code = :regionCode\n" +
                "AND ah.parent_object_id = :parentObjectId",
        resultSetMapping = "houseWithHouseTypeNamesMapping",
        resultClass = HousesWithHouseTypeNames.class
)

@AllArgsConstructor
@Getter
@Setter
@Entity
public class HousesWithHouseTypeNames {

    @Id
    @Column(name = "object_id")
    private Long objectId;

    @Column(name = "object_guid")
    private String objectGUID;

    @Column(name = "house_num")
    private String houseNum;

    @Column(name = "house_type_name")
    private String houseTypeName;

}

