package ru.zeker.homeowners.domain.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.proxy.HibernateProxy;
import ru.zeker.common.model.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@ToString(onlyExplicitlyIncluded = true)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "meter_history_values", indexes = {
        @Index(name = "idx_meter_history_values_meter_id", columnList = "meter_id"),
        @Index(name = "idx_meter_history_values_date", columnList = "date"),
        @Index(name = "uk_meter_history_values_meter_date",
                columnList = "meter_id, date", unique = true)
})
public class MeterHistoryValue extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "meter_id", nullable = false)
    @ToString.Include
    private Meter meter;

    @Column(name = "value", nullable = false, precision = 15, scale = 4)
    private BigDecimal value;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        MeterHistoryValue user = (MeterHistoryValue) o;
        return getId() != null && Objects.equals(getId(), user.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
