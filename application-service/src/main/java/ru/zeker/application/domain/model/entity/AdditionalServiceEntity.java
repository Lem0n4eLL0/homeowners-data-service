package ru.zeker.application.domain.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import java.util.Objects;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;
import ru.zeker.common.model.BaseEntity;

@ToString(onlyExplicitlyIncluded = true)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "additional_service")
public class AdditionalServiceEntity extends BaseEntity {
    @Size(min = 3, max = 100, message = "Title must be between {min} and {max} characters")
    @Column(name = "title", nullable = false, length = 100)
    private String title;
    @Size(min = 3, max = 1000, message = "Description must be between {min} and {max} characters")
    @Column(name = "description", nullable = false,  length = 1000)
    private String description;
    @Column(name = "price", nullable = false)
    private int price;

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
