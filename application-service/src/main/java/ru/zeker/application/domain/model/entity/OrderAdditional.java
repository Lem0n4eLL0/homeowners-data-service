package ru.zeker.application.domain.model.entity;

import jakarta.persistence.*;
import java.util.Objects;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;
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
    private UUID additionalServiceId;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class oEffectiveClass = o instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : o.getClass(); Class thisEffectiveClass = this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Application user = (Application ) o;
        return getId() != null && Objects.equals(getId(), user.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

}
