package ru.zeker.homeowners.domain.model.entity;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import io.hypersistence.utils.hibernate.type.range.PostgreSQLRangeType;
import io.hypersistence.utils.hibernate.type.range.Range;
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
import org.hibernate.annotations.Type;
import org.hibernate.proxy.HibernateProxy;
import ru.zeker.common.dto.model.ServiceDetails;
import ru.zeker.common.model.BaseEntity;
import ru.zeker.homeowners.domain.model.enums.PaidStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(
        name = "accruals",
        indexes = {
                @Index(name = "idx_accruals_personal_account", columnList = "personal_account_id"),
                @Index(name = "idx_accruals_period", columnList = "period")
        }
)
public class Accrual extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "personal_account_id", nullable = false)
    private PersonalAccount personalAccount;

    /**
     * PostgreSQL tsrange
     */
    @Type(PostgreSQLRangeType.class)
    @Column(name = "period", columnDefinition = "daterange")
    private Range<LocalDate> period;

    @Column(name = "total_sum", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalSum;

    @Column(name = "paid_amount", nullable = false, precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal paidAmount = BigDecimal.ZERO;

    @Type(JsonBinaryType.class)
    @Column(name = "services_details", columnDefinition = "jsonb")
    private Map<String, ServiceDetails> servicesDetails;

    public PaidStatus getPaidStatus() {
        if (paidAmount.compareTo(BigDecimal.ZERO) == 0) {
            return PaidStatus.NOT_PAID;
        }
        if (paidAmount.compareTo(totalSum) < 0) {
            return PaidStatus.PARTIALLY_PAID;
        }
        return PaidStatus.PAID;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy proxy
                ? proxy.getHibernateLazyInitializer().getPersistentClass()
                : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy proxy
                ? proxy.getHibernateLazyInitializer().getPersistentClass()
                : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Accrual that = (Accrual) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy proxy
                ? proxy.getHibernateLazyInitializer().getPersistentClass().hashCode()
                : getClass().hashCode();
    }
}