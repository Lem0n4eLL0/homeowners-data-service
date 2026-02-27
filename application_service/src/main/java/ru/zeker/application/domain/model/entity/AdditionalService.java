package ru.zeker.application.domain.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.zeker.common.model.BaseEntity;

@ToString(onlyExplicitlyIncluded = true)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "additional_service")
public class AdditionalService extends BaseEntity {
    @Size(min = 3, max = 100, message = "Title must be between {min} and {max} characters")
    @Column(name = "title", nullable = false, length = 100)
    String title;
    @Size(min = 3, max = 1000, message = "Description must be between {min} and {max} characters")
    @Column(name = "description", nullable = false,  length = 1000)
    String description;
    @Column(name = "price", nullable = false)
    int price;


}
