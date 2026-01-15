package com.swp391_be.SWP391_be.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Roles extends BaseEntity {
    private String roleName;

    @OneToOne(mappedBy = "role")
    private Employee employee;
}
