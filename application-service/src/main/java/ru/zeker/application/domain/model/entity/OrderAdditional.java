package ru.zeker.application.domain.model.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.zeker.application.domain.model.enums.Status;
import ru.zeker.common.model.BaseEntity;

import java.util.UUID;
@ToString(onlyExplicitlyIncluded = true)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "order_additional_service", indexes = {
        @Index(name = "idx_propertyMembershipsId", columnList = "propertyMembershipsId", unique = false),

})
public class OrderAdditional extends BaseEntity {

    @Column(name = "account_id", nullable = false)
    private UUID accountId;
    @Column(name = "property_id", nullable = false)
    private UUID propertyId;

    @Column(name = "additional_service_id", nullable = false)
    private UUID additional_service_id;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    Status status;

}
