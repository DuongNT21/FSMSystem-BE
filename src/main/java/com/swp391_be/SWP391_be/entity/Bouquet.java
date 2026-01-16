package com.swp391_be.SWP391_be.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Bouquet extends BaseEntity{
    private String name;
    private int status;
    private float price;

    @OneToMany(mappedBy = "bouquet")
    private List<BouquetImage> images;

    @OneToMany(mappedBy = "bouquet")
    private List<BouquetsMaterial> bouquetsMaterials;
}
