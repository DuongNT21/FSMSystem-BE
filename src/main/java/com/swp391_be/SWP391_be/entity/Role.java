package com.swp391_be.SWP391_be.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Role extends BaseEntity {
    private String roleName;

    @OneToOne(mappedBy = "role")
    private Employee employee;

    @OneToMany(mappedBy = "role")
    private List<User> users;
}
