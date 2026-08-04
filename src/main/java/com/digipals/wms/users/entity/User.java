package com.digipals.wms.users.entity;

import com.digipals.wms.common.entity.BaseEntity;
import com.digipals.wms.warehouse.entity.Warehouse;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class User extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    private String firstName;

    private String lastName;

    private String phoneNumber;

    @Builder.Default
    private Boolean accountLocked = false;

    @Builder.Default
    private Boolean enabled = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "default_warehouse_id")
    private Warehouse defaultWarehouse;

    @Builder.Default
    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private Set<UserRole> userRoles = new HashSet<>();

    public String getFullName() {
    return (firstName == null ? "" : firstName)
            + " "
            + (lastName == null ? "" : lastName);
}
}