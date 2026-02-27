package ru.zeker.application.domain.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
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
@Table(name = "application", indexes = {
        @Index(name = "idx_propertyMembershipsId", columnList = "propertyMembershipsId", unique = false),

})
public class Application extends BaseEntity {
    @Column(name = "property_memberships_id", nullable = false)
    private UUID propertyMembershipsId;
    @Size(min = 3, max = 100, message = "Title must be between {min} and {max} characters")
    @Column(name = "title", nullable = false, length = 100)
    String title;
    @Size(min = 3, max = 1000, message = "Comment must be between {min} and {max} characters")
    @Column(name = "comment", nullable = false, length = 1000)
    String comment;
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    Status status;


}
