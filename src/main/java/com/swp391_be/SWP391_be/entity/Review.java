package com.swp391_be.SWP391_be.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Review extends BaseEntity {
    private String title;
    private String content;
    private float rating;

    @ManyToOne
    @JoinColumn(name = "boutquet_id", nullable = false)
    @JsonIgnore
    private Bouquet bouquet;
}
