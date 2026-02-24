package com.swp391_be.SWP391_be.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

import jakarta.persistence.Column;
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
public class BouquetImage extends BaseEntity {
    private String image;
    
    @Column(name = "public_id")
    private String publicId;


    @ManyToOne
    @JoinColumn(name = "bouquet_id", nullable = false)
    @JsonIgnore
    private Bouquet bouquet;
}
