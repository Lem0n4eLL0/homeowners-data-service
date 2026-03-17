package ru.zeker.application.domain.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
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
@Table(name = "application", indexes = {
        @Index(name = "idx_propertyMembershipsId", columnList = "propertyMembershipsId", unique = false),

})
public class Application extends BaseEntity {
    @Column(name = "account_id", nullable = false)
    private UUID accountId;
    @Column(name = "property_id", nullable = false)
    private UUID propertyId;
    @Size(min = 3, max = 100, message = "Title must be between {min} and {max} characters")
    @Column(name = "title", nullable = false, length = 100)
    private  String title;
    @Size(min = 3, max = 1000, message = "Comment must be between {min} and {max} characters")
    @Column(name = "comment", nullable = false, length = 1000)
    private String comment;
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

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
