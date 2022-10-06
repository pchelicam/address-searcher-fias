package ru.pchelicam.entities.dao;

import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SqlResultSetMapping;

@SqlResultSetMapping(
        name = "apartmentsWithApartmentTypeNamesMapping",
        classes = {
                @ConstructorResult(
                        targetClass = ApartmentsWithApartmentTypeNames.class,
                        columns = {
                                @ColumnResult(name = "object_id", type = Long.class),
                                @ColumnResult(name = "object_guid", type = String.class),
                                @ColumnResult(name = "apart_number", type = String.class),
                                @ColumnResult(name = "apartment_type_name", type = String.class)
                        }
                )
        }
)
@NamedNativeQuery(
        name = "ApartmentsWithApartmentTypeNames.getApartmentsWithApartmentTypeNames",
        query = "SELECT ap.object_id, ro.object_guid, ap.apart_number, apt.apartment_type_name\n" +
                "FROM {h-schema}apartments ap\n" +
                "JOIN {h-schema}reestr_objects ro\n" +
                "ON ap.object_id = ro.object_id\n" +
                "JOIN {h-schema}adm_hierarchy ah\n" +
                "ON ap.object_id = ah.object_id\n" +
                "JOIN {h-schema}apartment_types apt\n" +
                "ON ap.apart_type = apt.apartment_type_id\n" +
                "WHERE ap.region_code = :regionCode\n" +
                "AND ro.region_code = :regionCode\n" +
                "AND ah.region_code = :regionCode\n" +
                "AND ah.parent_object_id = :parentObjectId\n" +
                "ORDER BY ap.apart_number",
        resultSetMapping = "apartmentsWithApartmentTypeNamesMapping",
        resultClass = ApartmentsWithApartmentTypeNames.class
)

@Entity
public class ApartmentsWithApartmentTypeNames {

    @Id
    @Column(name = "object_id")
    private Long objectId;

    @Column(name = "object_guid")
    private String objectGUID;

    @Column(name = "apart_number")
    private String apartmentNumber;

    @Column(name = "apartment_type_name")
    private String apartmentTypeName;

    public ApartmentsWithApartmentTypeNames(Long objectId, String objectGUID, String apartmentNumber, String apartmentTypeName) {
        this.objectId = objectId;
        this.objectGUID = objectGUID;
        this.apartmentNumber = apartmentNumber;
        this.apartmentTypeName = apartmentTypeName;
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

    public String getApartmentNumber() {
        return apartmentNumber;
    }

    public void setApartmentNumber(String apartmentNumber) {
        this.apartmentNumber = apartmentNumber;
    }

    public String getApartmentTypeName() {
        return apartmentTypeName;
    }

    public void setApartmentTypeName(String apartmentTypeName) {
        this.apartmentTypeName = apartmentTypeName;
    }

}
