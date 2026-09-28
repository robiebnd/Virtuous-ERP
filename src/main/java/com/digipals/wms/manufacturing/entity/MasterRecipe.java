package com.digipals.wms.manufacturing.entity;
import com.digipals.wms.common.entity.BaseEntity;
import com.digipals.wms.products.Product;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.util.*;
@Entity @Table(name="mfg_master_recipes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class MasterRecipe extends BaseEntity {
 @Column(name="recipe_number",nullable=false,unique=true) private String recipeNumber;
 @ManyToOne(optional=false) @JoinColumn(name="product_id") private Product product;
 @Column(name="plant_code",nullable=false) private String plantCode;
 @Column(nullable=false) private String status;
 @OneToMany(mappedBy="recipe",cascade=CascadeType.ALL,orphanRemoval=true) @Builder.Default private List<RecipeOperation> operations=new ArrayList<>();
}