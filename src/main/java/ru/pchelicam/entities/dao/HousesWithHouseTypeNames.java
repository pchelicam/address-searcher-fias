package ru.pchelicam.entities.dao;

import javax.persistence.*;

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

    public HousesWithHouseTypeNames(Long objectId, String objectGUID, String houseNum, String houseTypeName) {
        this.objectId = objectId;
        this.objectGUID = objectGUID;
        this.houseNum = houseNum;
        this.houseTypeName = houseTypeName;
    }

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    public String getObjectGUID() {
        return objectGUID;
    }

    public void setObjectGUID(String objectGUID) {
        this.objectGUID = objectGUID;
    }

    public String getHouseNum() {
        return houseNum;
    }

    public void setHouseNum(String houseNum) {
        this.houseNum = houseNum;
    }

    public String getHouseTypeName() {
        return houseTypeName;
    }

    public void setHouseTypeName(String houseTypeName) {
        this.houseTypeName = houseTypeName;
    }

}
