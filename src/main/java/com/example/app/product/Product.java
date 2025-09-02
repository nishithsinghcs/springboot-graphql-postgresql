
package com.example.app.product;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "products")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private Boolean avail; // available?

    @Column(nullable = false, updatable = false)
    private String createdBy;

    @Column(nullable = false, updatable = false)
    private Instant createdDate;

    @Column(nullable = false)
    private String modifiedBy;

    @Column(nullable = false)
    private Instant modifiedDate;

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        if (createdBy == null) createdBy = "system";
        if (modifiedBy == null) modifiedBy = createdBy;
        this.createdDate = now;
        this.modifiedDate = now;
        if (avail == null) avail = Boolean.TRUE;
    }

    @PreUpdate
    public void preUpdate() {
        this.modifiedDate = Instant.now();
        if (modifiedBy == null) modifiedBy = "system";
    }
}
