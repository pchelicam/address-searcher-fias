package ru.pchelicam.entities.dao;

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

@AllArgsConstructor
@Getter
@Setter
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

}
